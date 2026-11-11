
package pe.edu.sajv.sajv_qos.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import pe.edu.sajv.sajv_qos.dto.QosApiResponseDTO;
import pe.edu.sajv.sajv_qos.dto.QosOperacionRequestDTO;
import pe.edu.sajv.sajv_qos.dto.RouterConexionRequestDTO;

@Service
public class QosApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiUrl;

    public QosApiClient(
            RestClient qosRestClient,
            @Value("${sajv.qos.api-url}") String apiUrl) {

        this.restClient = qosRestClient;
        this.apiUrl = apiUrl;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    }

    // ==========================================
    // ACTIVAR QoS
    // ==========================================
    public QosApiResponseDTO activar(
            QosOperacionRequestDTO request) {

        return enviarOperacion("/api/qos/activar", request);
    }

    // ==========================================
    // DESACTIVAR QoS
    // ==========================================
    public QosApiResponseDTO desactivar(
            QosOperacionRequestDTO request) {

        return enviarOperacion("/api/qos/desactivar", request);
    }

    // ==========================================
    // ENVÍO HTTP NATIVO DE JSON A FASTAPI
    // ==========================================
    private QosApiResponseDTO enviarOperacion(
            String endpoint,
            QosOperacionRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "La solicitud QoS no puede ser nula.");
        }

        try {

            // 1. Serializar el DTO a JSON
            String json = objectMapper.writeValueAsString(request);

            System.out.println("========== JSON GENERADO POR SPRING ==========");
                String jsonSeguro = json.replaceAll(
                "(\"(?:contrasena|contrasena_enable)\"\\s*:\\s*\")[^\"]*(\")",
                "$1********$2"
                );

                System.out.println(jsonSeguro);

System.out.println("==============================================");
            // 2. Construir la dirección completa
            URI uri = URI.create(
                    apiUrl.replaceAll("/+$", "") + endpoint
            );

            // 3. Construir la petición HTTP con cuerpo JSON
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            json,
                            StandardCharsets.UTF_8
                    ))
                    .build();

            // 4. Información de diagnóstico sin exponer credenciales
            System.out.println("========== ENVÍO QOS HTTP NATIVO ==========");
            System.out.println("URL: " + uri);
            System.out.println("Método: POST");
            System.out.println("Content-Type: application/json");
            System.out.println("Tamaño JSON: "
                    + json.getBytes(StandardCharsets.UTF_8).length
                    + " bytes");
            System.out.println("===========================================");

            // 5. Enviar petición y recibir respuesta
            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString(
                            StandardCharsets.UTF_8
                    )
            );

            int status = response.statusCode();
            String respuestaJson = response.body();

            System.out.println("========== RESPUESTA FASTAPI =============");
            System.out.println("HTTP Status: " + status);
            System.out.println("Respuesta recibida: "
                    + (respuestaJson != null && !respuestaJson.isBlank()));
            System.out.println("===========================================");

            // 6. Comprobar código HTTP
            if (status < 200 || status >= 300) {

                throw new IllegalStateException(
                        "FastAPI respondió HTTP " + status
                        + ". Detalle: " + respuestaJson
                );
            }

            // 7. Comprobar cuerpo de respuesta
            if (respuestaJson == null || respuestaJson.isBlank()) {

                throw new IllegalStateException(
                        "FastAPI devolvió una respuesta vacía."
                );
            }

            // 8. Convertir respuesta JSON a DTO
            return objectMapper.readValue(
                    respuestaJson,
                    QosApiResponseDTO.class
            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Error al procesar el JSON de la operación QoS.",
                    e
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "No se pudo establecer la comunicación HTTP con FastAPI.",
                    e
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "La comunicación con FastAPI fue interrumpida.",
                    e
            );
        }
    }

    // ==========================================
    // PROBAR CONEXIÓN SSH
    // ==========================================
    public Map<String, Object> probarConexion(
            RouterConexionRequestDTO request) {

        return restClient
                .post()
                .uri("/api/router/conexion")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    // ==========================================
    // VERIFICAR ESTADO DE FASTAPI
    // ==========================================
    public String health() {

        return restClient
                .get()
                .uri("/api/health")
                .retrieve()
                .body(String.class);
    }
}
