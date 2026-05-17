package com.examen.pedidos.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PedidoRequestDTO {

    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    private String cliente;

    @NotBlank(message = "El correo del cliente es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correoCliente;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombreProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que cero")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser mayor que cero")
    private BigDecimal precioUnitario;
}
