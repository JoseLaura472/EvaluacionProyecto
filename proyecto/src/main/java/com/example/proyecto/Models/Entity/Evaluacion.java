package com.example.proyecto.Models.Entity;

import java.util.ArrayList;
import java.util.List;

import com.example.proyecto.config.AuditoriaConfig;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "evaluacion")
@Getter @Setter
public class Evaluacion extends AuditoriaConfig{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad")
    private Actividad actividad; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscripcion")
    private Inscripcion inscripcion; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jurado")
    private Jurado jurado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rubrica")
    private Rubrica rubrica; 

    private double totalPonderacion;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluacionDetalle> detalles = new ArrayList<>();
}