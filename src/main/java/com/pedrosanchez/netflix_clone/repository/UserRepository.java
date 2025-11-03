package com.pedrosanchez.netflix_clone.repository;

import com.pedrosanchez.netflix_clone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Repositorio para la entidad User. Proporciona métodos CRUD y de consulta.
public interface UserRepository extends JpaRepository<User, Long> {
    // Metodo para buscar un usuario por su nombre de usuario.
    Optional<User> findByUsername(String username);
}