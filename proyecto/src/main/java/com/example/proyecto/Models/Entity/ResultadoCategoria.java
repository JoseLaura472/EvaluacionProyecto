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
@Table(name = "resultado_categoria")
@Setter @Getter
public class ResultadoCategoria extends AuditoriaConfig{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResultadoCategoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad") 
    private Actividad actividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_actividad") 
    private CategoriaActividad categoriaActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscripcion") 
    private Inscripcion inscripcion;

    private String puntajeFinal;
    private String puesto;
    private String desempate;
}
