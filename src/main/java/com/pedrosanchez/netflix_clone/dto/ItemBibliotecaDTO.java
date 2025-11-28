package com.pedrosanchez.netflix_clone.dto;

import java.time.LocalDateTime;

public record ItemBibliotecaDTO(
        Long movieId,
        String titulo,
        String imagenUrl,
        Long cantidadComprada,
        LocalDateTime ultimaFechaCompra
) {}
