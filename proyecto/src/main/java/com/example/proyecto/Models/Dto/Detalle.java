package com.example.proyecto.Models.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Detalle {
    private Long criterioId;
    private Double puntaje;     // 0..100
    private Integer porcentaje; // redundante
}
