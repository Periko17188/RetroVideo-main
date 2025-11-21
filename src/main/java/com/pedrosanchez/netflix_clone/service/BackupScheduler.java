package com.pedrosanchez.netflix_clone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupScheduler {

    private final AdminService adminService;
    private static final String BACKUP_BASE_PATH = "C:\\Users\\perik\\Desktop\\Pedflix-main\\backup";

    /**
     * Programa la generación de copias de seguridad cada 15 minutos
     * Se ejecuta a en punto, a los 15, 30 y 45 minutos de cada hora
     */
    @Scheduled(cron = "0 */15 * * * *")
    public void scheduleBackup() {
        try {
            // Crear directorio si no existe
            Path backupDir = Paths.get(BACKUP_BASE_PATH);
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
                log.info("Directorio de backups automáticos creado: {}", BACKUP_BASE_PATH);
            }

            // Generar nombre de archivo con timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "auto_backup_" + timestamp + ".zip";
            String filePath = backupDir.resolve(filename).toString();

            // Generar el backup
            log.info("Iniciando copia de seguridad automática en: {}", filePath);
            adminService.createBackup(filePath);
            log.info("Copia de seguridad automática completada: {}", filename);

        } catch (IOException e) {
            log.error("Error al generar la copia de seguridad automática", e);
        }
    }
}
