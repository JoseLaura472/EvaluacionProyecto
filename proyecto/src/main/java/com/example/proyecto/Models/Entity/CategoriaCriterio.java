package com.example.proyecto.Models.Entity;

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
@Table(name = "CategoriaCriterio")
@Getter
@Setter
public class CategoriaCriterio {
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_categoria_criterio;
    private String nombre_cat_criterio;
    private String estado;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoriaCriterio", fetch = FetchType.LAZY)
    private List<Criterio> criterios;
}
