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

        Inscripcion insc = inscripcionRepo.findById(dto.getInscripcionId());

        if (!insc.getActividad().getIdActividad().equals(dto.getActividadId())) {
            throw new IllegalArgumentException("Actividad no coincide con la inscripción");
        }

        boolean yaExiste = evaluacionRepo
        .existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJuradoAndRubrica_IdRubrica(
            dto.getActividadId(), dto.getInscripcionId(), jurado.getIdJurado(), dto.getRubricaId());

        if (yaExiste) {
            throw new IllegalStateException("La inscripción ya fue evaluada por este jurado");
        }

        Rubrica rub = rubricaRepo.findById(dto.getRubricaId());

        List<RubricaCriterio> criterios = criterioRepo.findByRubrica(rub.getIdRubrica());
        if (criterios.isEmpty()) throw new IllegalStateException("La rúbrica no tiene criterios");

        boolean esEscala = (dto.getDetalles() != null && dto.getDetalles().size() == 1)
        || criterios.stream().anyMatch(c -> c.getMaxPuntos() != null);

        if (!esEscala) {
            int sumaPorc = criterios.stream().mapToInt(RubricaCriterio::getPorcentaje).sum();
            if (sumaPorc != 100) {
                throw new IllegalStateException("La rúbrica no suma 100%");
            }
        }

        Map<Long, RubricaCriterio> mapCriterios = criterios.stream()
                .collect(Collectors.toMap(RubricaCriterio::getIdRubricaCriterio, c -> c));

        Evaluacion ev = new Evaluacion();
        ev.setActividad(insc.getActividad());
        ev.setInscripcion(insc);
        ev.setEstado("A");
        ev.setJurado(jurado);
        ev.setRubrica(rub);

        ev.setParticipante(insc.getParticipante());
        ev.setCategoriaActividad(insc.getCategoriaActividad());

        double total = 0.0;

        if (esEscala && (dto.getDetalles() == null || dto.getDetalles().size() != 1)) {
            throw new IllegalArgumentException("La rúbrica de escala debe tener exactamente un detalle seleccionado");
        }

        for (var d : dto.getDetalles()) {
            RubricaCriterio crit = mapCriterios.get(d.getCriterioId());
            if (crit == null)
                throw new IllegalArgumentException("Criterio inválido: " + d.getCriterioId());

            double punt = Optional.ofNullable(d.getPuntaje()).orElse(0.0);
            if (punt < 0 || punt > 100)
                throw new IllegalArgumentException("Puntaje fuera de rango (0..100)");

            int porc = esEscala ? 100 : crit.getPorcentaje();
            double pond = punt * (porc / 100.0);
            total += pond;

            EvaluacionDetalle det = new EvaluacionDetalle();
            det.setEvaluacion(ev);
            det.setRubricaCriterio(crit);
            det.setPuntaje(punt);
            det.setEstado("A");
            det.setPorcentaje(porc);
            det.setPonderado(pond);

            ev.getDetalles().add(det);
        }
        ev.setTotalPonderacion(Math.round(total * 100.0) / 100.0);

        evaluacionRepo.save(ev);

        var snap = dashService.snapshot(dto.getActividadId(), null);
        Map<String, Object> payload = Map.of(
                "tipo", "EVALUACION_GUARDADA",
                "filas", snap.getFilas(),
                "categorias", snap.getCategorias(),
                "resumenGlobal", snap.getResumenGlobal());
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
