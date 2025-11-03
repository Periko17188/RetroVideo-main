package com.pedrosanchez.netflix_clone.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

// Entidad JPA que representa una película en la base de datos.
@Entity
@Table(name = "pelicula")
public class Movie {

    // Clave primaria autogenerada
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    // Sinopsis de la película (largo hasta 1000 caracteres)
    @Column(length = 1000)
    private String sinopsis;

    private Integer anio;

    private String imagenUrl;

    private Double rating;

    // Una película puede tener múltiples géneros.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> generos = new HashSet<>();

    public Movie() {
    }

    public Movie(String titulo, String sinopsis, Integer anio, String imagenUrl, Double rating) {
        this.titulo = titulo;
        this.sinopsis = sinopsis;
        this.anio = anio;
        this.imagenUrl = imagenUrl;
        this.rating = rating;
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Set<Genre> getGeneros() { return generos; }
    public void setGeneros(Set<Genre> generos) { this.generos = generos; }
    public void addGenre(Genre genre) { this.generos.add(genre); }
}