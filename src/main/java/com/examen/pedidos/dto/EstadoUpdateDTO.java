package com.examen.pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoUpdateDTO {

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;
}
