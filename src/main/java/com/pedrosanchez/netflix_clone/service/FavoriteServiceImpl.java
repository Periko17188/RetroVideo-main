package com.pedrosanchez.netflix_clone.service;

import com.pedrosanchez.netflix_clone.dto.FavoriteDTO;
import com.pedrosanchez.netflix_clone.model.Genre;
import com.pedrosanchez.netflix_clone.model.Movie;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.MovieRepository;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteDTO> getFavoritosPorUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return user.getFavorites().stream()
                .map(movie -> new FavoriteDTO(
                        movie.getId(),
                        movie.getTitulo(),
                        movie.getImagenUrl(),
                        movie.getAnio(),
                        movie.getRating(),
                        movie.getGeneros().stream()
                                .map(Genre::getNombre)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleFavorito(Long userId, Long movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));

        if (user.getFavorites().contains(movie)) {
            // Eliminar de favoritos
            user.getFavorites().remove(movie);
            user.setTotalFavorites(user.getTotalFavorites() - 1);
        } else {
            // Añadir a favoritos
            user.getFavorites().add(movie);
            user.setTotalFavorites(user.getTotalFavorites() + 1);
        }

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getFavoritoIds(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return user.getFavorites().stream()
                .map(Movie::getId)
                .collect(Collectors.toList());
    }
}
