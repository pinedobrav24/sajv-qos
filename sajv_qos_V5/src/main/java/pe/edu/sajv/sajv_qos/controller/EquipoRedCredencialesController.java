package pe.edu.sajv.sajv_qos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.edu.sajv.sajv_qos.dto.EquipoRedCredencialesDTO;
import pe.edu.sajv.sajv_qos.service.EquipoRedConexionService;
import pe.edu.sajv.sajv_qos.service.EquipoRedCredencialesService;

import java.util.Map;

@RestController
@RequestMapping("/api/qos/equipos")
@RequiredArgsConstructor
public class EquipoRedCredencialesController {

    private final EquipoRedConexionService conexionService;
    private final EquipoRedCredencialesService credencialesService;

    @PutMapping("/{id}/credenciales")
    public ResponseEntity<Map<String, String>> actualizarCredenciales(
            @PathVariable Integer id,
            @Valid @RequestBody EquipoRedCredencialesDTO request) {

        credencialesService.actualizarCredenciales(id, request);

        return ResponseEntity.ok(
            Map.of(
                "estado", "OK",
                "mensaje", "Credenciales SSH actualizadas correctamente."
            )
        );
    }

    @PostMapping("/{id}/probar-conexion")
    public ResponseEntity<Map<String, Object>> probarConexion(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
            conexionService.probarConexion(id)
        );
    }
}