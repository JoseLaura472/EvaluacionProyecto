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

import com.example.proyecto.Models.Dao.IEvaluacionDao;
import com.example.proyecto.Models.Dao.IRubricaCriterioDao;
import com.example.proyecto.Models.Dto.EvaluacionGuardarDto;
import com.example.proyecto.Models.Dto.Fexcoin.EvaluacionCompletaDto;
import com.example.proyecto.Models.Dto.Fexcoin.EvaluacionResultDto;
import com.example.proyecto.Models.Entity.CategoriaActividad;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.EvaluacionDetalle;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.JuradoAsignacion;
import com.example.proyecto.Models.Entity.Participante;
import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.Entity.RubricaCriterio;
import com.example.proyecto.Models.IService.IEvaluacionService;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IRubricaService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluacionService {
    private final IEvaluacionDao evaluacionRepo;
    private final IRubricaCriterioDao criterioRepo;
    private final SseHub sseHub;
    private final IJuradoAsignacionService asignacionService;
    private final IRubricaService rubricaService;
    private final IInscripcionService inscripcionService;
    private final IEvaluacionService evaluacionService;
    private final ConcursoDashboardService dashboardService;

    @Transactional
    public void guardarEvaluacion(Jurado jurado, EvaluacionGuardarDto dto) {

        Inscripcion insc = inscripcionService.findById(dto.getInscripcionId());

        if (!insc.getActividad().getIdActividad().equals(dto.getActividadId())) {
            throw new IllegalArgumentException("Actividad no coincide con la inscripción");
        }

        boolean yaExiste = evaluacionRepo
                .existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJuradoAndRubrica_IdRubrica(
                        dto.getActividadId(), dto.getInscripcionId(), jurado.getIdJurado(), dto.getRubricaId());

        if (yaExiste) {
            throw new IllegalStateException("La inscripción ya fue evaluada por este jurado");
        }

        Rubrica rub = rubricaService.findById(dto.getRubricaId());

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

        var snap = dashboardService.snapshot(dto.getActividadId(), null);
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
    /**
     * Obtiene los datos necesarios para que el jurado evalúe
     * Solo devuelve datos de LA CATEGORÍA asignada al jurado
     */
    @Transactional
    public Map<String, Object> obtenerDatosEvaluacion(Long idActividad, Long idJurado) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // 1. Obtener la asignación del jurado
            JuradoAsignacion asignacion = asignacionService.findFirstByJuradoId(idJurado);
            
            if (asignacion == null) {
                throw new RuntimeException("No se encontró asignación para el jurado");
            }
            
            if (asignacion.getCategoriaActividad() == null) {
                throw new RuntimeException("La asignación no tiene categoría definida");
            }
            
            CategoriaActividad categoria = asignacion.getCategoriaActividad();
            Long idCategoria = categoria.getIdCategoriaActividad();
            
            // 2. Obtener la rúbrica de esta categoría
            Rubrica rubrica = rubricaService.findByActividadAndCategoria(idActividad, idCategoria);
            
            if (rubrica == null) {
                throw new RuntimeException("No se encontró rúbrica para la categoría: " + categoria.getNombre());
            }
            
            // 3. Obtener los criterios de evaluación
            List<Map<String, Object>> criterios = rubrica.getCriterios().stream()
                .filter(c -> "A".equals(c.getEstado()))
                .map(criterio -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", criterio.getIdRubricaCriterio());
                    map.put("nombre", criterio.getNombre());
                    map.put("maxPuntos", criterio.getMaxPuntos() != null ? criterio.getMaxPuntos() : 0);
                    map.put("descripcion", criterio.getDescripcion());
                    return map;
                })
                .collect(Collectors.toList());
            
            // 4. 🔥 AQUÍ ESTÁ EL CAMBIO CLAVE 🔥
            // Obtener inscripciones SOLO de esta categoría específica
            List<Inscripcion> inscripciones = inscripcionService.findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividadOrderByParticipante_PosicionAsc(
                idActividad, 
                idCategoria  // ← FILTRAR POR CATEGORÍA
            );
            
            // 5. Mapear participantes (ahora sin duplicados)
            List<Map<String, Object>> participantes = inscripciones.stream()
                .filter(i -> i.getParticipante() != null && "A".equals(i.getParticipante().getEstado()))
                .map(inscripcion -> {
                    Participante p = inscripcion.getParticipante();
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getIdParticipante());
                    map.put("nombre", p.getNombre());
                    map.put("institucion", p.getInstitucion());
                    
                    // Verificar si este jurado ya evaluó a este participante en esta categoría
                    boolean evaluado = evaluacionService.existeEvaluacion(
                        idJurado, 
                        p.getIdParticipante(), 
                        idCategoria
                    );
                    map.put("evaluado", evaluado);
                    
                    // Si ya fue evaluado, cargar puntajes
                    if (evaluado) {
                        Map<Long, Double> puntajes = obtenerPuntajesGuardados(
                            idJurado, 
                            p.getIdParticipante(), 
                            idCategoria
                        );
                        map.put("puntajes", puntajes);
                    } else {
                        map.put("puntajes", new HashMap<>());
                    }
                    
                    return map;
                })
                .collect(Collectors.toList());
            
            // 6. Construir respuesta
            Map<String, Object> datosCategoria = new HashMap<>();
            datosCategoria.put("idCategoria", idCategoria);
            datosCategoria.put("nombreCategoria", categoria.getNombre());
            datosCategoria.put("idRubrica", rubrica.getIdRubrica());
            datosCategoria.put("criterios", criterios);
            datosCategoria.put("participantes", participantes);
            
            resultado.put("categoria", datosCategoria);
            resultado.put("nombreActividad", asignacion.getActividad().getNombre());
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("══════════════════════════════════════════════════════");
            System.err.println("[EvaluacionService] ERROR: " + e.getMessage());
            e.printStackTrace();
            System.err.println("══════════════════════════════════════════════════════");
            throw new RuntimeException("Error al obtener datos de evaluación: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene los puntajes guardados anteriormente
     */
    private Map<Long, Double> obtenerPuntajesGuardados(Long idJurado, Long idParticipante, Long idCategoria) {
        Map<Long, Double> puntajes = new HashMap<>();
        
        try {
            Evaluacion evaluacion = evaluacionService.findByJuradoAndParticipanteAndCategoria(
                idJurado, idParticipante, idCategoria
            );
            
            if (evaluacion != null && evaluacion.getDetalles() != null) {
                evaluacion.getDetalles().forEach(detalle -> {
                    if (detalle.getRubricaCriterio() != null) {
                        puntajes.put(
                            detalle.getRubricaCriterio().getIdRubricaCriterio(), 
                            detalle.getPuntaje()
                        );
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("[EvaluacionService] Error al obtener puntajes: " + e.getMessage());
        }
        
        return puntajes;
    }
    
    /**
     * Guarda o actualiza la evaluación
     */
    @Transactional
    public void guardarEvaluacion(Map<String, Object> datos, Long idJurado) {
        try {
            Long idParticipante = Long.valueOf(datos.get("idParticipante").toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> puntajes = (Map<String, Object>) datos.get("puntajes");
            Double total = Double.valueOf(datos.get("total").toString());
            
            System.out.println("══════════════════════════════════════════════════════");
            System.out.println("[GuardarEvaluacion] Guardando evaluación");
            System.out.println("Jurado ID: " + idJurado);
            System.out.println("Participante ID: " + idParticipante);
            System.out.println("Total: " + total);
            
            // Obtener asignación del jurado
            JuradoAsignacion asignacion = asignacionService.findFirstByJuradoId(idJurado);
            if (asignacion == null) {
                throw new RuntimeException("No se encontró asignación del jurado");
            }
            
            Long idCategoria = asignacion.getCategoriaActividad().getIdCategoriaActividad();
            Long idActividad = asignacion.getActividad().getIdActividad();
            
            System.out.println("Categoría: " + asignacion.getCategoriaActividad().getNombre() + " (ID: " + idCategoria + ")");
            
            // Obtener rúbrica
            Rubrica rubrica = rubricaService.findByActividadAndCategoria(idActividad, idCategoria);
            if (rubrica == null) {
                throw new RuntimeException("No se encontró la rúbrica");
            }
            
            // Buscar inscripción del participante EN ESTA CATEGORÍA
            Inscripcion inscripcion = inscripcionService.findByActividadParticipanteAndCategoria(
                idActividad, 
                idParticipante,
                idCategoria
            );
            
            if (inscripcion == null) {
                throw new RuntimeException("El participante no está inscrito en esta categoría");
            }
            
            // Verificar si ya existe evaluación
            Evaluacion evaluacion = evaluacionService.findByJuradoAndParticipanteAndCategoria(
                idJurado, idParticipante, idCategoria
            );
            
            if (evaluacion == null) {
                // CREAR nueva evaluación
                evaluacion = new Evaluacion();
                evaluacion.setActividad(asignacion.getActividad());
                evaluacion.setJurado(asignacion.getJurado());
                evaluacion.setParticipante(inscripcion.getParticipante());
                evaluacion.setInscripcion(inscripcion);
                evaluacion.setRubrica(rubrica);
                evaluacion.setCategoriaActividad(asignacion.getCategoriaActividad());
                evaluacion.setEstado("A");
                
                System.out.println("→ Creando NUEVA evaluación");
            } else {
                // ACTUALIZAR evaluación existente
                evaluacion.getDetalles().clear();
                System.out.println("→ Actualizando evaluación existente (ID: " + evaluacion.getIdEvaluacion() + ")");
            }
            
            evaluacion.setTotalPonderacion(total);
            
            // Crear detalles por cada criterio
            for (Map.Entry<String, Object> entry : puntajes.entrySet()) {
                Long idCriterio = Long.valueOf(entry.getKey());
                Double puntaje = Double.valueOf(entry.getValue().toString());
                
                RubricaCriterio criterio = rubrica.getCriterios().stream()
                    .filter(c -> c.getIdRubricaCriterio().equals(idCriterio))
                    .findFirst()
                    .orElse(null);
                
                if (criterio != null) {
                    EvaluacionDetalle detalle = new EvaluacionDetalle();
                    detalle.setEvaluacion(evaluacion);
                    detalle.setRubricaCriterio(criterio);
                    detalle.setPuntaje(puntaje);
                    detalle.setPonderado(puntaje);
                    detalle.setEstado("A");
                    
                    evaluacion.getDetalles().add(detalle);
                    
                    System.out.println("  - Criterio: " + criterio.getNombre() + " = " + puntaje + " pts");
                }
            }
            
            // Guardar en BD
            evaluacionService.save(evaluacion);
            
            System.out.println("✅ Evaluación guardada exitosamente");
            System.out.println("══════════════════════════════════════════════════════");
            
            // 🔥 EMITIR ACTUALIZACIÓN VIA SSE 🔥
            try {
                // Obtener snapshot actualizado
                Map<String, Object> snapshot = dashboardService.getSnapshotCompleto(idActividad);
                
                // Broadcast a todos los clientes conectados a esta actividad
                sseHub.broadcast(idActividad, snapshot);
                
                log.info("📡 Broadcast enviado para actividad {}", idActividad);
                
            } catch (Exception e) {
                log.warn("Error enviando broadcast SSE: {}", e.getMessage());
                // No lanzar excepción, el guardado ya se realizó exitosamente
            }
            
        } catch (Exception e) {
            System.err.println("══════════════════════════════════════════════════════");
            System.err.println("[GuardarEvaluacion] ERROR: " + e.getMessage());
            e.printStackTrace();
            System.err.println("══════════════════════════════════════════════════════");
            throw new RuntimeException("Error al guardar la evaluación: " + e.getMessage());
        }
    }

    /* PARA FEXCOIN EL PRIMERO SE ELIMINA DE ARRIBAAAA AAHHH */

    /**
     * ✅ MÉTODO OPTIMIZADO: Guarda TODAS las evaluaciones en UNA sola transacción
     * 
     * Flujo:
     * 1. Validaciones iniciales (jurado, inscripción, categoría)
     * 2. Cargar TODAS las rúbricas y criterios en memoria (reduce queries)
     * 3. Procesar todas las evaluaciones
     * 4. Guardar en batch (reduce roundtrips a BD)
     * 5. Enviar UN solo evento SSE al finalizar
     */
    @Transactional
    public EvaluacionResultDto guardarEvaluacionCompleta(
            Jurado jurado, 
            EvaluacionCompletaDto dto) {
        
        log.info("Iniciando evaluación completa - Jurado: {}, Participante: {}, Rúbricas: {}", 
                 jurado.getIdJurado(), dto.getParticipanteId(), dto.getRubricas().size());
        
        // ========== 1. VALIDACIONES INICIALES ==========
        validarJuradoYCategoria(jurado, dto.getCategoriaId());
        
        Inscripcion inscripcion = obtenerInscripcion(dto.getCategoriaId(), dto.getParticipanteId());
        
        // ========== 2. CARGAR DATOS EN MEMORIA (optimización) ==========
        List<Long> rubricaIds = dto.getRubricas().stream()
            .map(EvaluacionCompletaDto.EvaluacionRubricaDto::getRubricaId)
            .collect(Collectors.toList());
        
        Map<Long, Rubrica> mapRubricas = cargarRubricas(rubricaIds, dto.getCategoriaId());
        Map<Long, List<RubricaCriterio>> mapCriteriosPorRubrica = cargarCriteriosPorRubricas(rubricaIds);
        
        // ========== 3. PROCESAR CADA RÚBRICA ==========
        List<Evaluacion> evaluacionesGuardadas = new ArrayList<>();
        double totalAcumulado = 0.0;
        
        for (EvaluacionCompletaDto.EvaluacionRubricaDto rubricaDto : dto.getRubricas()) {
            Rubrica rubrica = mapRubricas.get(rubricaDto.getRubricaId());
            if (rubrica == null) {
                throw new IllegalArgumentException("Rúbrica no encontrada: " + rubricaDto.getRubricaId());
            }
            
            List<RubricaCriterio> criteriosDisponibles = mapCriteriosPorRubrica.get(rubricaDto.getRubricaId());
            if (criteriosDisponibles == null || criteriosDisponibles.isEmpty()) {
                throw new IllegalStateException("La rúbrica '" + rubrica.getNombre() + "' no tiene criterios");
            }
            
            log.debug("Procesando rúbrica: '{}' (ID: {})", rubrica.getNombre(), rubrica.getIdRubrica());
            
            // Verificar si ya existe evaluación
            Optional<Evaluacion> existente = evaluacionRepo
                .findByJurado_IdJuradoAndInscripcion_IdInscripcionAndRubrica_IdRubrica(
                    jurado.getIdJurado(), 
                    inscripcion.getIdInscripcion(), 
                    rubrica.getIdRubrica()
                );
            
            Evaluacion evaluacion;
            if (existente.isPresent()) {
                log.warn("⚠ Actualizando evaluación existente - Rúbrica: {}", rubrica.getIdRubrica());
                evaluacion = existente.get();
                evaluacion.clearDetalles();
            } else {
                evaluacion = crearNuevaEvaluacion(jurado, inscripcion, rubrica);
            }
            
            // ✅ PROCESAR: Obtiene el puntaje directo del criterio seleccionado
            double puntajeRubrica = procesarDetalles(
                evaluacion, 
                rubricaDto.getDetalles(), 
                criteriosDisponibles
            );
            
            evaluacion.setTotalPonderacion(redondear(puntajeRubrica));
            evaluacionesGuardadas.add(evaluacion);
            
            // ✅ SUMAR al total acumulado
            totalAcumulado += puntajeRubrica;
        }
        
        // ========== 4. GUARDAR TODAS LAS EVALUACIONES ==========
        evaluacionRepo.saveAll(evaluacionesGuardadas);
        evaluacionRepo.flush(); // Forzar escritura inmediata
   
        // ========== 5. ENVIAR EVENTO SSE (después del commit) ==========
        Long actividadId = inscripcion.getActividad().getIdActividad();
        programarEventoSSE(actividadId);

        // ========== 6. RETORNAR RESULTADO ==========
        return EvaluacionResultDto.builder()
            .success(true)
            .message("Evaluación guardada correctamente")
            .participanteId(dto.getParticipanteId())
            .rubricasGuardadas(evaluacionesGuardadas.size())
            .promedioTotal(redondear(totalAcumulado))
            .build();
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private void validarJuradoYCategoria(Jurado jurado, Long categoriaId) {
        if (jurado == null || jurado.getIdJurado() == null) {
            throw new IllegalArgumentException("Jurado inválido");
        }
        
        boolean tieneAsignacion = asignacionService
            .existsCategoriaAsignadaByPersona(jurado.getPersona().getIdPersona());
        
        if (!tieneAsignacion) {
            throw new IllegalStateException("El jurado no tiene categoría asignada");
        }
    }
    
    private Inscripcion obtenerInscripcion(Long categoriaId, Long participanteId) {
        return inscripcionService
            .findByCategoriaAndParticipante(categoriaId, participanteId)
            .orElseThrow(() -> new EntityNotFoundException(
                "No existe inscripción para el participante en esta categoría"));
    }
    
    /**
     * ✅ Carga TODAS las rúbricas en una sola query
     */
    private Map<Long, Rubrica> cargarRubricas(List<Long> rubricaIds, Long categoriaId) {
        List<Rubrica> rubricas = rubricaService.findAllByIdRubricaIn(rubricaIds);
        
        Map<Long, Rubrica> mapa = new HashMap<>();
        for (Rubrica r : rubricas) {
            // Validar que pertenece a la categoría
            if (r.getCategoriaActividad() == null ||
                !r.getCategoriaActividad().getIdCategoriaActividad().equals(categoriaId)) {
                throw new IllegalStateException(
                    "La rúbrica '" + r.getNombre() + "' no pertenece a la categoría seleccionada");
            }
            
            if (!"A".equalsIgnoreCase(r.getEstado())) {
                throw new IllegalStateException(
                    "La rúbrica '" + r.getNombre() + "' no está activa");
            }
            
            mapa.put(r.getIdRubrica(), r);
        }
        
        return mapa;
    }
    
    /**
     * ✅ Carga TODOS los criterios de múltiples rúbricas en una sola query
     */
    private Map<Long, List<RubricaCriterio>> cargarCriteriosPorRubricas(List<Long> rubricaIds) {
        List<RubricaCriterio> todosCriterios = criterioRepo.findByRubricaIdIn(rubricaIds);
        
        return todosCriterios.stream()
            .collect(Collectors.groupingBy(
                c -> c.getRubrica().getIdRubrica(),
                Collectors.toList()
            ));
    }
    
    private Evaluacion crearNuevaEvaluacion(Jurado jurado, Inscripcion inscripcion, Rubrica rubrica) {
        Evaluacion ev = new Evaluacion();
        ev.setJurado(jurado);
        ev.setInscripcion(inscripcion);
        ev.setRubrica(rubrica);
        ev.setActividad(inscripcion.getActividad());
        ev.setParticipante(inscripcion.getParticipante());
        ev.setCategoriaActividad(inscripcion.getCategoriaActividad());
        ev.setEstado("A");
        return ev;
    }
    
    /**
     * ✅ Procesa detalles de una rúbrica y retorna el total ponderado
     */
    private double procesarDetalles(
        Evaluacion evaluacion,
        List<EvaluacionCompletaDto.DetalleDto> detallesSeleccionados,
        List<RubricaCriterio> criteriosDisponibles) {
    
        String nombreRubrica = evaluacion.getRubrica().getNombre();
        
        log.debug("Procesando rúbrica '{}' - Criterios seleccionados: {}", 
                nombreRubrica, detallesSeleccionados.size());
        
        // ========== VALIDACIÓN: Solo se permite UN criterio por rúbrica ==========
        if (detallesSeleccionados.size() != 1) {
            throw new IllegalArgumentException(
                "Debe seleccionar exactamente UN criterio por rúbrica. " +
                "Rúbrica '" + nombreRubrica + "' tiene " + detallesSeleccionados.size() + " seleccionados."
            );
        }
        
        // ========== CREAR MAPA DE CRITERIOS DISPONIBLES ==========
        Map<Long, RubricaCriterio> mapCriterios = criteriosDisponibles.stream()
            .collect(Collectors.toMap(
                RubricaCriterio::getIdRubricaCriterio,
                c -> c
            ));
        
        // ========== PROCESAR EL CRITERIO SELECCIONADO ==========
        EvaluacionCompletaDto.DetalleDto detalleDto = detallesSeleccionados.get(0);
        RubricaCriterio criterioSeleccionado = mapCriterios.get(detalleDto.getCriterioId());
        
        if (criterioSeleccionado == null) {
            throw new IllegalArgumentException(
                "Criterio inválido o no pertenece a la rúbrica: " + detalleDto.getCriterioId()
            );
        }
        
        // ========== OBTENER PUNTAJE DEL CRITERIO ==========
        // El puntaje viene directamente del criterio (maxPuntos o el valor que envía el frontend)
        double puntajeDirecto = obtenerPuntajeCriterio(detalleDto, criterioSeleccionado);
        
        log.debug("  → Criterio seleccionado: '{}' - Puntaje: {} pts", 
                criterioSeleccionado.getNombre(), puntajeDirecto);
        
        // ========== CREAR DETALLE DE EVALUACIÓN ==========
        EvaluacionDetalle detalle = new EvaluacionDetalle();
        detalle.setEvaluacion(evaluacion);
        detalle.setRubricaCriterio(criterioSeleccionado);
        detalle.setPuntaje(puntajeDirecto);         // Puntaje directo
        detalle.setPorcentaje(100);                  // Siempre 100% (es el único seleccionado)
        detalle.setPonderado(puntajeDirecto);        // Ponderado = puntaje directo
        detalle.setEstado("A");
        
        evaluacion.getDetalles().add(detalle);
        
        // ========== RETORNAR EL PUNTAJE (que se sumará al total) ==========
        return puntajeDirecto;
    }
    
    /**
     * Obtiene el puntaje del criterio seleccionado
     */
    private double obtenerPuntajeCriterio(
            EvaluacionCompletaDto.DetalleDto detalleDto, 
            RubricaCriterio criterio) {
        
        // El frontend envía el puntaje que el jurado ve en pantalla
        Integer puntajeRecibido = detalleDto.getPuntaje();
        
        if (puntajeRecibido == null || puntajeRecibido < 0) {
            throw new IllegalArgumentException(
                "Puntaje inválido para criterio '" + criterio.getNombre() + "'"
            );
        }
        
        // Validar que el puntaje coincida con maxPuntos (si está definido)
        if (criterio.getMaxPuntos() != null && criterio.getMaxPuntos() > 0) {
            // El criterio tiene un puntaje fijo
            int maxPuntos = criterio.getMaxPuntos();
            
            // El puntaje enviado debe ser exactamente el maxPuntos del criterio
            // (porque el jurado seleccionó ESTE criterio completo)
            if (puntajeRecibido != maxPuntos) {
                log.warn("Puntaje recibido ({}) difiere de maxPuntos ({}). Usando maxPuntos.", 
                        puntajeRecibido, maxPuntos);
                return maxPuntos;
            }
            
            return maxPuntos;
        }
        
        // Si no tiene maxPuntos, usar el puntaje recibido
        return puntajeRecibido.doubleValue();
    }
    
    /**
     * ✅ Programa evento SSE para ejecutar DESPUÉS del commit
     */
    private void programarEventoSSE(Long actividadId) {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        var snapshot = dashboardService.snapshot(actividadId, null);
                        
                        Map<String, Object> payload = Map.of(
                            "tipo", "EVALUACION_GUARDADA",
                            "filas", snapshot.getFilas(),
                            "categorias", snapshot.getCategorias(),
                            "resumenGlobal", snapshot.getResumenGlobal()
                        );
                        
                        sseHub.broadcast(actividadId, payload);
                        
                        log.debug("Evento SSE enviado correctamente - Actividad: {}", actividadId);
                        
                    } catch (Exception e) {
                        log.warn("Error al enviar evento SSE (actividadId={}): {}", 
                                actividadId, e.getMessage());
                    }
                }
            }
        );
    }
    
    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

}