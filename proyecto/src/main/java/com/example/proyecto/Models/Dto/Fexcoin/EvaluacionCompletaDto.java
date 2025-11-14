package com.example.proyecto.Models.Dto.Fexcoin;

import java.util.List;

import lombok.Data;

@Data
public class EvaluacionCompletaDto {
    private Long categoriaId;
    private Long participanteId;
    private List<EvaluacionRubricaDto> rubricas;
    
    /**
     * Evaluación de una rúbrica específica
     */
    @Data
    public static class EvaluacionRubricaDto {
        
        private Long rubricaId;
        private List<DetalleDto> detalles;
    }
    
    /**
     * Detalle de un criterio evaluado
     */
    @Data
    public static class DetalleDto {
        
        private Long criterioId;
        private Integer puntaje;
        private String observacion;
    }
}
