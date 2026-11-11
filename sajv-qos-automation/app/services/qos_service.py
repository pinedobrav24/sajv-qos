from netmiko.exceptions import (
    NetmikoAuthenticationException,
    NetmikoTimeoutException
)

from app.services.conexion import conexion_cisco, settings


class QosService:

    def validar_datos(self, datos):
        conexion_cisco.validar_identificador(datos.interfaz)
        conexion_cisco.validar_identificador(datos.class_map)
        conexion_cisco.validar_identificador(datos.policy_map)
        conexion_cisco.validar_direccion(datos.direccion)

    def politica_aplicada(self, configuracion, policy_map):
        objetivo = f"service-policy output {policy_map}"
        return any(
            linea.strip() == objetivo
            for linea in configuracion.splitlines()
        )

    def otra_politica_aplicada(self, configuracion, policy_map):
        for linea in configuracion.splitlines():
            linea = linea.strip()
            if linea.startswith("service-policy output "):
                actual = linea.split("service-policy output ", 1)[1].strip()
                if actual != policy_map:
                    return actual
        return None

    def verificar_requisitos(self, conexion, datos):
        if not conexion_cisco.verificar_class_map(
            conexion, datos.class_map
        ):
            raise RuntimeError(
                f"No existe la CLASS-MAP {datos.class_map}"
            )

        if not conexion_cisco.verificar_policy_map(
            conexion, datos.policy_map
        ):
            raise RuntimeError(
                f"No existe la POLICY-MAP {datos.policy_map}"
            )

        if not conexion_cisco.policy_contiene_class(
            conexion, datos.policy_map, datos.class_map
        ):
            raise RuntimeError(
                f"La POLICY-MAP {datos.policy_map} no contiene "
                f"la clase {datos.class_map}"
            )

    def simular(self, datos, accion):
        return {
            "exitoso": True,
            "modo": "SIMULACION",
            "accion": accion,
            "audiencia_id": datos.audiencia_id,
            "equipo_id": datos.equipo_id,
            "hostname": datos.hostname,
            "interfaz": datos.interfaz,
            "class_map": datos.class_map,
            "policy_map": datos.policy_map,
            "mensaje": (
                f"Simulación de {accion.lower()} QoS "
                f"para la audiencia {datos.audiencia_id}"
            ),
            "cambio_realizado": False
        }

    def ejecutar(self, datos, accion):
        self.validar_datos(datos)

        if settings.qos_simulation:
            return self.simular(datos, accion)

        conexion = None
        try:
            conexion = conexion_cisco.conectar(datos)
            self.verificar_requisitos(conexion, datos)

            configuracion = (
                conexion_cisco.consultar_configuracion_interfaz(
                    conexion, datos.interfaz
                )
            )
            activa = self.politica_aplicada(
                configuracion, datos.policy_map
            )

            otra = self.otra_politica_aplicada(
                configuracion, datos.policy_map
            )
            if otra:
                raise RuntimeError(
                    f"La interfaz tiene aplicada otra política: {otra}. "
                    "No se realizarán cambios."
                )

            if accion == "ACTIVAR":
                if activa:
                    return {
                        "exitoso": True,
                        "modo": "REAL",
                        "accion": accion,
                        "audiencia_id": datos.audiencia_id,
                        "equipo_id": datos.equipo_id,
                        "interfaz": datos.interfaz,
                        "policy_map": datos.policy_map,
                        "mensaje": "La política ya estaba activa",
                        "cambio_realizado": False
                    }

                salida = conexion.send_config_set([
                    f"interface {datos.interfaz}",
                    f"service-policy output {datos.policy_map}"
                ])
                final = conexion_cisco.consultar_configuracion_interfaz(
                    conexion, datos.interfaz
                )
                if not self.politica_aplicada(final, datos.policy_map):
                    raise RuntimeError(
                        "No se pudo verificar la activación de QoS"
                    )

                return {
                    "exitoso": True,
                    "modo": "REAL",
                    "accion": accion,
                    "audiencia_id": datos.audiencia_id,
                    "equipo_id": datos.equipo_id,
                    "interfaz": datos.interfaz,
                    "policy_map": datos.policy_map,
                    "mensaje": "Política QoS activada correctamente",
                    "cambio_realizado": True,
                    "salida": salida
                }

            if accion == "DESACTIVAR":
                if not activa:
                    return {
                        "exitoso": True,
                        "modo": "REAL",
                        "accion": accion,
                        "audiencia_id": datos.audiencia_id,
                        "equipo_id": datos.equipo_id,
                        "interfaz": datos.interfaz,
                        "policy_map": datos.policy_map,
                        "mensaje": "La política ya estaba desactivada",
                        "cambio_realizado": False
                    }

                salida = conexion.send_config_set([
                    f"interface {datos.interfaz}",
                    f"no service-policy output {datos.policy_map}"
                ])
                final = conexion_cisco.consultar_configuracion_interfaz(
                    conexion, datos.interfaz
                )
                if self.politica_aplicada(final, datos.policy_map):
                    raise RuntimeError(
                        "No se pudo verificar la desactivación de QoS"
                    )

                return {
                    "exitoso": True,
                    "modo": "REAL",
                    "accion": accion,
                    "audiencia_id": datos.audiencia_id,
                    "equipo_id": datos.equipo_id,
                    "interfaz": datos.interfaz,
                    "policy_map": datos.policy_map,
                    "mensaje": "Política QoS desactivada correctamente",
                    "cambio_realizado": True,
                    "salida": salida
                }

            raise ValueError("Acción QoS no válida")

        except NetmikoAuthenticationException:
            return {
                "exitoso": False,
                "modo": "REAL",
                "accion": accion,
                "mensaje": "Error de autenticación SSH"
            }
        except NetmikoTimeoutException:
            return {
                "exitoso": False,
                "modo": "REAL",
                "accion": accion,
                "mensaje": "Tiempo de espera agotado al conectar"
            }
        except Exception :
            return {
                "exitoso": False,
                "modo": "REAL",
                "accion": accion,
                "mensaje": "Error durante la ejecución de la acción QoS"
            }
        finally:
            if conexion:
                conexion_cisco.desconectar(conexion)

    def activar(self, datos):
        return self.ejecutar(datos, "ACTIVAR")

    def desactivar(self, datos):
        return self.ejecutar(datos, "DESACTIVAR")


qos_service = QosService()
