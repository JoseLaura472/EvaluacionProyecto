package com.example.proyecto.Models.Entity;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pregunta")
    private Pregunta preguntas; 

    @JsonIgnore
    @ManyToMany(mappedBy = "ponderaciones")
    private Set<Evaluacion> evaluacion;
}
