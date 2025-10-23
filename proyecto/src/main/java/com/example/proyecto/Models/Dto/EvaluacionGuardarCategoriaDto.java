package com.example.proyecto.Models.Dto;

import java.util.List;

public record EvaluacionGuardarCategoriaDto(
    Long categoriaId,
    Long participanteId,
    Long rubricaId,
    List<EvaluacionDetalleCategoriaDto> detalles
) {}
