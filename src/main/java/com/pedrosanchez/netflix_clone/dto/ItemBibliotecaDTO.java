package com.pedrosanchez.netflix_clone.dto;

import java.time.LocalDateTime;

// DTO para mostrar las películas que el usuario ha comprado
public record ItemBibliotecaDTO(
        Long movieId,
        String titulo,
        String imagenUrl,
        Long cantidadComprada,
        LocalDateTime ultimaFechaCompra
) {}
