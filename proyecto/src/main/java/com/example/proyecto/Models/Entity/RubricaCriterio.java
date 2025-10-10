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
@Table(name = "rubrica_criterio")
@Setter @Getter
public class RubricaCriterio extends AuditoriaConfig{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRubricaCriterio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rubrica") 
    private Rubrica rubrica;
    
    private String nombre;
    private int porcentaje;
    private String descripcion;
}