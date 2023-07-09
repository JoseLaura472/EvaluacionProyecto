package com.example.proyecto.Models.Entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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

  

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "evaluacion_criterio", joinColumns = @JoinColumn(name = "id_evaluacion"), inverseJoinColumns = @JoinColumn(name = "id_criterio"))
    private Set<Criterio> criterios;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "evaluacion_proyecto", joinColumns = @JoinColumn(name = "id_evaluacion"), inverseJoinColumns = @JoinColumn(name = "id_proyecto"))
    private Set<Proyecto> proyectos;




}
