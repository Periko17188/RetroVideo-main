package com.pedrosanchez.netflix_clone.service;

import com.pedrosanchez.netflix_clone.dto.FavoriteDTO;
import java.util.List;

public interface FavoriteService {
    List<FavoriteDTO> getFavoritosPorUsuario(Long userId);

    void toggleFavorito(Long userId, Long movieId);

    List<Long> getFavoritoIds(Long userId);
}
