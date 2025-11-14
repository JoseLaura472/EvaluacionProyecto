package com.example.proyecto.Models.Dto.Fexcoin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa el total acumulado de un jurado para un participante
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionTotalJuradoDto {
    /**
     * ID del jurado
     */
    private Long idJurado;
    
    /**
     * Nombre completo del jurado
     */
    private String nombreJurado;
    
    /**
     * Total de puntos acumulados (suma de todas las rúbricas)
     */
    private Double totalAcumulado;
    
    /**
     * Cantidad de rúbricas evaluadas por este jurado
     */
    private Integer rubricasEvaluadas;
    
    /**
     * Cantidad total de rúbricas en la categoría
     */
    private Integer totalRubricas;
    
    /**
     * Indica si el jurado completó todas las evaluaciones
     */
    public boolean isCompleto() {
        return rubricasEvaluadas != null && totalRubricas != null && 
               rubricasEvaluadas.equals(totalRubricas);
    }
    
    /**
     * Porcentaje de avance
     */
    public double getPorcentajeAvance() {
        if (totalRubricas == null || totalRubricas == 0) return 0.0;
        return (rubricasEvaluadas * 100.0) / totalRubricas;
    }
}
