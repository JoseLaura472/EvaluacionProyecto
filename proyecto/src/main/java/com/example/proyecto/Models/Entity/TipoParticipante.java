package com.example.proyecto.Models.Entity;

import com.example.proyecto.config.AuditoriaConfig;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tipo_participante")
@Setter @Getter
public class TipoParticipante extends AuditoriaConfig{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoParticipante;

    private String nombre;
    private String descripcion;
}
