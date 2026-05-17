package com.examen.pedidos.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {

    private String mensaje;
    private String detalle;
    private LocalDateTime fecha;

    public ErrorResponse(String mensaje, String detalle) {
        this.mensaje = mensaje;
        this.detalle = detalle;
        this.fecha = LocalDateTime.now();
    }
}
