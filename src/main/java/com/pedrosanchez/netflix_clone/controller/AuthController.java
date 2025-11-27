package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.dto.UserRegisterDTO;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // REGISTRO DE USUARIO NORMAL (ROLE_USER)
    @PostMapping("/registro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO dto) {

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("El usuario ya existe");
        }

        User newUser = new User(
                dto.getUsername(),
                passwordEncoder.encode(dto.getPassword())
        );

        // Fecha de registro automático
        newUser.setMemberSince(LocalDate.now().toString());

        // Rol del usuario normal
        newUser.getRoles().add("ROLE_USER");

        User savedUser = userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }


    // NUEVO ENDPOINT: DEVOLVER USUARIO ACTUAL + ROLES
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("username", auth.getName());
        data.put("roles", auth.getAuthorities());

        return ResponseEntity.ok(data);
    }
}
