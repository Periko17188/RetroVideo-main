package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.dto.ItemBibliotecaDTO;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import com.pedrosanchez.netflix_clone.service.BibliotecaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/biblioteca")
@RequiredArgsConstructor
public class BibliotecaController {

    private final BibliotecaService bibliotecaService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ItemBibliotecaDTO>> getMiBiblioteca(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        // Bloquear admin
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }

        // Obtener usuario
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow();

        // Obtener biblioteca del usuario
        List<ItemBibliotecaDTO> items = bibliotecaService.getBibliotecaPorUsuario(user.getId());

        return ResponseEntity.ok(items);
    }
}
