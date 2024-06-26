package com.example.proyecto.Models.Entity;

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
@Table(name = "tipoProyecto")
@Setter
@Getter
public class TipoProyecto {
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tipoProyecto;
    private String nom_tipoProyecto;
    private String estado;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoProyecto", fetch = FetchType.LAZY)
    private List<Proyecto> proyectos;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoProyecto", fetch = FetchType.LAZY)
    private List<CategoriaCriterio> categoriaCriterios;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoProyecto", fetch = FetchType.LAZY)
    private List<CategoriaProyecto> categoriaProyectos;
}
