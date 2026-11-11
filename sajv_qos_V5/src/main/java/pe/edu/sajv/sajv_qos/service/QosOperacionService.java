
package pe.edu.sajv.sajv_qos.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.sajv.sajv_qos.dto.QosApiResponseDTO;
import pe.edu.sajv.sajv_qos.dto.QosEstadoDTO;
import pe.edu.sajv.sajv_qos.dto.QosOperacionRequestDTO;

import pe.edu.sajv.sajv_qos.model.Audiencia;
import pe.edu.sajv.sajv_qos.model.EquipoRed;
import pe.edu.sajv.sajv_qos.model.PoliticaQos;
import pe.edu.sajv.sajv_qos.model.QosOperacion;
import pe.edu.sajv.sajv_qos.model.Usuario;
import pe.edu.sajv.sajv_qos.model.QosControlManual;
import pe.edu.sajv.sajv_qos.model.QosModoControl;

import pe.edu.sajv.sajv_qos.repository.AudienciaRepository;
import pe.edu.sajv.sajv_qos.repository.EquipoRedRepository;
import pe.edu.sajv.sajv_qos.repository.PoliticaQosRepository;
import pe.edu.sajv.sajv_qos.repository.QosOperacionRepository;
import pe.edu.sajv.sajv_qos.repository.QosControlManualRepository;

import pe.edu.sajv.sajv_qos.util.CryptoService;

@Service
public class QosOperacionService {

    private static final Logger log =
            LoggerFactory.getLogger(QosOperacionService.class);

    // =====================================================
    // CONSTANTES
    // =====================================================

    private static final int ACCION_ACTIVAR = 1;
    private static final int ACCION_DESACTIVAR = 2;

    private static final int RESULTADO_EXITOSO = 1;
    private static final int RESULTADO_FALLIDO = 2;

    private static final int ORIGEN_MANUAL = 2;

    private static final ZoneId ZONA_LIMA =
            ZoneId.of("America/Lima");

    // =====================================================
    // DEPENDENCIAS
    // =====================================================

    private final AudienciaRepository audienciaRepository;
    private final EquipoRedRepository equipoRepository;
    private final PoliticaQosRepository politicaRepository;
    private final QosOperacionRepository operacionRepository;
    private final QosControlManualRepository controlManualRepository;

    private final QosApiClient qosApiClient;
    private final CryptoService cryptoService;

    // =====================================================
    // CONFIGURACIÓN
    // =====================================================

    @Value("${sajv.qos.simulation:true}")
    private boolean simulation;

    @Value("${sajv.qos.manual.duration-minutes:10}")
    private long duracionManualMinutos;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public QosOperacionService(
            AudienciaRepository audienciaRepository,
            EquipoRedRepository equipoRepository,
            PoliticaQosRepository politicaRepository,
            QosOperacionRepository operacionRepository,
            QosApiClient qosApiClient,
            CryptoService cryptoService,
            QosControlManualRepository controlManualRepository) {

        this.audienciaRepository = audienciaRepository;
        this.equipoRepository = equipoRepository;
        this.politicaRepository = politicaRepository;
        this.operacionRepository = operacionRepository;
        this.qosApiClient = qosApiClient;
        this.cryptoService = cryptoService;
        this.controlManualRepository = controlManualRepository;
    }

    // =====================================================
    // CONSULTAR ESTADO QoS
    // =====================================================

