package com.coopera2075.cooperanki.exception;

// La operación choca con el estado actual de los datos (duplicados, reglas de negocio) -> 409
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
