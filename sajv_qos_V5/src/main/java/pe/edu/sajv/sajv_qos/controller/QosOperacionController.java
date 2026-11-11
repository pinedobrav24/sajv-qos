package pe.edu.sajv.sajv_qos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pe.edu.sajv.sajv_qos.dto.QosOperacionManualDTO;
import pe.edu.sajv.sajv_qos.dto.QosOperacionResultadoDTO;
import pe.edu.sajv.sajv_qos.dto.QosEstadoDTO;

import pe.edu.sajv.sajv_qos.model.QosOperacion;
import pe.edu.sajv.sajv_qos.model.Usuario;

import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;
import pe.edu.sajv.sajv_qos.service.QosApiClient;
import pe.edu.sajv.sajv_qos.service.QosOperacionService;

@RestController
@RequestMapping("/api/qos/operaciones")
@RequiredArgsConstructor
public class QosOperacionController {

    private final QosOperacionService qosOperacionService;
    private final QosApiClient qosApiClient;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/activar")
    public ResponseEntity<QosOperacionResultadoDTO> activar(
            @Valid @RequestBody QosOperacionManualDTO request,
            Authentication authentication) {

        Usuario usuario = obtenerUsuario(authentication);

        QosOperacion resultado = qosOperacionService.ejecutarManual(
            request.audienciaId(),
            request.equipoId(),
            request.politicaId(),
            1,
            usuario,
            request.motivo()
        );

        return ResponseEntity.ok(convertir(resultado));
    }

    @PostMapping("/desactivar")
    public ResponseEntity<QosOperacionResultadoDTO> desactivar(
            @Valid @RequestBody QosOperacionManualDTO request,
            Authentication authentication) {

        Usuario usuario = obtenerUsuario(authentication);

        QosOperacion resultado = qosOperacionService.ejecutarManual(
            request.audienciaId(),
            request.equipoId(),
            request.politicaId(),
            2,
            usuario,
            request.motivo()
        );

        return ResponseEntity.ok(convertir(resultado));
    }

    private Usuario obtenerUsuario(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                "No existe un usuario autenticado.");
        }

        return usuarioRepository.findByUsuario(authentication.getName())
            .orElseThrow(() ->
                new IllegalStateException(
                    "No se encontró el usuario autenticado."));
    }

    private QosOperacionResultadoDTO convertir(QosOperacion op) {

        return new QosOperacionResultadoDTO(
            op.getId(),
            op.getAudiencia() != null
                ? op.getAudiencia().getId()
                : null,
            op.getEquipo().getId(),
            op.getPolitica().getId(),
            op.getAccion(),
            op.getOrigen(),
            op.getFechaInicio(),
            op.getFechaFin(),
            op.getResultado(),
            op.getMensaje(),
            op.getSalida()
        );
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(qosApiClient.health());
    }

    @GetMapping("/estado")
    public ResponseEntity<QosEstadoDTO> consultarEstado(
            @RequestParam Integer audienciaId,
            @RequestParam Integer equipoId,
            @RequestParam Integer politicaId) {

        QosEstadoDTO estado = qosOperacionService.consultarEstado(
            audienciaId,
            equipoId,
            politicaId
        );

        return ResponseEntity.ok(estado);
    }
}