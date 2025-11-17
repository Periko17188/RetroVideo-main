package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.dto.UserRegisterDTO;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

// Controlador que gestiona el registro de nuevos usuarios
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Metodo que registra un nuevo usuario en el sistema
    @PostMapping("/registro")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO dto) {

        // Comprueba si el nombre de usuario ya existe
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return new ResponseEntity<>("El usuario ya existe", HttpStatus.BAD_REQUEST);
        }

        // Codifica la contraseña antes de guardar
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // Crea el nuevo usuario usando el DTO
        User newUser = new User(
                dto.getUsername(),
                encodedPassword,
                "USER"
        );

        // Guarda el nuevo usuario en la base de datos
        User savedUser = userRepository.save(newUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}