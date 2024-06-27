package com.example.proyecto.Models.Entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "facultad")
@Setter
@Getter
public class Facultad implements Serializable{
    
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_facultad;
    private String estado;
    private String nom_facultad;
    private String sigla;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "facultad", fetch = FetchType.LAZY)
    private List<Programa> programas;
}
