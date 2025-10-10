package com.example.proyecto.Models.Dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluacionGuardarDto {
    private Long actividadId;
    private Long inscripcionId;
    private Long rubricaId;
    private List<Detalle> detalles;
    private Double total;
}
