package com.example.proyecto.Models.Entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "persona")
@Getter
@Setter
public class Persona implements Serializable{
       private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_persona;
    
    private String estado;
    private String tipoSexo;
    private String nombres;
    private String paterno;
    private String materno;
    private String ci;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecNacimiento;
    
    private String direccion;
    private String telefono;
    private String email;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Jurado> jurado;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Docente> docente;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Estudiante> estudiantes;

    public String getNombreCompleto() {
        if (this.getMaterno() == null) {
            return this.getNombres() + " " + this.getPaterno();
        }

        if (this.getPaterno() == null) {
            return this.getNombres() + " " + this.getMaterno();
        }

        return this.getNombres() + " " + this.getPaterno() + " " + this.getMaterno();
    }
    
}
