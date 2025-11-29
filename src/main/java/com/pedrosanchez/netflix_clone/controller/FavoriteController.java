package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.dto.FavoriteDTO;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import com.pedrosanchez.netflix_clone.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Long>> getFavoritosIds(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        // Bloquear admin
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow();

        List<Long> ids = favoriteService.getFavoritoIds(user.getId());
        return ResponseEntity.ok(ids);
    }

    @PostMapping("/{movieId}")
    public ResponseEntity<Void> toggleFavorito(@PathVariable Long movieId, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        // Bloquear admin
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow();

        favoriteService.toggleFavorito(user.getId(), movieId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mis-favoritos")
    public ResponseEntity<List<FavoriteDTO>> getMisFavoritos(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        // Bloquear admin
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow();

        List<FavoriteDTO> favoritos = favoriteService.getFavoritosPorUsuario(user.getId());
        return ResponseEntity.ok(favoritos);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> deleteFavorito(@PathVariable Long movieId, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).build();
        }

        // Bloquear admin
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow();

        // Reutilizar toggle para eliminar
        favoriteService.toggleFavorito(user.getId(), movieId);
        return ResponseEntity.ok().build();
    }
}
