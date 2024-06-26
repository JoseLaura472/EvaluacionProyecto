package com.example.proyecto.Models.Entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categoriaProyecto")
@Setter
@Getter
public class CategoriaProyecto {
    
    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_categoriaProyecto;
    private String nom_categoria;
    private String estado;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipoProyecto")
    private TipoProyecto tipoProyecto;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoriaProyecto", fetch = FetchType.LAZY)
    private List<Proyecto> proyectos;
}
