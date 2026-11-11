
package pe.edu.sajv.sajv_qos.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import pe.edu.sajv.sajv_qos.model.Sala;
import pe.edu.sajv.sajv_qos.model.Sede;
import pe.edu.sajv.sajv_qos.model.Usuario;

import pe.edu.sajv.sajv_qos.repository.SalaRepository;
import pe.edu.sajv.sajv_qos.repository.SedeRepository;
import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;
import pe.edu.sajv.sajv_qos.repository.AudienciaRepository;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;
    private final SedeRepository sedeRepository;
    private final UsuarioRepository usuarioRepository;
    private final AudienciaRepository audienciaRepository;
    private final AuditoriaService auditoriaService;

    private static final Integer ADMINISTRADOR = 1;
    private static final Integer OPERADOR = 2;

    private static final Integer ACTIVO = 1;
    private static final Integer INACTIVO = 0;


    // =====================================================
    // LISTAR TODAS LAS SALAS
    // =====================================================

    @Transactional(readOnly = true)
    public List<Sala> SalaSel() {

        return salaRepository.findAllByOrderByDescripcionAsc();
    }


    // =====================================================
    // LISTAR SALAS SEGÚN SEDE Y USUARIO AUTENTICADO
    // MÉTODO UTILIZADO POR AUDIENCIAS
    // =====================================================

    @Transactional(readOnly = true)
    public List<Sala> listarPorSede(
            Integer sedeId,
            String nombreUsuario) {

        if (sedeId == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar una sede."
            );
        }

        Usuario usuario = obtenerUsuario(nombreUsuario);

        Integer rol = usuario.getRol();

        // Administrador: puede consultar cualquier sede
        if (ADMINISTRADOR.equals(rol)) {

            return salaRepository
                .findBySede_IdAndEstadoOrderByDescripcionAsc(
                    sedeId, ACTIVO
                );
        }

        // Operador: solamente puede consultar su sede
        if (OPERADOR.equals(rol)) {

            validarSedeOperador(usuario, sedeId);

            return salaRepository
                .findBySede_IdAndEstadoOrderByDescripcionAsc(
                    sedeId, ACTIVO
                );
        }

        throw new AccessDeniedException(
            "No tiene permisos para consultar las salas."
        );
    }


    // =====================================================
    // LISTAR SALAS PARA GESTIÓN
    // ADMINISTRADOR: TODAS
    // OPERADOR: SU SEDE
    // =====================================================

    @Transactional(readOnly = true)
    public List<Sala> listarParaGestion(String nombreUsuario) {

        Usuario usuario = obtenerUsuario(nombreUsuario);

        if (ADMINISTRADOR.equals(usuario.getRol())) {

            return salaRepository.findAllByOrderByDescripcionAsc();
        }

        if (OPERADOR.equals(usuario.getRol())) {

            if (usuario.getSede() == null) {
                throw new AccessDeniedException(
                    "El operador no tiene una sede asignada."
                );
            }

            return salaRepository.findBySede_IdOrderByDescripcionAsc(
                usuario.getSede().getId()
            );
        }

        throw new AccessDeniedException(
            "No tiene permisos para administrar salas."
        );
    }


    // =====================================================
    // LISTAR SEDES DISPONIBLES SEGÚN USUARIO
    // =====================================================

    @Transactional(readOnly = true)
    public List<Sede> listarSedesDisponibles(String nombreUsuario) {

        Usuario usuario = obtenerUsuario(nombreUsuario);

        // Administrador: todas las sedes activas
        if (ADMINISTRADOR.equals(usuario.getRol())) {

            return sedeRepository.findAll()
                .stream()
                .filter(sede -> ACTIVO.equals(sede.getEstado()))
                .sorted(Comparator.comparing(
                    Sede::getDescripcion,
                    String.CASE_INSENSITIVE_ORDER
                ))
                .toList();
        }

        // Operador: únicamente su sede
        if (OPERADOR.equals(usuario.getRol())) {

            if (usuario.getSede() == null) {
                throw new AccessDeniedException(
                    "El operador no tiene una sede asignada."
                );
            }

            Sede sede = usuario.getSede();

            if (!ACTIVO.equals(sede.getEstado())) {
                throw new IllegalArgumentException(
                    "La sede asignada al operador está inactiva."
                );
            }

            return List.of(sede);
        }

        throw new AccessDeniedException(
            "No tiene permisos para administrar salas."
        );
    }


    // =====================================================
    // REGISTRAR SALA
    // =====================================================

    @Transactional
    public void registrar(
            Sala sala,
            String nombreUsuario) {

        Usuario usuario = obtenerUsuario(nombreUsuario);

        validarPermisoGestion(usuario);
        validarDatos(sala);

        Integer sedeId = sala.getSede().getId();

        Sede sede = sedeRepository.findById(sedeId)
            .orElseThrow(() -> new IllegalArgumentException(
                "La sede seleccionada no existe."
            ));

        validarAccesoSede(usuario, sedeId);

        if (!ACTIVO.equals(sede.getEstado())) {
            throw new IllegalArgumentException(
                "No se puede registrar una sala en una sede inactiva."
            );
        }

        String descripcion = sala.getDescripcion().trim();

        // Validar duplicidad
        if (salaRepository.existsBySede_IdAndDescripcionIgnoreCase(
                sedeId, descripcion)) {

            throw new IllegalArgumentException(
                "Ya existe una sala con esa descripción en la sede seleccionada."
            );
        }

        sala.setDescripcion(descripcion);
        sala.setSede(sede);
        sala.setEstado(ACTIVO);

        Sala guardada = salaRepository.save(sala);
        auditoriaService.registrar("SALAS", "CREAR",
            "Se registró la sala " + guardada.getDescripcion()
                + " (ID " + guardada.getId() + ") en la sede "
                + sede.getDescripcion() + ".");
    }


    // =====================================================
    // EDITAR SALA
    // =====================================================

    @Transactional
    public void editar(
            Integer id,
            Sala datos,
            String nombreUsuario) {

        Usuario usuario = obtenerUsuario(nombreUsuario);

        validarPermisoGestion(usuario);
        validarDatos(datos);

        Sala sala = salaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "La sala que intenta editar no existe."
            ));

        validarAccesoSede(usuario, sala.getSede().getId());

        if (!ACTIVO.equals(sala.getEstado())) {
            throw new IllegalArgumentException(
                "No se puede editar una sala inactiva."
            );
        }

        Integer nuevaSedeId = datos.getSede().getId();

        validarAccesoSede(usuario, nuevaSedeId);

        Sede nuevaSede = sedeRepository.findById(nuevaSedeId)
            .orElseThrow(() -> new IllegalArgumentException(
                "La sede seleccionada no existe."
            ));

        if (!ACTIVO.equals(nuevaSede.getEstado())) {
            throw new IllegalArgumentException(
                "No se puede asignar una sala a una sede inactiva."
            );
        }

        String descripcion = datos.getDescripcion().trim();

        // Validar duplicidad excluyendo la sala actual
        if (salaRepository
                .existsBySede_IdAndDescripcionIgnoreCaseAndIdNot(
                    nuevaSedeId,
                    descripcion,
                    id
                )) {

            throw new IllegalArgumentException(
                "Ya existe otra sala con esa descripción en la sede seleccionada."
            );
        }

        // Si cambia de sede, verificar que no tenga audiencias
        boolean cambiaSede = !sala.getSede().getId().equals(nuevaSedeId);

        if (cambiaSede &&
            audienciaRepository.existsBySala_IdAndEstado(id, ACTIVO)) {

            throw new IllegalArgumentException(
                "No se puede cambiar la sede de una sala que tiene audiencias registradas."
            );
        }

        sala.setDescripcion(descripcion);
        sala.setSede(nuevaSede);

        salaRepository.save(sala);
        auditoriaService.registrar("SALAS", "ACTUALIZAR",
            "Se actualizaron los datos de la sala " + sala.getDescripcion()
                + " (ID " + sala.getId() + ").");
    }


    // =====================================================
    // DESACTIVAR SALA
    // =====================================================

    @Transactional
    public void desactivar(
            Integer id,
            String nombreUsuario) {

        Usuario usuario = obtenerUsuario(nombreUsuario);

        validarPermisoGestion(usuario);

        Sala sala = salaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "La sala que intenta desactivar no existe."
            ));

        validarAccesoSede(usuario, sala.getSede().getId());

        if (!ACTIVO.equals(sala.getEstado())) {
            throw new IllegalArgumentException(
                "La sala ya se encuentra desactivada."
            );
        }

        LocalDate fechaActual = LocalDate.now(
            ZoneId.of("America/Lima")
        );

        LocalTime horaActual = LocalTime.now(
            ZoneId.of("America/Lima")
        );

        boolean tieneAudiencias =
            audienciaRepository.tieneAudienciasPendientes(
                id,
                fechaActual,
                horaActual
            );

        if (tieneAudiencias) {
            throw new IllegalArgumentException(
                "No se puede desactivar la sala porque tiene audiencias pendientes, en curso o suspendidas."
            );
        }

        sala.setEstado(INACTIVO);

        salaRepository.save(sala);
        auditoriaService.registrar("SALAS", "DESACTIVAR",
            "Se desactivó la sala " + sala.getDescripcion()
                + " (ID " + sala.getId() + ").");
    }


    // =====================================================
    // ACTIVAR SALA
    // =====================================================

    @Transactional
    public void activar(
            Integer id,
            String nombreUsuario) {

        Usuario usuario = obtenerUsuario(nombreUsuario);

        validarPermisoGestion(usuario);

        Sala sala = salaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "La sala que intenta activar no existe."
            ));

        validarAccesoSede(usuario, sala.getSede().getId());

        if (!INACTIVO.equals(sala.getEstado())) {
            throw new IllegalArgumentException(
                "La sala ya se encuentra activa."
            );
        }

        Sede sede = sedeRepository.findById(
            sala.getSede().getId()
        ).orElseThrow(() -> new IllegalArgumentException(
            "La sede asociada a la sala no existe."
        ));

        if (!ACTIVO.equals(sede.getEstado())) {
            throw new IllegalArgumentException(
                "No se puede activar la sala porque su sede está inactiva."
            );
        }

        sala.setEstado(ACTIVO);

        salaRepository.save(sala);
        auditoriaService.registrar("SALAS", "ACTIVAR",
            "Se activó la sala " + sala.getDescripcion()
                + " (ID " + sala.getId() + ").");
    }


    // =====================================================
    // OBTENER USUARIO
    // =====================================================

    private Usuario obtenerUsuario(String nombreUsuario) {

        return usuarioRepository.findByUsuario(nombreUsuario)
            .orElseThrow(() -> new AccessDeniedException(
                "Usuario no encontrado."
            ));
    }


    // =====================================================
    // VALIDAR PERMISO DE GESTIÓN
    // =====================================================

    private void validarPermisoGestion(Usuario usuario) {

        Integer rol = usuario.getRol();

        if (!ADMINISTRADOR.equals(rol) &&
            !OPERADOR.equals(rol)) {

            throw new AccessDeniedException(
                "No tiene permisos para administrar salas."
            );
        }
    }


    // =====================================================
    // VALIDAR ACCESO A UNA SEDE
    // =====================================================

    private void validarAccesoSede(
            Usuario usuario,
            Integer sedeId) {

        if (sedeId == null) {
            throw new IllegalArgumentException(
                "Debe seleccionar una sede."
            );
        }

        if (ADMINISTRADOR.equals(usuario.getRol())) {
            return;
        }

        if (OPERADOR.equals(usuario.getRol())) {

            validarSedeOperador(usuario, sedeId);

            return;
        }

        throw new AccessDeniedException(
            "No tiene autorización para acceder a esta sede."
        );
    }


    // =====================================================
    // VALIDAR SEDE DEL OPERADOR
    // =====================================================

    private void validarSedeOperador(
            Usuario usuario,
            Integer sedeId) {

        if (usuario.getSede() == null ||
            !sedeId.equals(usuario.getSede().getId())) {

            throw new AccessDeniedException(
                "No tiene autorización para operar en esta sede."
            );
        }
    }


    // =====================================================
    // VALIDAR DATOS DE LA SALA
    // =====================================================

    private void validarDatos(Sala sala) {

        if (sala == null) {
            throw new IllegalArgumentException(
                "Los datos de la sala son obligatorios."
            );
        }

        if (sala.getDescripcion() == null ||
            sala.getDescripcion().isBlank()) {

            throw new IllegalArgumentException(
                "La descripción de la sala es obligatoria."
            );
        }

        if (sala.getDescripcion().trim().length() > 100) {
            throw new IllegalArgumentException(
                "La descripción no puede superar los 100 caracteres."
            );
        }

        if (sala.getSede() == null ||
            sala.getSede().getId() == null) {

            throw new IllegalArgumentException(
                "Debe seleccionar una sede."
            );
        }
    }

}
