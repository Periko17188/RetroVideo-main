package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.dto.MovieRequestDTO;
import com.pedrosanchez.netflix_clone.model.Genre;
import com.pedrosanchez.netflix_clone.model.Movie;
import com.pedrosanchez.netflix_clone.repository.GenreRepository;
import com.pedrosanchez.netflix_clone.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Controlador REST para gestionar operaciones con películas (CRUD)
@RestController
@RequestMapping("/api/v1/peliculas")
@RequiredArgsConstructor
public class MovieController {

    // Servicios y repositorios necesarios para las operaciones sobre películas
    private final MovieService movieService;
    private final GenreRepository genreRepository;

    // Obtiene todas las películas
    @GetMapping
    public List<Movie> getAllMovies() {
        try {
            return movieService.findAll();
        } catch (Exception e) {
            System.err.println("Error al obtener todas las películas: " + e.getMessage());
            return List.of();
        }
    }

    // Obtiene una película por ID
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return movieService.findById(id)
                .map(movie -> new ResponseEntity<>(movie, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Crea una nueva película usando DTO + Validación
    @PostMapping
    public ResponseEntity<?> createMovie(@Valid @RequestBody MovieRequestDTO dto) {

        try {
            Movie movie = new Movie(
                    dto.getTitulo(),
                    dto.getSinopsis(),
                    dto.getAnio(),
                    dto.getImagenUrl(),
                    dto.getRating()
            );

            // Cargar géneros por IDs
            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(dto.getGenreIds()));
            movie.setGeneros(genres);

            Movie savedMovie = movieService.save(movie);
            return new ResponseEntity<>(savedMovie, HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("Error al crear película: " + e.getMessage());
            return new ResponseEntity<>("Error interno del servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualiza película usando DTO + Validación
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequestDTO dto
    ) {
        return movieService.findById(id)
                .map(existingMovie -> {
                    try {
                        existingMovie.setTitulo(dto.getTitulo());
                        existingMovie.setSinopsis(dto.getSinopsis());
                        existingMovie.setAnio(dto.getAnio());
                        existingMovie.setImagenUrl(dto.getImagenUrl());
                        existingMovie.setRating(dto.getRating());

                        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(dto.getGenreIds()));
                        existingMovie.setGeneros(genres);

                        Movie updatedMovie = movieService.save(existingMovie);
                        return new ResponseEntity<>(updatedMovie, HttpStatus.OK);

                    } catch (Exception e) {
                        System.err.println("Error al actualizar película: " + e.getMessage());
                        return new ResponseEntity<>("Error interno del servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Elimina una película por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.err.println("Error al eliminar película con ID " + id + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}