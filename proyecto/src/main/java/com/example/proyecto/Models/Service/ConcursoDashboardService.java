package com.example.proyecto.Models.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dto.ActividadMiniDto;
import com.example.proyecto.Models.Dto.DashboardSnapshotDto;
import com.example.proyecto.Models.Entity.CategoriaActividad;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.IService.IActividadService;
import com.example.proyecto.Models.IService.ICategoriaActividadService;
import com.example.proyecto.Models.IService.IEvaluacionService;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IRubricaService;

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
}
