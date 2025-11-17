package com.pedrosanchez.netflix_clone.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

// Entidad que representa una película en la base de datos.
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "pelicula")
public class Movie {

    // Identificador único autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String titulo;

    @Column(length = 1000)
    @NonNull
    private String sinopsis;

    @NonNull
    private Integer anio;

    @NonNull
    private String imagenUrl;

    @NonNull
    private Double rating;

    // Una película puede tener múltiples géneros.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> generos = new HashSet<>();

    // Metodo auxiliar para añadir un género a la película
    public void addGenre(Genre genre) {
        this.generos.add(genre);
    }
}