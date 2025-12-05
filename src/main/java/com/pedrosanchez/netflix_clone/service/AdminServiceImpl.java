package com.pedrosanchez.netflix_clone.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrosanchez.netflix_clone.model.*;
import com.pedrosanchez.netflix_clone.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public Resource createBackup(String filePath) throws IOException {
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // Agregar metadatos al backup
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            addToZip(zos, "backup_metadata.txt", 
                   "Backup creado el: " + timestamp + "\n" +
                   "Aplicación: Netflix Clone\n" +
                   "Tipo de backup: Respaldo completo de la base de datos");

            // Backup de películas
            List<Movie> movies = movieRepository.findAll();
            String moviesJson = objectMapper.writeValueAsString(movies);
            addToZip(zos, "peliculas.json", moviesJson);

            // Backup de géneros
            List<Genre> genres = genreRepository.findAll();
            String genresJson = objectMapper.writeValueAsString(genres);
            addToZip(zos, "generos.json", genresJson);

            // Backup de usuarios (excluyendo datos sensibles)
            List<User> users = userRepository.findAll();
            String usersJson = objectMapper.writeValueAsString(users.stream()
                    .map(user -> new UserInfo(user.getId(), user.getUsername(), user.getRoles()))
                    .toList());
            addToZip(zos, "usuarios.json", usersJson);

            // Backup de elementos del carrito
            List<CartItem> cartItems = cartItemRepository.findAll();
            String cartItemsJson = objectMapper.writeValueAsString(cartItems);
            addToZip(zos, "carrito.json", cartItemsJson);

            // Backup de pedidos
            List<Order> orders = orderRepository.findAll();
            String ordersJson = objectMapper.writeValueAsString(orders);
            addToZip(zos, "pedidos.json", ordersJson);
            
            zos.finish();
            
            log.info("Backup guardado exitosamente en: {}", filePath);
            return new FileSystemResource(file);
            
        } catch (Exception e) {
            log.error("Error al crear el backup en {}", filePath, e);
            throw new IOException("Error al generar el respaldo: " + e.getMessage(), e);
        }
    }

    private void addToZip(ZipOutputStream zos, String filename, String content) throws IOException {
        try {
            ZipEntry entry = new ZipEntry(filename);
            zos.putNextEntry(entry);
            zos.write(content.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            log.debug("Archivo '{}' añadido al backup", filename);
        } catch (IOException e) {
            log.error("Error al añadir el archivo '{}' al ZIP: {}", filename, e.getMessage());
            throw e;
        }
    }
    
    // Clase interna para manejar la información segura de usuarios
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class UserInfo {
        private Long id;
        private String username;
        private Set<String> roles;
    }
}
