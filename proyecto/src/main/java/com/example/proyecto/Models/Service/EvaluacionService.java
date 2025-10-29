package com.example.proyecto.Models.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.proyecto.Models.Dao.ICategoriaActividadDao;
import com.example.proyecto.Models.Dao.IEvaluacionDao;
import com.example.proyecto.Models.Dao.IJuradoDao;
import com.example.proyecto.Models.Dao.IRubricaCriterioDao;
import com.example.proyecto.Models.Dto.EvaluacionGuardarDto;
import com.example.proyecto.Models.Entity.CategoriaActividad;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.EvaluacionDetalle;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.Entity.RubricaCriterio;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.example.proyecto.Models.IService.IRubricaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluacionService {
    private final IEvaluacionDao evaluacionRepo;
    private final IInscripcionService inscripcionRepo;
    private final IRubricaService rubricaRepo;
    private final IRubricaCriterioDao criterioRepo;
    private final SseHub sseHub;
    private final ConcursoDashboardService dashService;
    private final ICategoriaActividadDao categoriaActividadDao;
    private final IJuradoDao juradoDao;
    private final IRubricaCriterioDao rubricaCriterioDao;

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
        if (criterios.isEmpty())
            throw new IllegalStateException("La rúbrica no tiene criterios");

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
            @Override
            public void afterCommit() {
                try {
                    sseHub.broadcast(dto.getActividadId(), payload);
                } catch (Exception e) {
                    // Nunca re-lances: sólo loguea
                    // log.warn("SSE broadcast falló (actId={}): {}", dto.getActividadId(),
                    // e.toString());
                }
            }
        });
    }

    /* PARA EVALUACIÓN DE ENTRADA UNIVERISTARIA */

    @Transactional
    public Map<String, Object> obtenerDatosEvaluacion(Long idActividad, Long idJurado) {

        // Obtener categorías de recorrido y palco
        List<CategoriaActividad> categoriasRecorrido = categoriaActividadDao
                .findByActividadIdActividadAndFase(idActividad, "RECORRIDO");

        List<CategoriaActividad> categoriasPalco = categoriaActividadDao
                .findByActividadIdActividadAndFase(idActividad, "PALCO");

        Map<String, Object> resultado = new HashMap<>();

        // Datos de recorrido
        resultado.put("recorrido", construirDatosFase(
                categoriasRecorrido, idJurado, "RECORRIDO"));

        // Datos de palco
        resultado.put("palco", construirDatosFase(
                categoriasPalco, idJurado, "PALCO"));

        return resultado;
    }

    private Map<String, Object> construirDatosFase(
            List<CategoriaActividad> categorias,
            Long idJurado,
            String fase) {

        Map<String, Object> datosFase = new HashMap<>();

        // Obtener criterios (rúbrica)
        if (!categorias.isEmpty()) {
            CategoriaActividad categoria = categorias.get(0);
            Rubrica rubrica = rubricaRepo.findByCategoria(categoria.getIdCategoriaActividad());

            List<Map<String, Object>> criterios = new ArrayList<>();
            for (RubricaCriterio criterio : rubrica.getCriterios()) {
                criterios.add(Map.of(
                        "id", criterio.getIdRubricaCriterio(),
                        "nombre", criterio.getNombre(),
                        "maxPuntos", criterio.getMaxPuntos()));
            }
            datosFase.put("criterios", criterios);
        }

        // Obtener participantes
        List<Map<String, Object>> participantes = new ArrayList<>();
        for (CategoriaActividad categoria : categorias) {
            List<Inscripcion> inscripciones = inscripcionRepo
                    .findByCategoria(categoria.getIdCategoriaActividad());

            for (Inscripcion inscripcion : inscripciones) {
                boolean evaluado = evaluacionRepo
                        .existsByInscripcionIdInscripcionAndJuradoIdJurado(
                                inscripcion.getIdInscripcion(), idJurado);

                participantes.add(Map.of(
                        "id", inscripcion.getParticipante().getIdParticipante(),
                        "nombre", inscripcion.getParticipante().getNombre(),
                        "evaluado", evaluado,
                        "puntajes", new HashMap<>()));
            }
        }
        datosFase.put("participantes", participantes);

        return datosFase;
    }

    @Transactional
    public void guardarEvaluacion(Map<String, Object> datos, Long idJurado) {
        Long idParticipante = ((Number) datos.get("idParticipante")).longValue();
        String fase = (String) datos.get("fase");
        Map<String, Double> puntajes = (Map<String, Double>) datos.get("puntajes");

        // Buscar inscripción correspondiente
        Inscripcion inscripcion = inscripcionRepo
                .findByParticipanteAndFase(idParticipante, fase);

        // Crear evaluación
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setInscripcion(inscripcion);
        evaluacion.setJurado(juradoDao.findById(idJurado).orElseThrow());
        evaluacion.setActividad(inscripcion.getActividad());
        evaluacion.setCategoriaActividad(inscripcion.getCategoriaActividad());
        evaluacion.setParticipante(inscripcion.getParticipante());
        Rubrica rubrica = rubricaRepo.findByCategoria(
                inscripcion.getCategoriaActividad().getIdCategoriaActividad());
        evaluacion.setRubrica(rubrica);

        double totalPonderacion = 0;

        // Crear detalles
        for (Map.Entry<String, Double> entry : puntajes.entrySet()) {
            Long idCriterio = Long.parseLong(entry.getKey());
            Double puntaje = entry.getValue();

            RubricaCriterio criterio = rubricaCriterioDao
                    .findById(idCriterio).orElseThrow();

            EvaluacionDetalle detalle = new EvaluacionDetalle();
            detalle.setEvaluacion(evaluacion);
            detalle.setRubricaCriterio(criterio);
            detalle.setPuntaje(puntaje);
            detalle.setPorcentaje(0); // Si no usas porcentaje
            detalle.setPonderado(puntaje);

            evaluacion.getDetalles().add(detalle);
            totalPonderacion += puntaje;
        }

        evaluacion.setTotalPonderacion(totalPonderacion);
        evaluacionRepo.save(evaluacion);
    }
}
