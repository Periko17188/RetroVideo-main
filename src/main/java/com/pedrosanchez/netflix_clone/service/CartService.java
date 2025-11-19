package com.pedrosanchez.netflix_clone.service;

import com.pedrosanchez.netflix_clone.model.CartItem;
import com.pedrosanchez.netflix_clone.model.Movie;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;

    // Añade una película al carrito del usuario o incrementa la cantidad si ya existe
    @Transactional
    public void addToCart(User user, Movie movie) {
        cartItemRepository.findByUserAndMovie(user, movie).ifPresentOrElse(
            item -> {
                item.setQuantity(item.getQuantity() + 1);
                cartItemRepository.save(item);
            },
            () -> {
                CartItem newItem = new CartItem(user, movie, 5.99);
                cartItemRepository.save(newItem);
            }
        );
    }
    
    @Transactional
    public void updateCartItemQuantity(Long itemId, int quantity) {
        if (quantity <= 0) {
            cartItemRepository.deleteById(itemId);
        } else {
            cartItemRepository.findById(itemId).ifPresent(item -> {
                item.setQuantity(quantity);
                cartItemRepository.save(item);
            });
        }
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

    public Optional<CartItem> findById(Long id) {
        return cartItemRepository.findById(id);
    }

}