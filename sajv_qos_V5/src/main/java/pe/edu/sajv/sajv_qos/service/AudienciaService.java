package pe.edu.sajv.sajv_qos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.model.Audiencia;
import pe.edu.sajv.sajv_qos.dto.AudienciaCalendarioDTO;
import pe.edu.sajv.sajv_qos.model.Sala;
import pe.edu.sajv.sajv_qos.model.Usuario;
import pe.edu.sajv.sajv_qos.repository.AudienciaRepository;
import pe.edu.sajv.sajv_qos.repository.SalaRepository;
import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class AudienciaService {

    private final AudienciaRepository audienciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SalaRepository salaRepository;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public List<AudienciaCalendarioDTO> listarCalendario(String nombreUsuario) {

        // Obtener al usuario autenticado
        Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario)
            .orElseThrow(() -> new AccessDeniedException(
                "No se encontró el usuario autenticado."
            ));

        Integer rol = usuario.getRol();

        List<Audiencia> audiencias;

        // ADMINISTRADOR: consulta todas las sedes
        if (Integer.valueOf(1).equals(rol)) {

            audiencias = audienciaRepository
                .findByEstadoOrderByFechaAscHoraInicioAsc(1);

        } 
        // OPERADOR Y CONSULTA: solo su sede asignada
        else if (Integer.valueOf(2).equals(rol)
                || Integer.valueOf(3).equals(rol)) {

            if (usuario.getSede() == null) {
                throw new AccessDeniedException(
                    "El usuario no tiene una sede asignada."
                );
            }

            Integer sedeId = usuario.getSede().getId();

            audiencias = audienciaRepository
                .findBySala_Sede_IdAndEstadoOrderByFechaAscHoraInicioAsc(
                    sedeId,
                    1
                );

        } else {

            throw new AccessDeniedException(
                "El usuario no tiene un rol autorizado."
            );
        }

        // Convertir las audiencias al formato que consume FullCalendar
        return audiencias.stream()
            .filter(a -> a.getFecha() != null)
            .filter(a -> a.getHoraInicio() != null)
            .filter(a -> a.getHoraFin() != null)
            .map(this::convertirDTO)
            .toList();
    }

    private AudienciaCalendarioDTO convertirDTO(Audiencia audiencia) {

        LocalDateTime inicio = LocalDateTime.of(
            audiencia.getFecha(),
            audiencia.getHoraInicio()
        );

        LocalDateTime fin = LocalDateTime.of(
            audiencia.getFecha(),
            audiencia.getHoraFin()
        );

        String nombreSala = audiencia.getSala() != null
            ? audiencia.getSala().getDescripcion()
            : "Sin sala";

        String nombreSede = audiencia.getSala() != null
                && audiencia.getSala().getSede() != null
            ? audiencia.getSala().getSede().getDescripcion()
            : "Sin sede";

        return new AudienciaCalendarioDTO(
            audiencia.getId(),
            "Expediente " + audiencia.getNroExpediente(),
            inicio,
            fin,
            audiencia.getNroExpediente(),
            nombreSala,
            nombreSede,
            audiencia.getResponsable(),
            audiencia.getEstado(),
            audiencia.getSituacion(),
            audiencia.getSala() != null
                ? audiencia.getSala().getId() : null,
            audiencia.getSala() != null
                && audiencia.getSala().getSede() != null
                ? audiencia.getSala().getSede().getId() : null,
            audiencia.getCoHost(),
            audiencia.getTipo(),
            audiencia.getComentarios()
        );
    }

    @Transactional
    public Audiencia registrar(
            String nombreUsuario,
            String nroExpediente,
            Integer sedeId,
            Integer salaId,
            String responsable,
            String coHost,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Integer tipo,
            String comentarios) {

        // 1. Obtener usuario autenticado
        Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario)
            .orElseThrow(() -> new AccessDeniedException(
                "No se encontró el usuario autenticado."
            ));

        Integer rol = usuario.getRol();

        // 2. Validar permisos
        if (!Integer.valueOf(1).equals(rol)
                && !Integer.valueOf(2).equals(rol)) {
            throw new AccessDeniedException(
                "No tienes permisos para registrar audiencias."
            );
        }

        // 3. Validar campos obligatorios
        if (nroExpediente == null || nroExpediente.isBlank()) {
            throw new IllegalArgumentException(
                "El número de expediente es obligatorio."
            );
        }

        if (responsable == null || responsable.isBlank()) {
            throw new IllegalArgumentException(
                "El responsable es obligatorio."
            );
        }

        if (salaId == null || fecha == null
                || horaInicio == null || horaFin == null || tipo == null) {
            throw new IllegalArgumentException(
                "Completa todos los campos obligatorios."
            );
        }

        if (nroExpediente.length() > 20 || responsable.length() > 80
                || (coHost != null && coHost.length() > 80)
                || (comentarios != null && comentarios.length() > 200)) {
            throw new IllegalArgumentException(
                "Uno o más campos superan la longitud permitida."
            );
        }

        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException(
                "La hora de fin debe ser posterior a la hora de inicio."
            );
        }

        if (tipo < 1 || tipo > 3) {
            throw new IllegalArgumentException(
                "El tipo de audiencia seleccionado no es válido."
            );
        }

        // 4. Determinar la sede autorizada
        Integer sedeAutorizada;

        if (Integer.valueOf(1).equals(rol)) {

            // Administrador: utiliza la sede seleccionada
            if (sedeId == null) {
                throw new IllegalArgumentException(
                    "Debes seleccionar una sede."
                );
            }

            sedeAutorizada = sedeId;

        } else {

            // Operador: utiliza su sede registrada en la BD
            if (usuario.getSede() == null) {
                throw new AccessDeniedException(
                    "El operador no tiene una sede asignada."
                );
            }

            sedeAutorizada = usuario.getSede().getId();
        }

        // 5. Validar que la sala pertenezca a la sede autorizada
        boolean salaValida = salaRepository
            .existsByIdAndSede_IdAndEstado(salaId, sedeAutorizada, 1);

        if (!salaValida) {
            throw new AccessDeniedException(
                "La sala seleccionada no pertenece a tu sede autorizada o no está activa."
            );
        }

        Sala sala = salaRepository.findById(salaId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró la sala seleccionada."
            ));

        // 6. Validar cruce de horarios
        boolean existeCruce = audienciaRepository.existeCruce(
    salaId, fecha, horaFin, horaInicio);

        if (existeCruce) {
            throw new IllegalArgumentException(
                "Ya existe una audiencia programada en esa sala y horario."
            );
        }

        // 7. Crear audiencia
        Audiencia audiencia = new Audiencia();

        audiencia.setNroExpediente(nroExpediente.trim());
        audiencia.setSala(sala);
        audiencia.setResponsable(responsable.trim());
        audiencia.setCoHost(coHost != null && !coHost.isBlank()
            ? coHost.trim() : null);
        audiencia.setFecha(fecha);
        audiencia.setHoraInicio(horaInicio);
        audiencia.setHoraFin(horaFin);
        audiencia.setTipo(tipo);
        audiencia.setComentarios(comentarios != null && !comentarios.isBlank()
            ? comentarios.trim() : null);

        // Datos controlados por el sistema
        audiencia.setUsuario(usuario);
        audiencia.setEstado(1);
        audiencia.setSituacion(1);

        Audiencia guardada = audienciaRepository.save(audiencia);
        auditoriaService.registrar("AUDIENCIAS", "CREAR",
            "Se registró la audiencia " + guardada.getNroExpediente()
                + " (ID " + guardada.getId() + ") en la sala "
                + sala.getDescripcion() + ".");
        return guardada;
    }

    @Transactional
    public Audiencia editar(
            String nombreUsuario,
            Integer audienciaId,
            String nroExpediente,
            Integer sedeId,
            Integer salaId,
            String responsable,
            String coHost,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Integer tipo,
            String comentarios) {

        // 1. Obtener usuario autenticado
        Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario)
            .orElseThrow(() -> new AccessDeniedException(
                "No se encontró el usuario autenticado."
            ));

        // 2. Buscar audiencia
        Audiencia audiencia = audienciaRepository.findById(audienciaId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró la audiencia."
            ));

        // 3. Validar permisos y sede
        validarAccesoAudiencia(usuario, audiencia);

        // 4. No permitir editar audiencias canceladas
        if (Integer.valueOf(4).equals(audiencia.getSituacion())
                || !Integer.valueOf(1).equals(audiencia.getEstado())) {
            throw new IllegalArgumentException(
                "No se puede editar una audiencia cancelada o inactiva."
            );
        }

        // 5. Validar campos
        validarDatosAudiencia(
            nroExpediente, salaId, responsable, coHost,
            fecha, horaInicio, horaFin, tipo, comentarios
        );

        // 6. Determinar sede autorizada
        Integer sedeAutorizada;

        if (Integer.valueOf(1).equals(usuario.getRol())) {
            if (sedeId == null) {
                throw new IllegalArgumentException(
                    "Debes seleccionar una sede."
                );
            }
            sedeAutorizada = sedeId;
        } else {
            sedeAutorizada = usuario.getSede().getId();
        }

        // 7. Validar que la sala esté activa y pertenezca a la sede
        boolean salaValida = salaRepository
            .existsByIdAndSede_IdAndEstado(salaId, sedeAutorizada, 1);

        if (!salaValida) {
            throw new AccessDeniedException(
                "La sala seleccionada no pertenece a la sede autorizada o no está activa."
            );
        }

        Sala sala = salaRepository.findById(salaId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró la sala seleccionada."
            ));

        // 8. Validar cruce, excluyendo la audiencia actual
        boolean existeCruce = audienciaRepository.existeCruceAlEditar(
            salaId, fecha, horaFin, horaInicio, audienciaId
        );

        if (existeCruce) {
            throw new IllegalArgumentException(
                "Ya existe otra audiencia programada en esa sala y horario."
            );
        }

        // 9. Actualizar datos
        audiencia.setNroExpediente(nroExpediente.trim());
        audiencia.setSala(sala);
        audiencia.setResponsable(responsable.trim());
        audiencia.setCoHost(
            coHost != null && !coHost.isBlank() ? coHost.trim() : null
        );
        audiencia.setFecha(fecha);
        audiencia.setHoraInicio(horaInicio);
        audiencia.setHoraFin(horaFin);
        audiencia.setTipo(tipo);
        audiencia.setComentarios(
            comentarios != null && !comentarios.isBlank()
                ? comentarios.trim() : null
        );

        // No modificar usuario, estado ni situación.
        Audiencia actualizada = audienciaRepository.save(audiencia);
        auditoriaService.registrar("AUDIENCIAS", "ACTUALIZAR",
            "Se actualizaron los datos de la audiencia "
                + actualizada.getNroExpediente() + " (ID "
                + actualizada.getId() + ").");
        return actualizada;
    }

    @Transactional
    public void cancelar(
            String nombreUsuario,
            Integer audienciaId) {

        Usuario usuario = usuarioRepository.findByUsuario(nombreUsuario)
            .orElseThrow(() -> new AccessDeniedException(
                "No se encontró el usuario autenticado."
            ));

        Audiencia audiencia = audienciaRepository.findById(audienciaId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se encontró la audiencia."
            ));

        // Validar rol y sede
        validarAccesoAudiencia(usuario, audiencia);

        if (!Integer.valueOf(1).equals(audiencia.getEstado())
                || Integer.valueOf(4).equals(audiencia.getSituacion())) {
            throw new IllegalArgumentException(
                "La audiencia ya está cancelada o inactiva."
            );
        }

        // Cancelación lógica: conservar el registro
        audiencia.setSituacion(4);

        audienciaRepository.save(audiencia);
        auditoriaService.registrar("AUDIENCIAS", "CANCELAR",
            "Se canceló la audiencia " + audiencia.getNroExpediente()
                + " (ID " + audiencia.getId() + ").");
    }


    private void validarAccesoAudiencia(
            Usuario usuario,
            Audiencia audiencia) {

        Integer rol = usuario.getRol();

        // Solo Administrador y Operador pueden modificar
        if (!Integer.valueOf(1).equals(rol)
                && !Integer.valueOf(2).equals(rol)) {
            throw new AccessDeniedException(
                "No tienes permisos para modificar audiencias."
            );
        }

        // Administrador puede gestionar cualquier sede
        if (Integer.valueOf(1).equals(rol)) {
            return;
        }

        // Operador: validar sede asignada
        if (usuario.getSede() == null
                || audiencia.getSala() == null
                || audiencia.getSala().getSede() == null
                || !usuario.getSede().getId().equals(
                    audiencia.getSala().getSede().getId()
                )) {
            throw new AccessDeniedException(
                "No tienes autorización para gestionar audiencias de otra sede."
            );
        }
    }

    private void validarDatosAudiencia(
            String nroExpediente,
            Integer salaId,
            String responsable,
            String coHost,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Integer tipo,
            String comentarios) {

        if (nroExpediente == null || nroExpediente.isBlank()
                || salaId == null
                || responsable == null || responsable.isBlank()
                || fecha == null || horaInicio == null
                || horaFin == null || tipo == null) {
            throw new IllegalArgumentException(
                "Completa todos los campos obligatorios."
            );
        }

        if (nroExpediente.trim().length() > 20
                || responsable.trim().length() > 80
                || (coHost != null && coHost.length() > 80)
                || (comentarios != null && comentarios.length() > 200)) {
            throw new IllegalArgumentException(
                "Uno o más campos superan la longitud permitida."
            );
        }

        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException(
                "La hora de fin debe ser posterior a la hora de inicio."
            );
        }

        if (tipo < 1 || tipo > 3) {
            throw new IllegalArgumentException(
                "El tipo de audiencia seleccionado no es válido."
            );
        }
    }
}