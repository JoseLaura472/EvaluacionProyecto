package com.example.proyecto.Models.Entity;

import java.util.Date;

import com.example.proyecto.config.AuditoriaConfig;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "actividad")
@Setter @Getter
public class Actividad extends AuditoriaConfig{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActividad;

    private String nombre;
    private String tipo;
    private String descripcion;
    private Date fecha;
}