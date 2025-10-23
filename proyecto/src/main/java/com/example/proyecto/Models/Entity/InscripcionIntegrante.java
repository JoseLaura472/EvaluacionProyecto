package com.example.proyecto.Models.Entity;

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
@Table(name = "inscripcion_integrante")
@Setter @Getter
public class InscripcionIntegrante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscripcionIntegrante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscripcion", nullable = false)
    private Inscripcion inscripcion;

    // Puedes reutilizar Persona si ya la usas para Jurado, o seguir con Participante.
    // Si mantienes Participante:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participante", nullable = false)
    private Participante participante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
}
