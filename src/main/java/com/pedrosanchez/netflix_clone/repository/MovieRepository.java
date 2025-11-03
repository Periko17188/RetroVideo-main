package com.pedrosanchez.netflix_clone.repository;

import com.pedrosanchez.netflix_clone.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repositorio para la entidad Movie. Proporciona métodos CRUD y de consulta.
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
}
