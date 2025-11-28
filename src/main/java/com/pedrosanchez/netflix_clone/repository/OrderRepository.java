package com.pedrosanchez.netflix_clone.repository;

import com.pedrosanchez.netflix_clone.dto.ItemBibliotecaDTO;
import com.pedrosanchez.netflix_clone.model.Order;
import com.pedrosanchez.netflix_clone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByUserOrderByOrderDateDesc(User user);

    @Query("""
        select new com.pedrosanchez.netflix_clone.dto.ItemBibliotecaDTO(
            m.id,
            m.titulo,
            m.imagenUrl,
            count(m),
            max(o.orderDate)
        )
        from Order o
        join o.movies m
        where o.user.id = :userId
        group by m.id, m.titulo, m.imagenUrl
        order by max(o.orderDate) desc
    """)
    List<ItemBibliotecaDTO> findLibraryByUserId(@Param("userId") Long userId);
}
