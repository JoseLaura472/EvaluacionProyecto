package com.example.proyecto.Models.Entity;

import com.example.proyecto.config.AuditoriaConfig;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "evaluacion_detalle")
@Setter @Getter
public class EvaluacionDetalle extends AuditoriaConfig{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluacionDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evaluacion")
    private Evaluacion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rubrica_criterio")
    private RubricaCriterio rubricaCriterio;

    private double puntaje;
    private int porcentaje;
    private double ponderado;
}
