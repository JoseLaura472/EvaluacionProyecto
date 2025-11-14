package com.example.proyecto.Models.Dto.Fexcoin;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EvaluacionRequestDTO {
    private Long categoriaId;
    private Long participanteId;
    private Long rubricaId;
    private List<DetalleEvaluacionDTO> detalles;
    
    @Getter @Setter
    public static class DetalleEvaluacionDTO {
        private Long criterioId;
        private Double puntaje;
        private String observacion; // opcional
    }
}
