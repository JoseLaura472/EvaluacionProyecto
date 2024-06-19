package com.example.proyecto.Models.Entity;

import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Ponderacion")
@Getter
@Setter
public class Ponderacion {
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_ponderacion;
    private int ponderacion;
    private String estado;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ponderaciones", fetch = FetchType.LAZY)
    private List<Criterio> criterios;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ponderaciones", fetch = FetchType.LAZY)
    private List<Puntaje> puntajes;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta")
    private Pregunta preguntas; 

    @JsonIgnore
    @ManyToMany(mappedBy = "ponderaciones")
    private Set<Evaluacion> evaluacion;
}
