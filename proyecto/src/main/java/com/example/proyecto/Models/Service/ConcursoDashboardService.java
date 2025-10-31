package com.example.proyecto.Models.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dto.ActividadMiniDto;
import com.example.proyecto.Models.Dto.DashboardSnapshotDto;
import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.CategoriaActividad;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.JuradoAsignacion;
import com.example.proyecto.Models.Entity.Participante;
import com.example.proyecto.Models.IService.IActividadService;
import com.example.proyecto.Models.IService.ICategoriaActividadService;
import com.example.proyecto.Models.IService.IEvaluacionService;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IRubricaService;
import com.example.proyecto.Models.Service.report.EntradaUniversitaria.ReportePdfService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcursoDashboardService {
    private final IEvaluacionService evaluacionRepo;
    private final IJuradoAsignacionService asignacionRepo;
    private final ICategoriaActividadService categoriaRepo;
    private final IInscripcionService inscripcionRepo;
    private final IActividadService actividadRepo;
    private final IRubricaService rubricaService;

    private final IActividadService actividadService;
    private final ICategoriaActividadService categoriaService;
    private final IInscripcionService inscripcionService;
    private final IEvaluacionService evaluacionService;
    private final IJuradoAsignacionService juradoAsignacionService;

    private final ReportePdfService reportePdfService;

    // Jurados para columnas
    private List<DashboardSnapshotDto.JuradoMiniDto> miniJurados(List<Jurado> jurados) {
            return jurados.stream()
                .map(j -> {
                    var d = new DashboardSnapshotDto.JuradoMiniDto();
                    d.setId(j.getIdJurado());
                    d.setNombre(j.getPersona() != null ? j.getPersona().getNombres() : ("Jurado #" + j.getIdJurado()));
                    return d;
                }).toList();
        }

    public DashboardSnapshotDto snapshot(Long actividadId, Long categoriaId) {
        var dto = new DashboardSnapshotDto();

        // 1) Actividad
        var act = actividadRepo.findById(actividadId);
        var actDto = new ActividadMiniDto();
        actDto.setId(act.getIdActividad());
        actDto.setNombre(act.getNombre());
        actDto.setDescripcion(act.getDescripcion());
        dto.setActividad(actDto);

        // 2) Categorías de la actividad
        var cats = categoriaRepo.findByActividad(actividadId);

        // 2.1) Precontar inscripciones por categoría
        Map<Long, Long> inscPorCat = cats.stream().collect(Collectors.toMap(
            CategoriaActividad::getIdCategoriaActividad,
            c -> inscripcionRepo.countByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(
                    actividadId, c.getIdCategoriaActividad())
        ));

        // 2.2) Precontar rúbricas totales por categoría (map catId -> int)
        Map<Long, Integer> rubTotalesPorCat = cats.stream().collect(Collectors.toMap(
            CategoriaActividad::getIdCategoriaActividad,
            c -> (int) rubricaService.countActivasByCategoria(c.getIdCategoriaActividad())
        ));

        // 3) Jurados: por categoría si viene filtro, si no por actividad completa
        List<Jurado> juradosEntity = (categoriaId != null)
            ? asignacionRepo.findJuradosByActividadAndCategoriaOrdenAsignacion(actividadId, categoriaId)
            : asignacionRepo.findJuradosByActividadOrdenAsignacion(actividadId);
        var jurados = miniJurados(juradosEntity);
        dto.setJurados(jurados);
        // para validación "completado"
        List<Long> juradoIdsConsiderados = juradosEntity.stream().map(Jurado::getIdJurado).toList();

        // 4) Inscripciones (filtradas si corresponde)
        List<Inscripcion> inscs = (categoriaId == null)
            ? inscripcionRepo.findByActividad_IdActividad(actividadId)
            : inscripcionRepo.findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(actividadId, categoriaId);

        // 5) Evaluaciones (todas de la actividad; si quieres, filtra por categoría en el repo)
        List<Evaluacion> evals = evaluacionRepo.findByActividad_IdActividad(actividadId);

        // (opcional) si categoría llega, puedes filtrar aquí:
        if (categoriaId != null) {
            evals = evals.stream()
                         .filter(e -> e.getInscripcion().getCategoriaActividad().getIdCategoriaActividad().equals(categoriaId))
                         .toList();
        }

        // 5.1) Agrupar por inscripción y jurado → lista de evaluaciones (cada evaluación = una rúbrica)
        // Map<inscId, Map<juradoId, List<Evaluacion>>>
        Map<Long, Map<Long, List<Evaluacion>>> evalsByInscJurado = new HashMap<>();
        for (var e : evals) {
            Long inscId = e.getInscripcion().getIdInscripcion();
            Long jurId = e.getJurado().getIdJurado();
            evalsByInscJurado
                .computeIfAbsent(inscId, k -> new HashMap<>())
                .computeIfAbsent(jurId, k -> new ArrayList<>())
                .add(e);
        }

        // 6) Armar filas
        List<DashboardSnapshotDto.FilaDto> filas = new ArrayList<>();
        Map<Long, Integer> completadasPorCat = new HashMap<>();

        for (var insc : inscs) {
            var f = new DashboardSnapshotDto.FilaDto();
            f.setInscripcionId(insc.getIdInscripcion());
            Long catId = insc.getCategoriaActividad().getIdCategoriaActividad();
            f.setCategoriaId(catId);
            f.setParticipante(insc.getParticipante().getNombre());
            f.setInstitucion(insc.getParticipante().getInstitucion());

            int rubTotales = rubTotalesPorCat.getOrDefault(catId, 0);
            f.setRubricasTotales(rubTotales);

            Map<Long, List<Evaluacion>> byJur = evalsByInscJurado.getOrDefault(insc.getIdInscripcion(), Map.of());

            // llenar scores y rubricasPorJurado sólo para jurados considerados
            for (Long jId : juradoIdsConsiderados) {
                List<Evaluacion> list = byJur.getOrDefault(jId, List.of());
                int hechas = list.size();
                f.getRubricasPorJurado().put(jId, hechas);

                if (!list.isEmpty()) {
                    // SUMA por jurado (todas las rúbricas de esa inscripción hechas por ese jurado)
                    double suma = list.stream()
                                    .mapToDouble(Evaluacion::getTotalPonderacion)
                                    .sum();
                    suma = Math.round(suma * 100.0) / 100.0;
                    f.getScores().put(jId, suma);
                }
            }

            // promedio/puntos (promedio simple de scores disponibles)
            if (!f.getScores().isEmpty()) {
                double prom = f.getScores().values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                prom = Math.round(prom * 100.0) / 100.0;
                f.setPromedio(prom);
            } else {
                f.setPromedio(null);
            }

            // completado: TODOS los jurados considerados hicieron TODAS las rúbricas
            boolean completo = !juradoIdsConsiderados.isEmpty()
                    && juradoIdsConsiderados.stream().allMatch(jId ->
                        f.getRubricasPorJurado().getOrDefault(jId, 0) >= rubTotales && rubTotales > 0
                    );
            f.setCompletado(completo);
            if (completo) {
                completadasPorCat.merge(catId, 1, Integer::sum);
            }

            filas.add(f);
        }

        dto.setFilas(filas);

        // 7) Chips de categorías (progreso)
        var catsDto = cats.stream().map(c -> {
            var cd = new DashboardSnapshotDto.CategoriaResumenDto();
            cd.setId(c.getIdCategoriaActividad());
            cd.setNombre(c.getNombre());

            var prog = new DashboardSnapshotDto.ProgresoDto();
            int total = inscPorCat.getOrDefault(c.getIdCategoriaActividad(), 0L).intValue();
            int comp = completadasPorCat.getOrDefault(c.getIdCategoriaActividad(), 0);
            prog.setTotal(total);
            prog.setCompletadas(comp);
            prog.setPorcentaje(total == 0 ? 0 : (comp * 100.0 / total));

            cd.setProgreso(prog);
            return cd;
        }).toList();
        dto.setCategorias(catsDto);

        // 8) Resumen global
        int totalGlobal = catsDto.stream().mapToInt(x -> x.getProgreso().getTotal()).sum();
        int compGlobal = catsDto.stream().mapToInt(x -> x.getProgreso().getCompletadas()).sum();
        var glob = new DashboardSnapshotDto.ProgresoDto();
        glob.setTotal(totalGlobal);
        glob.setCompletadas(compGlobal);
        glob.setPorcentaje(totalGlobal == 0 ? 0 : (compGlobal * 100.0 / totalGlobal));
        dto.setResumenGlobal(glob);

        return dto;
    }

    /**
     * Snapshot completo (todas las categorías individualmente)
     */
    public Map<String, Object> getSnapshotCompleto(Long idActividad) {
        Actividad actividad = actividadService.findById(idActividad);
        
        Map<String, Object> result = new HashMap<>();
        result.put("actividad", mapActividad(actividad));
        result.put("categorias", obtenerCategorias(idActividad));
        result.put("jurados", obtenerJurados(idActividad));
        result.put("filas", obtenerFilasPorCategoria(idActividad));
        result.put("resumenGlobal", calcularResumenGlobal(idActividad));
        result.put("tipoVista", "completo");
        
        return result;
    }
    
    /**
     * Snapshot filtrado por una categoría específica
     */
    public Map<String, Object> getSnapshotPorCategoria(Long idActividad, Long categoriaId) {
        Actividad actividad = actividadService.findById(idActividad);
        
        Map<String, Object> result = new HashMap<>();
        result.put("actividad", mapActividad(actividad));
        result.put("categorias", obtenerCategorias(idActividad));
        result.put("jurados", obtenerJuradosPorCategoria(idActividad, categoriaId));
        result.put("filas", obtenerFilasPorCategoria(idActividad, categoriaId));
        result.put("resumenGlobal", calcularResumenPorCategoria(idActividad, categoriaId));
        result.put("tipoVista", "categoria");
        result.put("categoriaSeleccionada", categoriaId);
        
        return result;
    }
    
    /**
     * Snapshot TOTAL (suma de todas las categorías)
     */
    public Map<String, Object> getSnapshotTotal(Long idActividad) {
        Actividad actividad = actividadService.findById(idActividad);
        
        Map<String, Object> result = new HashMap<>();
        result.put("actividad", mapActividad(actividad));
        result.put("categorias", obtenerCategorias(idActividad));
        result.put("jurados", new ArrayList<>()); // No se muestran columnas individuales en vista total
        result.put("filas", obtenerFilasTotal(idActividad));
        result.put("resumenGlobal", calcularResumenTotal(idActividad));
        result.put("tipoVista", "total");
        
        return result;
    }
    
    /**
     * Obtener categorías con su progreso
     */
    private List<Map<String, Object>> obtenerCategorias(Long idActividad) {
        List<CategoriaActividad> categorias = categoriaService.findByActividad(idActividad);
        
        return categorias.stream()
                .filter(c -> "A".equals(c.getEstado()))
                .map(cat -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", cat.getIdCategoriaActividad());
                    map.put("nombre", cat.getNombre());
                    map.put("fase", cat.getFase());
                    
                    // Calcular progreso
                    Map<String, Object> progreso = calcularProgreso(idActividad, cat.getIdCategoriaActividad());
                    map.put("progreso", progreso);
                    
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener jurados de una actividad
     */
    private List<Map<String, Object>> obtenerJurados(Long idActividad) {
        List<JuradoAsignacion> asignaciones = juradoAsignacionService.findByActividad(idActividad);
        
        return asignaciones.stream()
                .filter(a -> a.getJurado() != null && "A".equals(a.getJurado().getEstado()))
                .map(a -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", a.getJurado().getIdJurado());
                    map.put("nombre", a.getJurado().getPersona().getNombreCompleto());
                    map.put("categoria", a.getCategoriaActividad().getNombre());
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener jurados de una categoría específica
     */
    private List<Map<String, Object>> obtenerJuradosPorCategoria(Long idActividad, Long categoriaId) {
        List<JuradoAsignacion> asignaciones = juradoAsignacionService
                .findByActividadAndCategoria(idActividad, categoriaId);
        
        return asignaciones.stream()
                .filter(a -> a.getJurado() != null && "A".equals(a.getJurado().getEstado()))
                .map(a -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", a.getJurado().getIdJurado());
                    map.put("nombre", a.getJurado().getPersona().getNombreCompleto());
                    return map;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener filas por categoría (vista individual)
     */
    private List<Map<String, Object>> obtenerFilasPorCategoria(Long idActividad) {
        List<Inscripcion> inscripciones = inscripcionService.findByActividad(idActividad);
        
        return inscripciones.stream()
                .filter(i -> i.getParticipante() != null && "A".equals(i.getParticipante().getEstado()))
                .map(inscripcion -> {
                    Map<String, Object> fila = new HashMap<>();
                    
                    Participante p = inscripcion.getParticipante();
                    CategoriaActividad cat = inscripcion.getCategoriaActividad();
                    
                    fila.put("inscripcionId", inscripcion.getIdInscripcion());
                    fila.put("participante", p.getNombre());
                    fila.put("participanteId", p.getIdParticipante()); 
                    fila.put("institucion", p.getInstitucion());
                    fila.put("categoriaId", cat.getIdCategoriaActividad());
                    fila.put("categoriaNombre", cat.getNombre());
                    
                    // Obtener evaluaciones de esta inscripción
                    List<Evaluacion> evaluaciones = evaluacionService
                            .findByParticipanteAndCategoria(p.getIdParticipante(), cat.getIdCategoriaActividad());
                    
                    Map<Long, Double> scores = new HashMap<>();
                    double suma = 0;
                    int count = 0;
                    
                    for (Evaluacion ev : evaluaciones) {
                        if (ev.getJurado() != null) {
                            scores.put(ev.getJurado().getIdJurado(), ev.getTotalPonderacion());
                            suma += ev.getTotalPonderacion();
                            count++;
                        }
                    }
                    
                    fila.put("scores", scores);
                    fila.put("promedio", count > 0 ? suma / count : 0);
                    
                    // Verificar si está completo (todos los jurados de esta categoría evaluaron)
                    int juradosEsperados = juradoAsignacionService
                            .countByActividadAndCategoria(idActividad, cat.getIdCategoriaActividad());
                    fila.put("completado", count >= juradosEsperados);
                    
                    return fila;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener filas filtradas por una categoría
     */
    private List<Map<String, Object>> obtenerFilasPorCategoria(Long idActividad, Long categoriaId) {
        return obtenerFilasPorCategoria(idActividad).stream()
                .filter(f -> categoriaId.equals(f.get("categoriaId")))
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener filas TOTALES (suma de categorías por participante)
     */
    private List<Map<String, Object>> obtenerFilasTotal(Long idActividad) {
        List<Inscripcion> inscripciones = inscripcionService.findByActividad(idActividad);
        
        // Agrupar por participante
        Map<Long, List<Inscripcion>> porParticipante = inscripciones.stream()
                .filter(i -> i.getParticipante() != null && "A".equals(i.getParticipante().getEstado()))
                .collect(Collectors.groupingBy(i -> i.getParticipante().getIdParticipante()));
        
        return porParticipante.entrySet().stream()
                .map(entry -> {
                    Long idParticipante = entry.getKey();
                    List<Inscripcion> inscrips = entry.getValue();
                    
                    Participante p = inscrips.get(0).getParticipante();
                    
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("participante", p.getNombre());
                    fila.put("institucion", p.getInstitucion());
                    
                    // Sumar puntos de todas las categorías
                    double puntosTotal = 0;
                    Map<String, Double> desglose = new HashMap<>();
                    boolean completoTotal = true;
                    
                    for (Inscripcion insc : inscrips) {
                        CategoriaActividad cat = insc.getCategoriaActividad();
                        
                        List<Evaluacion> evaluaciones = evaluacionService
                                .findByParticipanteAndCategoria(idParticipante, cat.getIdCategoriaActividad());
                        
                        double sumaCategoria = 0;
                        int countCategoria = 0;
                        
                        for (Evaluacion ev : evaluaciones) {
                            sumaCategoria += ev.getTotalPonderacion();
                            countCategoria++;
                        }
                        
                        double promedioCategoria = countCategoria > 0 ? sumaCategoria / countCategoria : 0;
                        puntosTotal += promedioCategoria;
                        
                        desglose.put(cat.getNombre(), promedioCategoria);
                        
                        // Verificar si esta categoría está completa
                        int juradosEsperados = juradoAsignacionService
                                .countByActividadAndCategoria(idActividad, cat.getIdCategoriaActividad());
                        
                        if (countCategoria < juradosEsperados) {
                            completoTotal = false;
                        }
                    }
                    
                    fila.put("puntos", puntosTotal);
                    fila.put("desglose", desglose);
                    fila.put("completado", completoTotal);
                    fila.put("participanteId", idParticipante);
                    
                    return fila;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Calcular progreso de una categoría
     */
    private Map<String, Object> calcularProgreso(Long idActividad, Long categoriaId) {
        List<Inscripcion> inscripciones = inscripcionService
                .findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(idActividad, categoriaId);
        
        int total = inscripciones.size();
        int completadas = 0;
        
        int juradosEsperados = juradoAsignacionService
                .countByActividadAndCategoria(idActividad, categoriaId);
        
        for (Inscripcion insc : inscripciones) {
            int evaluaciones = evaluacionService
                    .countByParticipanteAndCategoria(
                        insc.getParticipante().getIdParticipante(), 
                        categoriaId
                    );
            
            if (evaluaciones >= juradosEsperados) {
                completadas++;
            }
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("completadas", completadas);
        map.put("porcentaje", total > 0 ? (completadas * 100.0 / total) : 0);
        
        return map;
    }
    
    /**
     * Calcular resumen global
     */
    private Map<String, Object> calcularResumenGlobal(Long idActividad) {
        List<CategoriaActividad> categorias = categoriaService.findByActividad(idActividad);
        
        int totalEvaluaciones = 0;
        int completadas = 0;
        
        for (CategoriaActividad cat : categorias) {
            Map<String, Object> prog = calcularProgreso(idActividad, cat.getIdCategoriaActividad());
            totalEvaluaciones += (int) prog.get("total");
            completadas += (int) prog.get("completadas");
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("total", totalEvaluaciones);
        map.put("completadas", completadas);
        map.put("porcentaje", totalEvaluaciones > 0 ? (completadas * 100.0 / totalEvaluaciones) : 0);
        
        return map;
    }
    
    /**
     * Resumen por categoría
     */
    private Map<String, Object> calcularResumenPorCategoria(Long idActividad, Long categoriaId) {
        return calcularProgreso(idActividad, categoriaId);
    }
    
    /**
     * Resumen total
     */
    private Map<String, Object> calcularResumenTotal(Long idActividad) {
        List<Map<String, Object>> filas = obtenerFilasTotal(idActividad);
        
        int total = filas.size();
        int completadas = (int) filas.stream().filter(f -> (boolean) f.get("completado")).count();
        
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("completadas", completadas);
        map.put("porcentaje", total > 0 ? (completadas * 100.0 / total) : 0);
        
        return map;
    }
    
    private Map<String, Object> mapActividad(Actividad a) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", a.getIdActividad());
        map.put("nombre", a.getNombre());
        map.put("tipo", a.getTipo());
        map.put("fecha", a.getFecha());
        return map;
    }
    
    /**
     * Genera reporte PDF por categoría
     */
    public byte[] generarReportePorCategoria(Long idActividad, Long idCategoria) {
        return reportePdfService.generarReportePorCategoria(idActividad, idCategoria);
    }
    
    /**
     * Genera reporte PDF total
     */
    public byte[] generarReporteTotal(Long idActividad) {
        return reportePdfService.generarReporteTotal(idActividad);
    }
    
    /**
     * Genera reporte PDF por participante en una categoría
     */
    public byte[] generarReportePorParticipanteCategoria(Long idParticipante, Long idCategoria) {
        return reportePdfService.generarReportePorParticipanteCategoria(idParticipante, idCategoria);
    }

    /**
     * Genera reporte completo de un participante (todas las categorías)
     */
    public byte[] generarReporteCompletoParticipante(Long idActividad, Long idParticipante) {
        return reportePdfService.generarReporteCompletoParticipante(idActividad, idParticipante);
    }
}
