package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.exception.NotFoundException;
import com.pedrosanchez.netflix_clone.model.CartItem;
import com.pedrosanchez.netflix_clone.model.Movie;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.MovieRepository;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import com.pedrosanchez.netflix_clone.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    // Añade una película al carrito
    @PostMapping("/add/{movieId}")
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                       @PathVariable Long movieId) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Película no encontrada con ID: " + movieId));

        cartService.addToCart(user, movie);

        return ResponseEntity.ok("Película añadida al carrito");
    }

    // Muestra el contenido del carrito
    @GetMapping
    public List<CartItem> viewCart(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        return cartService.getCartItems(user);
    }

    // Comprueba si una película está en el carrito del usuario
    @GetMapping("/contains/{movieId}")
    public ResponseEntity<?> contains(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long movieId) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        boolean exists = cartService.getCartItems(user)
                .stream()
                .anyMatch(item -> item.getMovie().getId().equals(movieId));

        return ResponseEntity.ok(java.util.Map.of("inCart", exists));
    }

    // Elimina un artículo del carrito
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeItem(@PathVariable Long id) {

        CartItem item = cartService.findById(id)
                .orElseThrow(() -> new NotFoundException("El artículo con ID " + id + " no existe en el carrito"));

        cartService.removeFromCart(id);

        return ResponseEntity.ok("Artículo eliminado del carrito");
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable Long id,
            @RequestParam int quantity) {
        
        if (quantity < 1) {
            return ResponseEntity.badRequest().body("La cantidad debe ser al menos 1");
        }
        
        cartService.updateCartItemQuantity(id, quantity);
        return ResponseEntity.ok("Cantidad actualizada correctamente");
    }
}
