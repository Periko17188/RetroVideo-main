package com.pedrosanchez.netflix_clone.service;

import com.pedrosanchez.netflix_clone.exception.NotFoundException;
import com.pedrosanchez.netflix_clone.model.CartItem;
import com.pedrosanchez.netflix_clone.model.Movie;
import com.pedrosanchez.netflix_clone.model.Order;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.OrderRepository;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final MovieService movieService;
    private final UserRepository userRepository;

    // Crea una nueva orden a partir del carrito del usuario
    @Transactional
    public Order createOrder(User user, List<Movie> movies) {

        // Los Movie que llegan NO incluyen quantity → NO los usamos directamente
        List<CartItem> cartItems = cartService.getCartItems(user);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        try {

            // Crear orden vacía
            Order order = Order.builder()
                    .user(user)
                    .orderDate(LocalDateTime.now())
                    .totalAmount(0.0)
                    .build();

            List<Movie> moviesToSave = new ArrayList<>();
            double total = 0.0;

            // Convertir cada CartItem en tantas películas como quantity
            for (CartItem item : cartItems) {
                Movie managedMovie = movieService.findById(item.getMovie().getId())
                        .orElseThrow(() -> new NotFoundException(
                                "Película no encontrada con ID: " + item.getMovie().getId()));

                // Añadir *quantity* veces la película a la orden
                for (int i = 0; i < item.getQuantity(); i++) {
                    moviesToSave.add(managedMovie);
                }

                // Acumular precio
                total += item.getPrice() * item.getQuantity();
            }

            // Redondear a 2 decimales
            total = Math.round(total * 100.0) / 100.0;
            order.setTotalAmount(total);

            // Añadir las películas (repetidas según quantity)
            moviesToSave.forEach(order::addMovie);

            // Guardar la orden completa
            Order savedOrder = orderRepository.save(order);

            // ACTUALIZAR ESTADÍSTICAS DEL USUARIO
            int moviesPurchased = moviesToSave.size();

            Integer currentPurchases = user.getTotalPurchases() != null ? user.getTotalPurchases() : 0;
            Integer currentMovies = user.getTotalMovies() != null ? user.getTotalMovies() : 0;

            user.setTotalPurchases(currentPurchases + 1);
            user.setTotalMovies(currentMovies + moviesPurchased);

            userRepository.save(user);

            // Vaciar carrito
            cartService.clearCart(user);

            return savedOrder;

        } catch (Exception e) {
            throw new RuntimeException("Error al crear la orden: " + e.getMessage(), e);
        }
    }

    // Obtiene todas las órdenes de un usuario
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }
}
