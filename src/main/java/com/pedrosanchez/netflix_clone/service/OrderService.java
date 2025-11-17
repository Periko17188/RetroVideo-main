package com.pedrosanchez.netflix_clone.service;

import com.pedrosanchez.netflix_clone.model.*;
import com.pedrosanchez.netflix_clone.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    // Crea una compra ficticia a partir del carrito del usuario
    public Order createOrder(User user, List<Movie> movies) {
        double total = movies.size() * 5.99; // precio ficticio
        Order order = new Order(user, LocalDateTime.now(), total, movies);
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(user); // vacía el carrito
        return savedOrder;
    }

    // Devuelve todas las compras de un usuario
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
}
