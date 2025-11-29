package com.pedrosanchez.netflix_clone.dto;

import java.util.List;

public record FavoriteDTO(
        Long id,
        String titulo,
        String imagenUrl,
        Integer anio,
        Double rating,
        List<String> generos) {
}
