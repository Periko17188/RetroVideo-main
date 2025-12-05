package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.dto.MovieRequestDTO;
import com.pedrosanchez.netflix_clone.exception.NotFoundException;
import com.pedrosanchez.netflix_clone.model.Genre;
import com.pedrosanchez.netflix_clone.model.Movie;
import com.pedrosanchez.netflix_clone.repository.GenreRepository;
import com.pedrosanchez.netflix_clone.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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

    private final MovieService movieService;
    private final GenreRepository genreRepository;

    // Obtiene todas las películas
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.findAll();
    }

    // Obtiene una película por ID
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.findById(id)
                .orElseThrow(() -> new NotFoundException("Película no encontrada con ID: " + id));

        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    // Crea una nueva película usando DTO + Validación
    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody MovieRequestDTO dto) {

        Movie movie = new Movie(
                dto.getTitulo(),
                dto.getSinopsis(),
                dto.getAnio(),
                dto.getImagenUrl(),
                dto.getRating()
        );

        // Buscar géneros
        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(dto.getGenreIds()));

        if (genres.isEmpty()) {
            throw new NotFoundException("Los géneros enviados no existen");
        }

        movie.setGeneros(genres);

        Movie savedMovie = movieService.save(movie);
        return new ResponseEntity<>(savedMovie, HttpStatus.CREATED);
    }

    // Actualiza película usando DTO + Validación
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequestDTO dto
    ) {

        Movie movie = movieService.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la película con ID: " + id));

        movie.setTitulo(dto.getTitulo());
        movie.setSinopsis(dto.getSinopsis());
        movie.setAnio(dto.getAnio());
        movie.setImagenUrl(dto.getImagenUrl());
        movie.setRating(dto.getRating());

        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(dto.getGenreIds()));

        if (genres.isEmpty()) {
            throw new NotFoundException("Los géneros enviados no existen");
        }

        movie.setGeneros(genres);

        Movie updatedMovie = movieService.save(movie);
        return new ResponseEntity<>(updatedMovie, HttpStatus.OK);
    }

    // Elimina una película por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {

        if (!movieService.findById(id).isPresent()) {
            throw new NotFoundException("No existe la película con ID: " + id);
        }

        try {
            movieService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (DataIntegrityViolationException e) {
            // Si entra aquí, es porque la película está comprada o en un carrito
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar esta película porque forma parte del historial de compras de un usuario.");
        }
    }
}