    @Transactional(readOnly = true)
    public QosEstadoDTO consultarEstado(
            Integer audienciaId,
            Integer equipoId,
            Integer politicaId) {

        if (audienciaId == null || audienciaId <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar una audiencia válida.");
        }

        if (equipoId == null || equipoId <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar un equipo de red válido.");
        }

        if (politicaId == null || politicaId <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar una política QoS válida.");
        }

        audienciaRepository.findById(audienciaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La audiencia no existe."));

        equipoRepository.findById(equipoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El equipo de red no existe."));

        politicaRepository.findById(politicaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La política QoS no existe."));

        QosOperacion ultimaOperacion =
                operacionRepository
                        .findTopByEquipo_IdAndPolitica_IdAndResultadoOrderByFechaInicioDescIdDesc(
                                equipoId,
                                politicaId,
                                RESULTADO_EXITOSO)
                        .orElse(null);

        String estado = "INACTIVO";
        Integer ultimaOperacionId = null;

        if (ultimaOperacion != null) {

            ultimaOperacionId = ultimaOperacion.getId();

            if (ultimaOperacion.getAccion() == ACCION_ACTIVAR) {
                estado = "ACTIVO";
            }
        }

        String modo = simulation ? "SIMULACION" : "REAL";

        return new QosEstadoDTO(
                audienciaId,
                equipoId,
                politicaId,
                estado,
                ultimaOperacionId,
                modo
        );
    }

    // =====================================================
    // VALIDAR CONFIGURACIÓN QoS
    // =====================================================

    private void validarConfiguracionQos(
            EquipoRed equipo,
            PoliticaQos politica) {

        if (equipo.getInterfazQos() == null
                || equipo.getInterfazQos().isBlank()) {

            throw new IllegalStateException(
                    "El equipo no tiene configurada la interfaz QoS.");
        }

        if (politica.getClassMap() == null
                || politica.getClassMap().isBlank()) {

            throw new IllegalStateException(
                    "La política no tiene configurado su class-map.");
        }

        if (politica.getPolicyMap() == null
                || politica.getPolicyMap().isBlank()) {

            throw new IllegalStateException(
                    "La política no tiene configurado su policy-map.");
        }

        String direccion = politica.getDireccion();

        if (direccion == null
                || (!direccion.equalsIgnoreCase("input")
                && !direccion.equalsIgnoreCase("output"))) {

            throw new IllegalStateException(
                    "La dirección QoS debe ser input u output.");
        }
    }

    // =====================================================
    // EJECUTAR OPERACIÓN QoS
    // =====================================================

    @Transactional
    public QosOperacion ejecutar(
            Integer audienciaId,
            Integer equipoId,
            Integer politicaId,
            Integer accion,
            Integer origen) {

        validarParametros(accion, origen);

        if (equipoId == null || equipoId <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un equipo de red válido.");
        }

        if (politicaId == null || politicaId <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una política QoS válida.");
        }

        LocalDateTime inicio = LocalDateTime.now(ZONA_LIMA);

        // =================================================
        // 1. RECUPERAR AUDIENCIA
        // =================================================

        Audiencia audiencia = null;

        if (audienciaId != null) {

            audiencia = audienciaRepository.findById(audienciaId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "La audiencia con ID "
                                    + audienciaId
                                    + " no existe."));
        }

        // =================================================
        // 2. RECUPERAR EQUIPO
        // =================================================

        EquipoRed equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El equipo de red no existe."));

        // =================================================
        // 3. RECUPERAR POLÍTICA
        // =================================================

        PoliticaQos politica = politicaRepository.findById(politicaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La política QoS no existe."));

        try {

            log.info(
                    "Iniciando operación QoS. Acción: {}, Equipo: {}, Política: {}",
                    accion,
                    equipo.getId(),
                    politica.getId()
            );

            // =================================================
            // 4. VALIDAR ESTADO DEL EQUIPO
            // =================================================

            if (equipo.getEstado() == null
                    || equipo.getEstado() != 1) {

                throw new IllegalStateException(
                        "El equipo de red se encuentra inactivo.");
            }

            // =================================================
            // 5. VALIDAR ESTADO DE LA POLÍTICA
            // =================================================

            if (politica.getEstado() == null
                    || politica.getEstado() != 1) {

                throw new IllegalStateException(
                        "La política QoS se encuentra inactiva.");
            }

            // =================================================
            // 6. VALIDAR CONFIGURACIÓN DESDE MYSQL
            // =================================================

            validarConfiguracionQos(equipo, politica);

            // =================================================
            // 7. VALIDAR ESTADO GLOBAL DEL ROUTER
            // =================================================

            QosOperacion ultimaOperacionExitosa =
                    operacionRepository
                            .findTopByEquipo_IdAndPolitica_IdAndResultadoOrderByFechaInicioDescIdDesc(
                                    equipoId,
                                    politicaId,
                                    RESULTADO_EXITOSO)
                            .orElse(null);

            boolean qosActiva =
                    ultimaOperacionExitosa != null
                    && ultimaOperacionExitosa.getAccion()
                            == ACCION_ACTIVAR;

            // Evitar activaciones duplicadas.
            if (accion == ACCION_ACTIVAR && qosActiva) {

                throw new IllegalStateException(
                        "La política QoS ya se encuentra activa "
                        + "en este router.");
            }

            // Evitar desactivaciones innecesarias.
            if (accion == ACCION_DESACTIVAR && !qosActiva) {

                throw new IllegalStateException(
                        "No es posible desactivar QoS porque "
                        + "no existe una activación exitosa vigente "
                        + "en este router.");
            }

            // =================================================
            // 8. DESCIFRAR CREDENCIALES SSH
            // =================================================

            String contrasena = cryptoService.decrypt(
                    equipo.getContrasena());

            if (contrasena == null || contrasena.isBlank()) {

                throw new IllegalStateException(
                        "El equipo no tiene una contraseña SSH registrada.");
            }

            // =================================================
            // 9. CONSTRUIR SOLICITUD FASTAPI
            // =================================================

            QosOperacionRequestDTO request =
                    new QosOperacionRequestDTO(
                            audienciaId,
                            equipo.getId(),
                            equipo.getHostname(),
                            equipo.getIp(),
                            equipo.getPuerto(),
                            equipo.getUsuario(),
                            contrasena,
                            null,
                            equipo.getInterfazQos(),
                            politica.getClassMap(),
                            politica.getPolicyMap(),
                            politica.getDireccion()
                                    .toLowerCase(Locale.ROOT)
                    );

            // =================================================
            // 10. EJECUTAR OPERACIÓN FASTAPI
            // =================================================

            QosApiResponseDTO respuesta;

            if (accion == ACCION_ACTIVAR) {

                log.info(
                        "Enviando ACTIVACIÓN QoS. Equipo: {}, Política: {}, Dirección: {}",
                        equipo.getNombre(),
                        politica.getPolicyMap(),
                        politica.getDireccion()
                );

                respuesta = qosApiClient.activar(request);

            } else {

                log.info(
                        "Enviando DESACTIVACIÓN QoS. Equipo: {}, Política: {}, Dirección: {}",
                        equipo.getNombre(),
                        politica.getPolicyMap(),
                        politica.getDireccion()
                );

                respuesta = qosApiClient.desactivar(request);
            }

            if (respuesta == null) {

                throw new IllegalStateException(
                        "FastAPI no devolvió una respuesta.");
            }

            log.info(
                    "Respuesta FastAPI. Exitoso: {}, Modo: {}, Cambio realizado: {}, Mensaje: {}",
                    respuesta.exitoso(),
                    respuesta.modo(),
                    respuesta.cambioRealizado(),
                    respuesta.mensaje()
            );

            // =================================================
            // 11. REGISTRAR RESULTADO
            // =================================================

            int resultado = respuesta.exitoso()
                    ? RESULTADO_EXITOSO
                    : RESULTADO_FALLIDO;

            return registrarOperacion(
                    audiencia,
                    equipo,
                    politica,
                    accion,
                    origen,
                    inicio,
                    resultado,
                    respuesta.mensaje(),
                    respuesta.salida()
            );

        } catch (Exception e) {

            log.error(
                    "Error al ejecutar QoS. Acción: {}, Equipo: {}, Política: {}",
                    accion,
                    equipo.getId(),
                    politica.getId(),
                    e
            );

            String mensaje =
                    "No se pudo completar la operación QoS.";

            if (e instanceof IllegalArgumentException
                    || e instanceof IllegalStateException) {

                mensaje = e.getMessage();
            }

            return registrarOperacion(
                    audiencia,
                    equipo,
                    politica,
                    accion,
                    origen,
                    inicio,
                    RESULTADO_FALLIDO,
                    mensaje,
                    null
            );
        }
    }

    // =====================================================
    // REGISTRAR OPERACIÓN
    // =====================================================

    private QosOperacion registrarOperacion(
            Audiencia audiencia,
            EquipoRed equipo,
            PoliticaQos politica,
            Integer accion,
            Integer origen,
            LocalDateTime inicio,
            Integer resultado,
            String mensaje,
            String salida) {

        QosOperacion operacion = new QosOperacion();

        operacion.setAudiencia(audiencia);
        operacion.setEquipo(equipo);
        operacion.setPolitica(politica);

        operacion.setAccion(accion);
        operacion.setOrigen(origen);

        operacion.setFechaInicio(inicio);
        operacion.setFechaFin(LocalDateTime.now(ZONA_LIMA));

        operacion.setResultado(resultado);
        operacion.setMensaje(mensaje);
        operacion.setSalida(salida);

        return operacionRepository.save(operacion);
    }

    // =====================================================
    // VALIDAR PARÁMETROS
    // =====================================================

    private void validarParametros(
            Integer accion,
            Integer origen) {

        if (accion == null
                || (accion != ACCION_ACTIVAR
                && accion != ACCION_DESACTIVAR)) {

            throw new IllegalArgumentException(
                    "Acción QoS no válida.");
        }

        if (origen == null
                || (origen != 1 && origen != 2)) {

            throw new IllegalArgumentException(
                    "Origen de operación no válido.");
        }
    }

    // =====================================================
    // CONSULTAR ESTADO GLOBAL
    // =====================================================

    @Transactional(readOnly = true)
    public boolean estaActivo(
            Integer equipoId,
            Integer politicaId) {

        if (equipoId == null || equipoId <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar un equipo de red válido.");
        }

        if (politicaId == null || politicaId <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar una política QoS válida.");
        }

        QosOperacion ultimaOperacion =
                operacionRepository
                        .findTopByEquipo_IdAndPolitica_IdAndResultadoOrderByFechaInicioDescIdDesc(
                                equipoId,
                                politicaId,
                                RESULTADO_EXITOSO)
                        .orElse(null);

        return ultimaOperacion != null
                && ultimaOperacion.getAccion() == ACCION_ACTIVAR;
    }

    // =====================================================
    // EJECUTAR INTERVENCIÓN MANUAL
    // =====================================================

    @Transactional
    public QosOperacion ejecutarManual(
            Integer audienciaId,
            Integer equipoId,
            Integer politicaId,
            Integer accion,
            Usuario usuario,
            String motivo) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "No se pudo identificar al usuario autenticado.");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe indicar el motivo de la intervención manual.");
        }

        if (motivo.length() > 500) {
            throw new IllegalArgumentException(
                    "El motivo no puede superar los 500 caracteres.");
        }

        validarParametros(accion, ORIGEN_MANUAL);

        if (equipoId == null || equipoId <= 0
                || politicaId == null || politicaId <= 0) {
            throw new IllegalArgumentException(
                    "Debe indicar un equipo y una política válidos.");
        }

        EquipoRed equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El equipo no existe."));

        PoliticaQos politica = politicaRepository.findById(politicaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La política no existe."));

        // Roles: 1 Administrador, 2 Operador.
        if (usuario.getRol() == null
                || (usuario.getRol() != 1
                && usuario.getRol() != 2)) {

            throw new IllegalStateException(
                    "El usuario no tiene permisos para operar QoS.");
        }

        // El operador solo puede intervenir equipos de su sede.
        if (usuario.getRol() == 2) {

            if (usuario.getSede() == null
                    || equipo.getSede() == null
                    || !usuario.getSede().getId()
                            .equals(equipo.getSede().getId())) {

                throw new IllegalStateException(
                        "No tiene autorización para controlar "
                        + "equipos de otra sede.");
            }
        }

        if (equipo.getEstado() == null
                || equipo.getEstado() != 1) {

            throw new IllegalStateException(
                    "El equipo está inactivo.");
        }

        if (politica.getEstado() == null
                || politica.getEstado() != 1) {

            throw new IllegalStateException(
                    "La política QoS está inactiva.");
        }

        LocalDateTime ahora =
                LocalDateTime.now(ZONA_LIMA);

        QosControlManual control =
                controlManualRepository
                        .findByEquipo_IdAndPolitica_Id(
                                equipoId,
                                politicaId)
                        .orElseGet(QosControlManual::new);

        // =================================================
        // VALIDAR INTERVENCIÓN MANUAL VIGENTE
        // =================================================

        if (Integer.valueOf(1).equals(control.getActivo())
                && control.getFechaFin() != null
                && control.getFechaFin().isAfter(ahora)) {

            throw new IllegalStateException(
                    "Ya existe una intervención manual vigente "
                    + "para este equipo y política.");
        }

        boolean qosActivo =
                estaActivo(equipoId, politicaId);

        Audiencia audiencia = null;

        if (audienciaId != null) {

            audiencia = audienciaRepository.findById(audienciaId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "La audiencia no existe."));
        }

        QosOperacion operacion;

        // =================================================
        // EVITAR REPETIR COMANDOS
        // =================================================

        if ((accion == ACCION_ACTIVAR && qosActivo)
                || (accion == ACCION_DESACTIVAR && !qosActivo)) {

            String mensaje =
                    accion == ACCION_ACTIVAR
                            ? "Intervención manual registrada. "
                              + "QoS ya se encontraba activo."
                            : "Intervención manual registrada. "
                              + "QoS ya se encontraba inactivo.";

            operacion = registrarOperacion(
                    audiencia,
                    equipo,
                    politica,
                    accion,
                    ORIGEN_MANUAL,
                    ahora,
                    RESULTADO_EXITOSO,
                    mensaje,
                    null
            );

        } else {

            operacion = ejecutar(
                    audienciaId,
                    equipoId,
                    politicaId,
                    accion,
                    ORIGEN_MANUAL
            );
        }

        // =================================================
        // TRAZABILIDAD
        // =================================================

        operacion.setUsuario(usuario);
        operacion.setMotivo(motivo);

        operacionRepository.save(operacion);

        // =================================================
        // CREAR INTERVENCIÓN SOLO SI FUE EXITOSA
        // =================================================

        if (operacion.getResultado() == RESULTADO_EXITOSO) {

            if (duracionManualMinutos <= 0) {
                throw new IllegalStateException(
                        "La duración manual debe ser mayor a cero.");
            }

            control.setEquipo(equipo);
            control.setPolitica(politica);

            control.setModo(
                    accion == ACCION_ACTIVAR
                            ? QosModoControl.FORZADO_ACTIVO
                            : QosModoControl.FORZADO_INACTIVO
            );

            control.setMotivo(motivo);
            control.setUsuario(usuario);

            control.setFechaInicio(ahora);
            control.setFechaFin(
                    ahora.plusMinutes(duracionManualMinutos)
            );

            control.setActivo(1);

            controlManualRepository.save(control);

            log.info(
                    "Intervención manual registrada. "
                    + "Equipo: {}, Política: {}, Usuario: {}, Vence: {}",
                    equipoId,
                    politicaId,
                    usuario.getUsuario(),
                    control.getFechaFin()
            );
        }

        return operacion;
    }

