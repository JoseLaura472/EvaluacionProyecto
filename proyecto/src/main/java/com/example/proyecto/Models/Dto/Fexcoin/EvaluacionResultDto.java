package com.example.proyecto.Models.Dto.Fexcoin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionResultDto {
    private boolean success;
    private String message;
    private Long participanteId;
    private Integer rubricasGuardadas;
    private Double promedioTotal;
}
