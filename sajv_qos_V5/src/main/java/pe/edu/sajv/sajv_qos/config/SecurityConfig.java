
package pe.edu.sajv.sajv_qos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // ==========================================
    // 1. CODIFICADOR DE CONTRASEÑAS
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // El filtro debe ejecutarse solo dentro de Spring Security, no dos veces.
    @Bean
    public FilterRegistrationBean<CambioContrasenaFilter> deshabilitarRegistroServlet(
            CambioContrasenaFilter filtro) {
        FilterRegistrationBean<CambioContrasenaFilter> registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    // ==========================================
    // 2. CONFIGURACIÓN DE SEGURIDAD
    // ==========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, CambioContrasenaFilter cambioContrasenaFilter) throws Exception {

        http

            // ==================================
            // AUTORIZACIÓN DE SOLICITUDES
            // ==================================

            .authorizeHttpRequests(auth -> auth

                // ----------------------------------
                // 1. RECURSOS PÚBLICOS
                // ----------------------------------

                .requestMatchers(
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/vendor/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()


                // ----------------------------------
                // 2. DASHBOARD
                // ----------------------------------

                .requestMatchers("/mi-contrasena").authenticated()

                .requestMatchers("/dashboard")
                    .hasAnyRole(
                        "ADMINISTRADOR",
                        "OPERADOR",
                        "CONSULTA"
                    )


                // ----------------------------------
                // 3. USUARIOS
                // ----------------------------------

                .requestMatchers("/usuarios/**")
                    .hasRole("ADMINISTRADOR")


                // ----------------------------------
                // 4. SEDES
                // ----------------------------------

                .requestMatchers("/sedes/**")
                    .hasRole("ADMINISTRADOR")


                // ----------------------------------
                // 5. AUDITORÍA
                // ----------------------------------

                .requestMatchers("/auditoria/**")
                    .hasRole("ADMINISTRADOR")


                // ----------------------------------
                // 6. SALAS JUDICIALES
                // ----------------------------------

                // Consulta de salas por sede
                .requestMatchers(
                    HttpMethod.GET,
                    "/salas/por-sede/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR"
                )

                // Administración del catálogo de salas
                .requestMatchers("/salas/**")
                    .hasRole("ADMINISTRADOR")


                // ----------------------------------
                // 7. AUDIENCIAS - VISTA
                // ----------------------------------

                .requestMatchers(
                    HttpMethod.GET,
                    "/audiencias"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR",
                    "CONSULTA"
                )


                // ----------------------------------
                // 8. AUDIENCIAS - CONSULTAS API
                // ----------------------------------

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/audiencias/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR",
                    "CONSULTA"
                )


                // ----------------------------------
                // 9. AUDIENCIAS - REGISTRO
                // ----------------------------------

                .requestMatchers(
                    HttpMethod.POST,
                    "/audiencias/**",
                    "/api/audiencias/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR"
                )


                // ----------------------------------
                // 10. AUDIENCIAS - ACTUALIZACIÓN
                // ----------------------------------

                .requestMatchers(
                    HttpMethod.PUT,
                    "/audiencias/**",
                    "/api/audiencias/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR"
                )

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/audiencias/**",
                    "/api/audiencias/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR"
                )


                // ----------------------------------
                // 11. AUDIENCIAS - ELIMINACIÓN
                // ----------------------------------

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/audiencias/**",
                    "/api/audiencias/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR"
                )


                // ----------------------------------
                // 12. CONTROL QoS
                // ----------------------------------

                // Credenciales: solo administrador
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/qos/equipos/*/credenciales"
                ).hasRole("ADMINISTRADOR")

                // Consultas QoS: los tres perfiles
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/qos/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR",
                    "CONSULTA"
                )

                // La pantalla de control manual solo está disponible para administrador y operador.
                .requestMatchers(
                    HttpMethod.GET,
                    "/qos/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR"
                )

                // Operaciones manuales y demás modificaciones
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/qos/**",
                    "/qos/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR"
                )

                .requestMatchers(
                    "/api/qos/**",
                    "/qos/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR"
                )

                // ----------------------------------
                // 13. MONITOREO DE RED
                // ----------------------------------

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/monitoreo/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR",
                    "CONSULTA"
                )

                .requestMatchers(
                    HttpMethod.GET,
                    "/monitoreo/**"
                ).hasAnyRole(
                    "ADMINISTRADOR",
                    "OPERADOR",
                    "CONSULTA"
                )

                .requestMatchers("/monitoreo/**")
                    .hasAnyRole(
                        "ADMINISTRADOR",
                        "OPERADOR"
                    )


                // ----------------------------------
                // 14. RESTO DE SOLICITUDES
                // ----------------------------------

                .anyRequest().authenticated()
            )


            // ==================================
            // AUTENTICACIÓN
            // ==================================

            .formLogin(form -> form

                .loginPage("/login")

                .loginProcessingUrl("/login")

                .usernameParameter("username")

                .passwordParameter("password")

                .defaultSuccessUrl("/dashboard", true)

                .failureUrl("/login?error")

                .permitAll()
            )


            // ==================================
            // CIERRE DE SESIÓN
            // ==================================

            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl("/login?logout")

                .invalidateHttpSession(true)

                .clearAuthentication(true)

                .deleteCookies("JSESSIONID")

                .permitAll()
            )


            // ==================================
            // ADMINISTRACIÓN DE SESIONES
            // ==================================

            .sessionManagement(session -> session

                .sessionFixation(fixation ->
                    fixation.migrateSession()
                )
            );


        http.addFilterAfter(cambioContrasenaFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
