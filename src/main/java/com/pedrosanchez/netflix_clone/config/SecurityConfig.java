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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .userDetailsService(jpaUserDetailsService)
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                // Permitir H2 console
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                .authorizeHttpRequests(auth -> auth

                        // Archivos estáticos y frontend
                        .requestMatchers("/", "/index.html", "/styles.css", "/script.js",
                                "/static/**", "/css/**", "/js/**", "/images/**",
                                "/h2-console/**")
                        .permitAll()

                        // Registro y login
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/registro").permitAll()

                        // Endpoints públicos (sin login)
                        .requestMatchers(HttpMethod.GET, "/api/v1/peliculas", "/api/v1/generos").permitAll()

                        // Endpoint para obtener datos del usuario actual
                        .requestMatchers("/api/v1/me").authenticated()

                        // Endpoint para eliminar el usuario actual
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/usuarios/me").hasRole("USER")

                        // ADMIN: CRUD películas + backups
                        .requestMatchers(HttpMethod.POST, "/api/v1/peliculas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/peliculas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/peliculas/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // USER: carrito y pedidos
                        .requestMatchers("/api/v1/cart/**", "/api/v1/orders/**").hasRole("USER")

                        // El resto requiere autenticación
                        .anyRequest().authenticated()
                )

                // HTTP Basic
                .httpBasic(Customizer.withDefaults())

                // Logout
                .logout(logout -> logout.permitAll().logoutSuccessUrl("/?logout"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
