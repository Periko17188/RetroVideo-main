package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.model.*;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import com.pedrosanchez.netflix_clone.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    // Finaliza la compra ficticia del carrito
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        List<Movie> movies = cartService.getCartItems(user)
                .stream()
                .map(CartItem::getMovie)
                .collect(Collectors.toList());

        Order order = orderService.createOrder(user, movies);

        return ResponseEntity.ok("Compra realizada con éxito. ID pedido: " + order.getId());
    }

    // Muestra todas las compras del usuario
    @GetMapping
    public List<Order> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return orderService.getOrdersByUser(user);
    }
}