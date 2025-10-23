package com.example.proyecto.Models.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dto.ActividadMiniDto;
import com.example.proyecto.Models.Dto.CategoriaDto;
import com.example.proyecto.Models.Dto.InscripcionPendienteDto;
import com.example.proyecto.Models.Dto.RubricaCriterioDto;
import com.example.proyecto.Models.Dto.RubricaDto;
import com.example.proyecto.Models.IService.ICategoriaActividadService;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IRubricaCriterioServcie;
import com.example.proyecto.Models.IService.IRubricaService;

import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class JuradoQueryService {
    private final IJuradoAsignacionService asignacionRepo;
    private final ICategoriaActividadService categoriaRepo;
    private final IRubricaService rubricaRepo;
    private final IRubricaCriterioServcie criterioRepo;
    private final IInscripcionService inscripcionRepo;

    public List<ActividadMiniDto> actividadesAsignadas(Long idJurado) {
        return asignacionRepo.findActividadesAsignadas(idJurado)
                .stream().map(ActividadMiniDto::of).toList();
    }

    public List<CategoriaDto> categorias(Long actId) {
        return categoriaRepo.findByActividad(actId).stream()
                .map(c -> new CategoriaDto(c.getIdCategoriaActividad(), c.getNombre()))
                .toList();
    }

    public RubricaDto rubricaDeActividad(Long actId) {
        var rub = rubricaRepo.findByActividadOrder(actId).stream().findFirst()
            .orElseThrow(() -> new IllegalStateException("No hay rúbrica para la actividad"));

        List<RubricaCriterioDto> crits = criterioRepo.findByRubrica(rub.getIdRubrica())
            .stream()
            .map(c -> new RubricaCriterioDto(
                    c.getIdRubricaCriterio(),
                    c.getNombre(),
                    c.getPorcentaje(),   // Integer
                    c.getMaxPuntos(),    // Integer
                    c.getDescripcion()
            ))
            .toList();

        return new RubricaDto(
            rub.getIdRubrica(),
            rub.getNombre(),
            rub.getVersion(),
            crits
        );
    }


    public List<InscripcionPendienteDto> pendientes(Long actId, Long catId, Long idJurado) {
        return inscripcionRepo.findPendientesByActividadAndCategoriaExcluyendoEvaluadas(actId, catId, idJurado)
                .stream().map(InscripcionPendienteDto::of).toList();
    }
}
