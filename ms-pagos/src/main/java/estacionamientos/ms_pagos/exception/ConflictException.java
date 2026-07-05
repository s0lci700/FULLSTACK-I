package estacionamientos.ms_pagos.exception;

// Se lanza cuando la operación entra en conflicto con el estado actual del recurso (ej. duplicados).
// Retorna HTTP 409 Conflict.
public class ConflictException extends RuntimeException {
    public ConflictException(String mensaje) {
        super(mensaje);
    }
}
