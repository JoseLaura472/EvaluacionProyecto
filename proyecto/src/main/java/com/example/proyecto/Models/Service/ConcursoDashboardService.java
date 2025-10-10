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
import com.example.proyecto.Models.IService.IActividadService;
import com.example.proyecto.Models.IService.ICategoriaActividadService;
import com.example.proyecto.Models.IService.IEvaluacionService;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConcursoDashboardService {
    private final IEvaluacionService evaluacionRepo;
    private final IJuradoAsignacionService asignacionRepo;
    private final ICategoriaActividadService categoriaRepo;
    private final IInscripcionService inscripcionRepo;
    private final IActividadService actividadRepo;

    // Jurados para columnas
    public List<DashboardSnapshotDto.JuradoMiniDto> juradosDeActividad(Long actividadId) {
        return asignacionRepo.findJuradosByActividadOrdenAsignacion(actividadId)
                .stream()
                .map(j -> {
                    var d = new DashboardSnapshotDto.JuradoMiniDto();
                    d.setId(j.getIdJurado()); // OJO: tu entidad tiene id_jurado
                    d.setNombre(j.getPersona() != null ? j.getPersona().getNombres() : ("Jurado #" + j.getIdJurado()));
                    return d;
                })
                .toList();
    }

    // Snapshot completo
    public DashboardSnapshotDto snapshot(Long actividadId, Long categoriaId) {
        var dto = new DashboardSnapshotDto();

        // Actividad → Mini DTO
        var actEntity = actividadRepo.findById(actividadId);
        var actDto = new ActividadMiniDto();
        actDto.setId(actEntity.getIdActividad());
        actDto.setNombre(actEntity.getNombre());
        actDto.setDescripcion(actEntity.getDescripcion());
        dto.setActividad(actDto);

        // Categorías de la actividad
        var cats = categoriaRepo.findByActividad(actividadId);

        // Conteo de inscripciones por categoría (map<Long catId, Long total>)
        Map<Long, Long> inscPorCat = cats.stream().collect(Collectors.toMap(
                CategoriaActividad::getIdCategoriaActividad,
                c -> inscripcionRepo.countByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(
                        actividadId, c.getIdCategoriaActividad())));

        // Jurados (columnas)
        var jurados = juradosDeActividad(actividadId);
        dto.setJurados(jurados);
        int totalJurados = jurados.size();

        // Inscripciones (trae todas y filtramos en memoria por categoría si hace falta)
        List<Inscripcion> inscs = (categoriaId == null)
                ? inscripcionRepo.findByActividad_IdActividad(actividadId)
                : inscripcionRepo.findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(actividadId,
                        categoriaId);

        // Evaluaciones agrupadas por inscripción
        Map<Long, List<Evaluacion>> evalsByInsc = evaluacionRepo.findByActividad_IdActividad(actividadId)
                .stream()
                .collect(Collectors.groupingBy(e -> e.getInscripcion().getIdInscripcion()));

        // Armar filas
        List<DashboardSnapshotDto.FilaDto> filas = new ArrayList<>();
        Map<Long, Integer> completadasPorCat = new HashMap<>();

        for (var i : inscs) {
            var f = new DashboardSnapshotDto.FilaDto();
            f.setInscripcionId(i.getIdInscripcion());
            f.setCategoriaId(i.getCategoriaActividad().getIdCategoriaActividad());
            f.setParticipante(i.getParticipante().getNombre());
            f.setInstitucion(i.getParticipante().getInstitucion());

            var evals = evalsByInsc.getOrDefault(i.getIdInscripcion(), List.of());
            for (var e : evals) {
                // OJO con el getter de jurado: tu campo es id_jurado
                f.getScores().put(e.getJurado().getIdJurado(), e.getTotalPonderacion());
            }

            if (!f.getScores().isEmpty()) {
                f.setPromedio(f.getScores().values().stream().mapToDouble(Double::doubleValue).average().orElse(0));
            } else {
                f.setPromedio(null);
            }

            f.setCompletado(f.getScores().size() == totalJurados);
            if (f.isCompletado()) {
                completadasPorCat.merge(f.getCategoriaId(), 1, Integer::sum);
            }
            filas.add(f);
        }
        dto.setFilas(filas);

        // Categorías con progreso
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

        // Resumen global
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
