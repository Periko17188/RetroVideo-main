package com.pedrosanchez.netflix_clone.service;

import com.pedrosanchez.netflix_clone.model.CartItem;
import com.pedrosanchez.netflix_clone.model.Movie;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;

    // Añade una película al carrito del usuario
    public void addToCart(User user, Movie movie) {
        CartItem item = new CartItem(user, movie, 5.99); // precio ficticio
        cartItemRepository.save(item);
    }

    // Devuelve todos los artículos del carrito de un usuario
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    // Elimina un artículo del carrito
    public void removeFromCart(Long id) {
        cartItemRepository.deleteById(id);
    }

    // Vacía el carrito tras una compra
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}