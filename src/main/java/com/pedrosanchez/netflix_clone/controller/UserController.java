package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // El usuario autenticado se elimina a sí mismo
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser(Authentication authentication) {

        // Si no hay sesión, no permito continuar
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Elimino el usuario
        userRepository.delete(user);

        return ResponseEntity.ok().body("Usuario eliminado con éxito");
    }
}

