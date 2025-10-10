package com.example.proyecto.Models.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.proyecto.Models.Dto.EvaluacionGuardarDto;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.EvaluacionDetalle;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.Entity.RubricaCriterio;
import com.example.proyecto.Models.IService.IEvaluacionService;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.example.proyecto.Models.IService.IRubricaCriterioServcie;
import com.example.proyecto.Models.IService.IRubricaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluacionService {
    private final IEvaluacionService evaluacionRepo;
    private final IInscripcionService inscripcionRepo;
    private final IRubricaService rubricaRepo;
    private final IRubricaCriterioServcie criterioRepo;
    private final SseHub sseHub;
    private final ConcursoDashboardService dashService;

    @Transactional
    public void guardarEvaluacion(Jurado jurado, EvaluacionGuardarDto dto) {

        // 1) Cargar y validar inscripción
        Inscripcion insc = inscripcionRepo.findById(dto.getInscripcionId());

        if (!insc.getActividad().getIdActividad().equals(dto.getActividadId())) {
            throw new IllegalArgumentException("Actividad no coincide con la inscripción");
        }

        // 2) Evitar doble evaluación del mismo jurado
        boolean yaExiste = evaluacionRepo
                .existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJurado(
                        dto.getActividadId(), dto.getInscripcionId(), jurado.getIdJurado());
        if (yaExiste) {
            throw new IllegalStateException("La inscripción ya fue evaluada por este jurado");
        }

        // 3) Cargar rúbrica
        Rubrica rub = rubricaRepo.findById(dto.getRubricaId());

        // 4) Cargar criterios y validar 100%
        List<RubricaCriterio> criterios = criterioRepo.findByRubrica(rub.getIdRubrica());
        if (criterios.isEmpty())
            throw new IllegalStateException("La rúbrica no tiene criterios");
        int sumaPorc = criterios.stream().mapToInt(RubricaCriterio::getPorcentaje).sum();
        if (sumaPorc != 100)
            throw new IllegalStateException("La rúbrica no suma 100%");

        Map<Long, RubricaCriterio> mapCriterios = criterios.stream()
                .collect(Collectors.toMap(RubricaCriterio::getIdRubricaCriterio, c -> c));

        // 5) Crear evaluación (usamos cascade: ver entity Evaluacion con detalles +
        // CascadeType.ALL)
        Evaluacion ev = new Evaluacion();
        ev.setActividad(insc.getActividad());
        ev.setInscripcion(insc);
        ev.setJurado(jurado);
        ev.setRubrica(rub);

        double total = 0.0;
        for (var d : dto.getDetalles()) {
            RubricaCriterio crit = mapCriterios.get(d.getCriterioId());
            if (crit == null)
                throw new IllegalArgumentException("Criterio inválido: " + d.getCriterioId());

            double punt = Optional.ofNullable(d.getPuntaje()).orElse(0.0);
            if (punt < 0 || punt > 100)
                throw new IllegalArgumentException("Puntaje fuera de rango (0..100)");

            int porc = crit.getPorcentaje();
            double pond = punt * (porc / 100.0);
            total += pond;

            EvaluacionDetalle det = new EvaluacionDetalle();
            det.setEvaluacion(ev); // FK
            det.setRubricaCriterio(crit);
            det.setPuntaje(punt);
            det.setPorcentaje(porc);
            det.setPonderado(pond);

            ev.getDetalles().add(det); // ← IMPORTANTE (y requiere que 'detalles' esté inicializada)
        }
        ev.setTotalPonderacion(Math.round(total * 100.0) / 100.0);

        evaluacionRepo.save(ev); // ← persiste cabecera + detalles (cascade)

        // 6) Notificar dashboard (SSE)
        var snap = dashService.snapshot(dto.getActividadId(), null);
        Map<String, Object> payload = Map.of(
                "tipo", "EVALUACION_GUARDADA",
                "filas", snap.getFilas(),
                "categorias", snap.getCategorias(),
                "resumenGlobal", snap.getResumenGlobal());

        // Enviar SOLO después que se confirme el commit:
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                try {
                    sseHub.broadcast(dto.getActividadId(), payload);
                } catch (Exception e) {
                    // Nunca re-lances: sólo loguea
                    // log.warn("SSE broadcast falló (actId={}): {}", dto.getActividadId(), e.toString());
                }
            }
        });
    }

}
