package com.pedrosanchez.netflix_clone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

// Entidad JPA que representa un género de película en la base de datos.
@Entity
@Table(name = "genero")
public class Genre {

    // Clave primaria autogenerada
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToMany(mappedBy = "generos")
    @JsonIgnore
    private Set<Movie> peliculas = new HashSet<>();

    public Genre() {
    }

    public Genre(String nombre) {
        this.nombre = nombre;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Movie> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(Set<Movie> peliculas) {
        this.peliculas = peliculas;
    }
}