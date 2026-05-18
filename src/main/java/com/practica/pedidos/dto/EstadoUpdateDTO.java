package com.examen.pedidos.dto;

import com.examen.pedidos.entity.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoUpdateDTO {

    @NotNull(message = "El estado no puede ser nulo. Valores válidos: REGISTRADO, PAGADO, ENVIADO, CANCELADO")
    private EstadoPedido estado;
}
