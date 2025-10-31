package com.example.proyecto.Models.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CronometroDTO {
    private String accion; // "iniciar", "finalizar", "actualizar"
    private Long participanteId;
    private String participanteNombre;
    private Long timestamp;
    private Integer tiempoRestante; // en segundos
    private String estado; // "verde", "amarillo", "rojo"
}
