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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario al que pertenece el carrito
    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Película añadida al carrito
    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    // Precio simulado de la película
    @NonNull
    @Column(nullable = false)
    private Double price;

    // Cantidad de unidades de esta película
    @Column(nullable = false)
    private Integer quantity = 1;
}