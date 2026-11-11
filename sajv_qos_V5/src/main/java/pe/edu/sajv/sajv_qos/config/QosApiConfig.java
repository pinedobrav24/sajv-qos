
package pe.edu.sajv.sajv_qos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class QosApiConfig {

    @Bean
    public RestClient qosRestClient(
            @Value("${sajv.qos.api-url}") String apiUrl) {

        return RestClient.builder()
                .baseUrl(apiUrl)
                .messageConverters(converters ->
                        converters.add(
                                new MappingJackson2HttpMessageConverter()
                        )
                )
                .build();
    }
}
