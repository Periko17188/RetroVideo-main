package com.pedrosanchez.netflix_clone.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    private String email;
    private Integer birthYear;
    private String address;
    private String postalCode;
    private String memberSince;

    private Integer totalPurchases = 0;
    private Integer totalMovies = 0;
    private Integer totalFavorites = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_favorites", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "movie_id"))
    private Set<Movie> favorites = new HashSet<>();
}