package com.examen.pedidos.exception;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(Long id) {
        super("No existe un pedido con el ID " + id);
    }
}
