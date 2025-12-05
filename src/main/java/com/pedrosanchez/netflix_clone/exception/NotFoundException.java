package com.pedrosanchez.netflix_clone.exception;

// Excepción personalizada para recursos no encontrados
public class NotFoundException extends RuntimeException {

    public NotFoundException(String mensaje) {
        super(mensaje);
    }
}