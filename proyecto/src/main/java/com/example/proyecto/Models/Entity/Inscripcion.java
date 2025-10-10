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
@Table(name = "inscripcion")
@Setter @Getter
public class Inscripcion extends AuditoriaConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad") 
    private Actividad actividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_actividad") 
    private CategoriaActividad categoriaActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participante") 
    private Participante participante;
}