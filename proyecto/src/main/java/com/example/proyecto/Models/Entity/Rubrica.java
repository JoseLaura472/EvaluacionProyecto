package com.example.proyecto.Models.Entity;

import com.example.proyecto.config.AuditoriaConfig;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rubrica")
@Setter @Getter
public class Rubrica extends AuditoriaConfig{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRubrica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad")
    private Actividad actividad;

    private String nombre;
    private String version;
}
