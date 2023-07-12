package com.example.proyecto.Models.Entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "proyecto")
@Getter
@Setter
public class Proyecto implements Serializable{
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_proyecto;
    private String estado;
    private String nombre_proyecto;
    private double promedio_final;
   


     //Tabla Persona
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente")
    private Docente docente; 

    //Tabla Persona
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_programa")
    private Programa programa; 

  @JsonManagedReference
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "estudiante_proyecto", joinColumns = @JoinColumn(name = "id_proyecto"), inverseJoinColumns = @JoinColumn(name = "id_estudiante"))
  private Set<Estudiante> estudiante;

   @JsonManagedReference
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "jurado_proyecto", joinColumns = @JoinColumn(name = "id_proyecto"), inverseJoinColumns = @JoinColumn(name = "id_jurado"))
  private Set<Jurado> jurado;

  @JsonIgnore
  @ManyToMany(mappedBy = "proyectos")
  private Set<Evaluacion> evaluacion;


 

}
