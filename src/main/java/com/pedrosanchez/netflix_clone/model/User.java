package com.pedrosanchez.netflix_clone.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// Entidad que representa un usuario de la aplicación.
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "APP_USER")
public class User {

    // Identificador único autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String username;
    @NonNull
    private String password;
    @NonNull
    private String role;
}