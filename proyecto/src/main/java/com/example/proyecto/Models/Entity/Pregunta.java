package com.example.proyecto.Models.Entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pregunta")
@Getter
@Setter
public class Pregunta implements Serializable{
     private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_pregunta;
    private String estado;
    private String pregunta;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "preguntas", fetch = FetchType.LAZY)
    private List<Criterio> criterio;
}