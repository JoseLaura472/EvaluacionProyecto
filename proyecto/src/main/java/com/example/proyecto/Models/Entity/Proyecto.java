package com.example.proyecto.Models.Entity;

import java.io.Serializable;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
    private String nro_stand;
    private String categoria_proyecto;


     //Tabla Persona
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente")
    private Docente docente; 

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoriaProyecto")
    private CategoriaProyecto categoriaProyecto; 

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipoProyecto")
    private TipoProyecto tipoProyecto; 

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

  @Transient
  private MultipartFile file; 
  
  @Transient
  private String nombreArchivo; 

  //Tabla Archivo Adjunto
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "id_archivo_adjunto")
  private ArchivoAdjunto archivoAdjunto;
 

}
