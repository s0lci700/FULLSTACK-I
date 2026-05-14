package estacionamientos.ms_pagos.exception;

// Se lanza cuando una operación viola una regla de negocio.
// El dato SÍ existe en la base de datos, pero la operación no está permitida.
// Ejemplo: intentar cobrar un acceso que ya tiene cobro generado.
// Retorna HTTP 400 Bad Request.
public class BusinessException extends RuntimeException {
    public BusinessException(String mensaje) {
        super(mensaje);
    }
}