package com.pedrosanchez.netflix_clone.model;

import jakarta.persistence.*;

import lombok.*;

// Entidad que representa un item dentro del carrito de un usuario
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "cart_item")
public class CartItem {

    // Identificador único autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario al que pertenece el carrito
    @NonNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Película añadida al carrito
    @NonNull
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // Precio simulado de la película
    @NonNull
    private Double price;
}