package com.pedrosanchez.netflix_clone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// DTO para crear o actualizar una película desde el frontend
@Getter
@Setter
public class MovieRequestDTO {

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @NotBlank(message = "La sinopsis no puede estar vacía")
    private String sinopsis;

    @NotNull(message = "El año no puede ser nulo")
    private Integer anio;

    @NotBlank(message = "La imagen no puede estar vacía")
    private String imagenUrl;

    @NotNull(message = "El rating no puede ser nulo")
    private Double rating;

    @NotNull(message = "Debes enviar al menos un género")
    private List<Long> genreIds;
}
