
import logging
import re

from pydantic_settings import BaseSettings, SettingsConfigDict
from netmiko import ConnectHandler
from netmiko.exceptions import (
    NetmikoAuthenticationException,
    NetmikoTimeoutException
)


logger = logging.getLogger(__name__)


# =========================================================
# CONFIGURACIÓN GENERAL
# =========================================================

class Settings(BaseSettings):

    app_name: str = "SAJV-QoS Automation"
    app_version: str = "1.2.0"

    # Simulación de operaciones QoS.
    qos_simulation: bool = True

    # Plataforma Netmiko.
    # Por ahora utilizamos Cisco IOS.
    netmiko_device_type: str = "cisco_ios"

    # Tiempos de conexión SSH.
    ssh_timeout: int = 10
    ssh_banner_timeout: int = 15
    ssh_auth_timeout: int = 10

    # Verificación de claves SSH.
    # Activar cuando se haya preparado known_hosts.
    ssh_strict: bool = False
    ssh_system_host_keys: bool = False

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )


settings = Settings()


# =========================================================
# CONEXIÓN CON EQUIPOS CISCO
# =========================================================

class ConexionCisco:

    # =====================================================
    # PLATAFORMAS NETMIKO PERMITIDAS
    # =====================================================

    PLATAFORMAS_PERMITIDAS = {
        "cisco_ios",
        "cisco_xe",
        "cisco_nxos",
        "cisco_xr"
    }

    # =====================================================
    # VALIDAR IDENTIFICADORES CISCO
    # =====================================================

    def validar_identificador(self, valor):

        if not isinstance(valor, str) or not valor.strip():

            raise ValueError(
                "El identificador de configuración está vacío."
            )

        valor = valor.strip()

        # Evita espacios, saltos de línea y caracteres
        # que podrían alterar los comandos CLI.
        if not re.fullmatch(
            r"[A-Za-z0-9_./:-]+",
            valor
        ):

            raise ValueError(
                "Identificador de configuración no válido."
            )

        return valor

    # =====================================================
    # VALIDAR DIRECCIÓN QoS
    # =====================================================

    def validar_direccion(self, direccion):

        if not isinstance(direccion, str):

            raise ValueError(
                "La dirección QoS debe ser input u output."
            )

        direccion = direccion.strip().lower()

        if direccion not in ("input", "output"):

            raise ValueError(
                "Dirección QoS no válida. Use input u output."
            )

        return direccion

    # =====================================================
    # OBTENER PLATAFORMA NETMIKO
    # =====================================================

    def obtener_device_type(self):

        device_type = settings.netmiko_device_type.strip().lower()

        if device_type not in self.PLATAFORMAS_PERMITIDAS:

            raise ValueError(
                "La plataforma Netmiko configurada no está permitida."
            )

        return device_type

    # =====================================================
    # CONSTRUIR PARÁMETROS SSH
    # =====================================================

    def construir_parametros(self, datos):

        if not datos.usuario or not datos.contrasena:

            raise ValueError(
                "Faltan las credenciales SSH del router."
            )

        if not datos.ip:

            raise ValueError(
                "No se ha proporcionado la dirección IP del router."
            )

        if not datos.puerto or not 1 <= datos.puerto <= 65535:

            raise ValueError(
                "El puerto SSH no es válido."
            )

        dispositivo = {
            "device_type": self.obtener_device_type(),
            "host": str(datos.ip),
            "port": datos.puerto,
            "username": datos.usuario,
            "password": datos.contrasena.get_secret_value(),

            "conn_timeout": settings.ssh_timeout,
            "banner_timeout": settings.ssh_banner_timeout,
            "auth_timeout": settings.ssh_auth_timeout,

            "fast_cli": False,

            # Política de verificación SSH.
            "ssh_strict": settings.ssh_strict,
            "system_host_keys": settings.ssh_system_host_keys
        }

        # Contraseña enable opcional.
        if datos.contrasena_enable:

            secret = datos.contrasena_enable.get_secret_value()

            if secret:
                dispositivo["secret"] = secret

        return dispositivo

    # =====================================================
    # ESTABLECER CONEXIÓN SSH
    # =====================================================

    def conectar(self, datos):

        dispositivo = self.construir_parametros(datos)

        conexion = None

        try:

            logger.info(
                "Estableciendo conexión SSH. "
                "Equipo: %s, IP: %s, Plataforma: %s",
                datos.equipo_id,
                datos.ip,
                dispositivo["device_type"]
            )

            conexion = ConnectHandler(**dispositivo)

            # =============================================
            # VALIDAR MODO PRIVILEGIADO
            # =============================================

            # Si el usuario ya ingresa con privilegio 15,
            # normalmente estará directamente en modo enable.

            if not conexion.check_enable_mode():

                if not datos.contrasena_enable:

                    raise RuntimeError(
                        "El usuario SSH no ingresó en modo privilegiado "
                        "y no se proporcionó contraseña enable."
                    )

                conexion.enable()

            if not conexion.check_enable_mode():

                raise RuntimeError(
                    "No se pudo ingresar al modo privilegiado del router."
                )

            logger.info(
                "Conexión SSH establecida correctamente. Equipo: %s",
                datos.equipo_id
            )

            return conexion

        except Exception:

            logger.exception(
                "Error al establecer conexión SSH. Equipo: %s",
                datos.equipo_id
            )

            if conexion:
                self.desconectar(conexion)

            raise

    # =====================================================
    # PROBAR CONEXIÓN SSH
    # =====================================================

    def probar_conexion(self, datos):

        conexion = None

        try:

            # IMPORTANTE:
            # Esta prueba es REAL incluso si qos_simulation=True.
            # No ejecuta cambios de configuración.

            conexion = self.conectar(datos)

            respuesta = conexion.send_command(
                "show clock"
            )

            return {
                "exitoso": True,
                "modo": "REAL",
                "equipo_id": datos.equipo_id,
                "hostname": datos.hostname,
                "ip": str(datos.ip),
                "protocolo": "SSH",
                "estado": "CONECTADO",
                "plataforma": settings.netmiko_device_type,
                "modo_privilegiado": conexion.check_enable_mode(),
                "comando": "show clock",
                "respuesta": respuesta
            }

        except NetmikoAuthenticationException:

            logger.warning(
                "Fallo de autenticación SSH. Equipo: %s",
                datos.equipo_id
            )

            return {
                "exitoso": False,
                "mensaje": "Error de autenticación SSH o enable."
            }

        except NetmikoTimeoutException:

            logger.warning(
                "Tiempo de espera agotado en conexión SSH. Equipo: %s",
                datos.equipo_id
            )

            return {
                "exitoso": False,
                "mensaje": "Tiempo de espera agotado al conectar."
            }

        except Exception:

            logger.exception(
                "Error durante la prueba de conexión. Equipo: %s",
                datos.equipo_id
            )

            return {
                "exitoso": False,
                "mensaje": "Error durante la prueba de conexión."
            }

        finally:

            if conexion:
                self.desconectar(conexion)

    # =====================================================
    # CONSULTAR CONFIGURACIÓN DE INTERFAZ
    # =====================================================

    def consultar_configuracion_interfaz(
        self,
        conexion,
        interfaz
    ):

        interfaz = self.validar_identificador(interfaz)

        return conexion.send_command(
            f"show running-config interface {interfaz}"
        )

    # =====================================================
    # CONSULTAR CLASS-MAPS
    # =====================================================

    def consultar_class_maps(self, conexion):

        return conexion.send_command(
            "show running-config | section ^class-map"
        )

    # =====================================================
    # CONSULTAR POLICY-MAPS
    # =====================================================

    def consultar_policy_maps(self, conexion):

        return conexion.send_command(
            "show running-config | section ^policy-map"
        )

    # =====================================================
    # VERIFICAR EXISTENCIA DE CLASS-MAP
    # =====================================================

    def verificar_class_map(
        self,
        conexion,
        class_map
    ):

        class_map = self.validar_identificador(class_map)

        configuracion = self.consultar_class_maps(conexion)

        patron = (
            rf"^class-map\s+(?:match-all|match-any)\s+"
            rf"{re.escape(class_map)}\s*$"
        )

        return bool(
            re.search(
                patron,
                configuracion,
                re.MULTILINE
            )
        )

    # =====================================================
    # VERIFICAR EXISTENCIA DE POLICY-MAP
    # =====================================================

    def verificar_policy_map(
        self,
        conexion,
        policy_map
    ):

        policy_map = self.validar_identificador(policy_map)

        configuracion = self.consultar_policy_maps(conexion)

        patron = (
            rf"^policy-map\s+{re.escape(policy_map)}\s*$"
        )

        return bool(
            re.search(
                patron,
                configuracion,
                re.MULTILINE
            )
        )

    # =====================================================
    # VERIFICAR CLASS-MAP DENTRO DE POLICY-MAP
    # =====================================================

    def policy_contiene_class(
        self,
        conexion,
        policy_map,
        class_map
    ):

        policy_map = self.validar_identificador(policy_map)
        class_map = self.validar_identificador(class_map)

        configuracion = self.consultar_policy_maps(conexion)

        dentro_policy = False

        for linea in configuracion.splitlines():

            linea = linea.strip()

            # Detectar inicio de policy-map.
            if linea.startswith("policy-map "):

                dentro_policy = (
                    linea == f"policy-map {policy_map}"
                )

                continue

            # Verificar pertenencia de la clase.
            if (
                dentro_policy
                and linea == f"class {class_map}"
            ):

                return True

        return False

    # =====================================================
    # DESCONECTAR EQUIPO
    # =====================================================

    def desconectar(self, conexion):

        if conexion:

            try:

                conexion.disconnect()

                logger.info(
                    "Conexión SSH cerrada correctamente."
                )

            except Exception:

                logger.warning(
                    "No se pudo cerrar correctamente la conexión SSH.",
                    exc_info=True
                )


# =========================================================
# INSTANCIA GLOBAL
# =========================================================

conexion_cisco = ConexionCisco()
