package com.example.proyecto.Models.Entity;

import java.util.ArrayList;
import java.util.List;

import com.example.proyecto.config.AuditoriaConfig;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "evaluacion", indexes = {
    @Index(name = "idx_eval_jurado_cat", columnList = "id_jurado, id_categoria_actividad"),
    @Index(name = "idx_eval_participante", columnList = "id_participante, id_categoria_actividad"),
    @Index(name = "idx_eval_inscripcion", columnList = "id_inscripcion"),
    @Index(name = "idx_eval_actividad", columnList = "id_actividad")
})
@Getter @Setter
@NoArgsConstructor
public class Evaluacion extends AuditoriaConfig{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_actividad")
    private Actividad actividad; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscripcion")
    private Inscripcion inscripcion; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jurado")
    private Jurado jurado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rubrica")
    private Rubrica rubrica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_participante")
    private Participante participante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_actividad")
    private CategoriaActividad categoriaActividad;

    private double totalPonderacion;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluacionDetalle> detalles = new ArrayList<>();

    // Método helper para mantener la sincronización bidireccional
    public void addDetalle(EvaluacionDetalle detalle) {
        detalles.add(detalle);
        detalle.setEvaluacion(this);
    }
    
    public void removeDetalle(EvaluacionDetalle detalle) {
        detalles.remove(detalle);
        detalle.setEvaluacion(null);
    }

    public void clearDetalles() {
        detalles.forEach(d -> d.setEvaluacion(null));
        detalles.clear();
    }
}