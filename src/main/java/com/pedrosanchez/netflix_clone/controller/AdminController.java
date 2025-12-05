package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.dto.VentasStatsDTO;
import com.pedrosanchez.netflix_clone.model.Order;
import com.pedrosanchez.netflix_clone.repository.OrderRepository;
import com.pedrosanchez.netflix_clone.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final OrderRepository orderRepository;

    private static final String BACKUP_BASE_PATH = "C:\\Users\\perik\\Desktop\\Proyectos\\RetroVideo\\backup";

    // Estadisticas de ventas
    @GetMapping("/ventas")
    public ResponseEntity<VentasStatsDTO> obtenerEstadisticas() {
        // Obtener todos los pedidos
        List<Order> pedidos = orderRepository.findAll();

        // Sumar total
        double totalDinero = pedidos.stream()
                .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount() : 0.0)
                .sum();

        // Contar ventas
        int totalVentas = pedidos.size();

        // Convertir a formato seguro (Mapas)
        List<Map<String, Object>> historialSeguro = pedidos.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("totalAmount", order.getTotalAmount());
            map.put("orderDate", order.getOrderDate());

            if (order.getUser() != null) {
                Map<String, String> userMap = new HashMap<>();
                userMap.put("username", order.getUser().getUsername());
                map.put("user", userMap);
            } else {
                map.put("user", null);
            }

            return map;
        }).collect(Collectors.toList());

        // 5. Devolver el DTO relleno con los datos seguros
        return ResponseEntity.ok(new VentasStatsDTO(totalDinero, totalVentas, historialSeguro));
    }

    // BACKUP
    @PostMapping("/backup")
    public ResponseEntity<Resource> createBackup() throws IOException {
        Path backupDir = Paths.get(BACKUP_BASE_PATH);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "backup_" + timestamp + ".zip";
        String filePath = backupDir.resolve(filename).toString();

        Resource backupFile = adminService.createBackup(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(backupFile);
    }
}