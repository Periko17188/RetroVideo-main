package com.pedrosanchez.netflix_clone.service;

import com.pedrosanchez.netflix_clone.dto.ItemBibliotecaDTO;
import com.pedrosanchez.netflix_clone.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BibliotecaService {

    private final OrderRepository orderRepository;

    public List<ItemBibliotecaDTO> getBibliotecaPorUsuario(Long userId) {
        return orderRepository.findLibraryByUserId(userId);
    }
}
