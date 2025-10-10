package com.example.proyecto.Models.Dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardSnapshotDto {
    private ActividadMiniDto actividad;
    private List<CategoriaResumenDto> categorias;
    private List<JuradoMiniDto> jurados;
    private List<FilaDto> filas;
    private ProgresoDto resumenGlobal;

    @Getter
    @Setter
    public static class CategoriaResumenDto {
        private Long id;
        private String nombre;
        private ProgresoDto progreso;
    }

    @Getter
    @Setter
    public static class ProgresoDto {
        private int total;
        private int completadas;
        private double porcentaje;
    }

    @Getter
    @Setter
    public static class JuradoMiniDto {
        private Long id;
        private String nombre;
    }

    @Getter
    @Setter
    public static class FilaDto {
        private Long inscripcionId;
        private Long categoriaId;
        private String participante;
        private String institucion;
        private Map<Long, Double> scores = new HashMap<>(); // juradoId -> total
        private Double promedio;
        private boolean completado;
    }
}
