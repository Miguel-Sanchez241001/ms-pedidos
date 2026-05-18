package com.examen.pedidos.entity;

/**
 * Estados posibles de un pedido.
 * Se almacena como cadena de texto en la BD gracias a @Enumerated(EnumType.STRING).
 */
public enum EstadoPedido {
    REGISTRADO,
    PAGADO,
    ENVIADO,
    CANCELADO
}
