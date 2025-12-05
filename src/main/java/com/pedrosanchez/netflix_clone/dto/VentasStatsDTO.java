package com.pedrosanchez.netflix_clone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

// DTO que envío al frontend con las estadísticas de ventas
@Data
@AllArgsConstructor
public class VentasStatsDTO {
    private Double totalIngresos;
    private Integer cantidadVentas;

    // Usamos Map para asegurar que los datos viajan limpios al frontend
    private List<Map<String, Object>> historial;
}