    // =====================================================
    // VALIDAR INTERVENCIÓN MANUAL VIGENTE
    // =====================================================

    @Transactional
    public boolean tieneIntervencionManualVigente(
            Integer equipoId,
            Integer politicaId) {

        LocalDateTime ahora =
                LocalDateTime.now(ZONA_LIMA);

        QosControlManual control =
                controlManualRepository
                        .findByEquipo_IdAndPolitica_Id(
                                equipoId,
                                politicaId)
                        .orElse(null);

        if (control == null
                || !Integer.valueOf(1).equals(
                        control.getActivo())) {

            return false;
        }

        // Intervención todavía vigente.
        if (control.getFechaFin() != null
                && control.getFechaFin().isAfter(ahora)) {

            log.info(
                    "Intervención manual vigente. "
                    + "Equipo: {}, Política: {}, Modo: {}, Vence: {}",
                    equipoId,
                    politicaId,
                    control.getModo(),
                    control.getFechaFin()
            );

            return true;
        }

        // =================================================
        // INTERVENCIÓN VENCIDA
        // =================================================

        control.setModo(QosModoControl.AUTOMATICO);
        control.setActivo(0);

        controlManualRepository.save(control);

        log.info(
                "Intervención manual vencida. "
                + "Equipo: {}, Política: {}. "
                + "Se restablece el modo automático.",
                equipoId,
                politicaId
        );

        return false;
    }
}
