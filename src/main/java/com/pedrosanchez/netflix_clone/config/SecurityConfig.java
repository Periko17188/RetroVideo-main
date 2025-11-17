package com.pedrosanchez.netflix_clone.config;

import com.pedrosanchez.netflix_clone.service.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Clase de configuración de seguridad para la aplicación.
// Aquí se definen los permisos, el login, el logout y la codificación de contraseñas.
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;

    // Configura las reglas de seguridad y las rutas protegidas
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(jpaUserDetailsService)
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                // Permite que la consola H2 funcione dentro de un iframe
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**", "/images/**",
                    "/h2-console/**").permitAll()

                    .requestMatchers("/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/registro").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/peliculas", "/api/v1/generos").permitAll()

                    // ADMIN
                    .requestMatchers(HttpMethod.POST, "/api/v1/peliculas").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/peliculas/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/peliculas/**").hasRole("ADMIN")

                    .requestMatchers("/api/v1/cart/**", "/api/v1/orders/**").authenticated()

                    .anyRequest().authenticated()
                )
                // Autenticación por HTTP Basic (para tus fetch con Authorization: Basic ...)
                .httpBasic(Customizer.withDefaults())

                // Cierre de sesión igual que antes
                .logout(logout -> logout.permitAll().logoutSuccessUrl("/?logout"));

        return http.build();
    }

    // Codifica las contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
