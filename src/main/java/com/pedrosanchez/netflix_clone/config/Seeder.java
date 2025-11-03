package com.pedrosanchez.netflix_clone.config;

import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Clase de configuración para insertar usuarios iniciales en la base de datos
@Configuration
public class Seeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.count() == 0) {
                // Codifica la contraseña común para los usuarios iniciales
                String encodedPassword = passwordEncoder.encode("12341234");

                User admin = new User("Pedro", encodedPassword, "ADMIN");
                userRepository.save(admin);

                User client = new User("Gia", encodedPassword, "USER");
                userRepository.save(client);

                System.out.println("Usuarios iniciales creados: Pedro (ADMIN) y Gia (USER)");
            }
        };
    }
}
