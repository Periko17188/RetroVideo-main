package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/perfil")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<User> getProfile(Authentication auth) {

        // Bloquear administradores (solo USER)
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow();

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(Authentication auth,
                                           @Valid @RequestBody ProfileUpdateRequest dto) {

        // Bloquear administradores
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow();

        // VALIDACIONES MANUALES PERSONALIZADAS
        Map<String, String> errors = new HashMap<>();

        // Email válido: texto@texto.com / .es / .net ...
        if (dto.getEmail() == null ||
                !dto.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            errors.put("email", "Formato de email inválido (ej: usuario@correo.com)");
        }

        int currentYear = java.time.LocalDate.now().getYear();
        if (dto.getBirthYear() != null && dto.getBirthYear() > currentYear) {
            errors.put("birthYear", "El año no puede ser mayor que el año actual (" + currentYear + ")");
        }

        // Si hay errores → devolverlos
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        user.setEmail(dto.email);
        user.setBirthYear(dto.birthYear);
        user.setAddress(dto.address);
        user.setPostalCode(dto.postalCode);

        if (dto.memberSince != null && !dto.memberSince.isBlank()) {
            user.setMemberSince(dto.memberSince);
        }

        return ResponseEntity.ok(userRepository.save(user));
    }

    @Data
    public static class ProfileUpdateRequest {

        @Email(message = "Email inválido")
        private String email;

        @Min(value = 1900, message = "Año mínimo 1900")
        private Integer birthYear;

        private String address;
        private String postalCode;
        private String memberSince;
    }
}
