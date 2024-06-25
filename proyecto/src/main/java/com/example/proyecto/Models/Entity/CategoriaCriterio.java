package com.example.proyecto.Models.Entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipoProyecto")
    private TipoProyecto tipoProyecto;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoriaCriterio", fetch = FetchType.LAZY)
    private List<Pregunta> preguntas;
    
}
