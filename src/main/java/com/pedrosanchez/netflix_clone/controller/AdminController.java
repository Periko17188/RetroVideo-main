package com.pedrosanchez.netflix_clone.controller;

import com.pedrosanchez.netflix_clone.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private static final String BACKUP_BASE_PATH = "C:\\Users\\perik\\Desktop\\Proyectos\\RetroVideo\\backup";

    @PostMapping("/backup")
    public ResponseEntity<Resource> createBackup() throws IOException {
        // Crear directorio si no existe
        Path backupDir = Paths.get(BACKUP_BASE_PATH);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }

        // Generar nombre de archivo con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "backup_" + timestamp + ".zip";
        String filePath = backupDir.resolve(filename).toString();

        // Generar el backup
        Resource backupFile = adminService.createBackup(filePath);
        
        // Configurar headers para la descarga
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(backupFile);
    }
}
