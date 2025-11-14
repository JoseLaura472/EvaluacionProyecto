package com.example.proyecto.Models.Dto.Fexcoin;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilaParticipante {
    private Long participanteId;
    private String participanteName;
    private String institucion;
    private Long categoriaId;
    private String categoriaNombre;
    
    /**
     * ✅ NUEVO: Mapa de totales por jurado
     * Key: ID del jurado
     * Value: Total acumulado de todas las rúbricas
     */
    private Map<Long, EvaluacionTotalJuradoDto> totalesPorJurado;
    
    /**
     * ✅ NUEVO: Promedio final entre todos los jurados
     */
    private Double promedioFinal;
    
    /**
     * Indica si todos los jurados completaron la evaluación
     */
    public boolean isTodosJuradosCompletaron() {
        if (totalesPorJurado == null || totalesPorJurado.isEmpty()) {
            return false;
        }
        return totalesPorJurado.values().stream()
            .allMatch(EvaluacionTotalJuradoDto::isCompleto);
    }
}
