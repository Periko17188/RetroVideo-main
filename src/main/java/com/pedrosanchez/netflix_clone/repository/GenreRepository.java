package com.pedrosanchez.netflix_clone.repository;

import com.pedrosanchez.netflix_clone.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio para la entidad Genre. Proporciona métodos CRUD y de consulta.
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}