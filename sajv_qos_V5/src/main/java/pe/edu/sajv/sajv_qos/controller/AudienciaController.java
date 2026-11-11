package pe.edu.sajv.sajv_qos.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.dto.AudienciaCalendarioDTO;
import pe.edu.sajv.sajv_qos.service.AudienciaService;

@RestController
@RequestMapping("/api/audiencias")
@RequiredArgsConstructor
public class AudienciaController {

    private final AudienciaService audienciaService;

    @GetMapping("/calendario")
    public List<AudienciaCalendarioDTO> listarCalendario(
            Authentication authentication) {

        return audienciaService.listarCalendario(
            authentication.getName()
        );
    }
}