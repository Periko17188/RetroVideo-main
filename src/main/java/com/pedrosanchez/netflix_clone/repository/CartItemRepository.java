package com.pedrosanchez.netflix_clone.repository;

import com.pedrosanchez.netflix_clone.model.CartItem;
import com.pedrosanchez.netflix_clone.model.Movie;
import com.pedrosanchez.netflix_clone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    void deleteByUser(User user);
    Optional<CartItem> findById(Long id);
    Optional<CartItem> findByUserAndMovie(User user, Movie movie);

}
