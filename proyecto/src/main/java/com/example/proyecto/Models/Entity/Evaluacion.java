package com.example.proyecto.Models.Entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "evaluacion")
@Getter
@Setter
public class Evaluacion implements Serializable{
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_evaluacion;
    private String estado;
    private int puntaje_total;
    
    //Tabla Jurado
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jurado")
    private Jurado jurado; 

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "evaluaciones", fetch = FetchType.LAZY)
    private List<Puntaje> puntajes;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "evaluacion_criterio", joinColumns = @JoinColumn(name = "id_evaluacion"), inverseJoinColumns = @JoinColumn(name = "id_criterio"))
    private Set<Criterio> criterios;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "evaluacion_ponderacion", joinColumns = @JoinColumn(name = "id_evaluacion"), inverseJoinColumns = @JoinColumn(name = "id_ponderacion"))
    private Set<Ponderacion> ponderaciones;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "evaluacion_proyecto", joinColumns = @JoinColumn(name = "id_evaluacion"), inverseJoinColumns = @JoinColumn(name = "id_proyecto"))
    private Set<Proyecto> proyectos;




}
