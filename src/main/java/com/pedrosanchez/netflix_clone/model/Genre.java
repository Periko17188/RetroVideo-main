package com.pedrosanchez.netflix_clone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

// Entidad que representa un género de película en la base de datos
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "genero")
public class Genre {

    // Identificador único autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String nombre;

    // Relación muchos a muchos con películas
    @ManyToMany(mappedBy = "generos")
    @JsonIgnore
    private Set<Movie> peliculas = new HashSet<>();
}