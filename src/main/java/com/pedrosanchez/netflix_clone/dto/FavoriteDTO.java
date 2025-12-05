package com.pedrosanchez.netflix_clone.dto;

import java.util.List;

// DTO que envío al frontend para representar una película favorita
public record FavoriteDTO(
        Long id,
        String titulo,
        String imagenUrl,
        Integer anio,
        Double rating,
        List<String> generos) {
}
