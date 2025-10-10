package com.example.proyecto.Controllers.Api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.proyecto.Models.Dto.DashboardSnapshotDto;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.Service.ConcursoDashboardService;
import com.example.proyecto.Models.Service.SseHub;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/concurso")
@RequiredArgsConstructor
public class ConcursoDashboardApi {
    private final ConcursoDashboardService dashService;
    private final SseHub sseHub; // ver sección 3
    private final IJuradoAsignacionService asignacionRepo;

    @GetMapping("/{actId}/snapshot")
    public DashboardSnapshotDto snapshot(@PathVariable Long actId,
                                        @RequestParam(required=false) Long categoriaId) {
    if (actId == null || actId <= 0) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "actividadId inválido");
    }
    return dashService.snapshot(actId, categoriaId);
    }

    @GetMapping(value = "/{actId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable Long actId) {
        return sseHub.subscribe(actId);
    }

    public List<DashboardSnapshotDto.JuradoMiniDto> juradosDeActividad(Long actividadId) {
        return asignacionRepo.findJuradosByActividadOrdenAsignacion(actividadId)
                .stream()
                .map(j -> {
                    var d = new DashboardSnapshotDto.JuradoMiniDto();
                    d.setId(j.getIdJurado());
                    // ajusta a tu formato de nombre:
                    d.setNombre(j.getPersona() != null ? j.getPersona().getNombres() : ("Jurado #" + j.getIdJurado()));
                    return d;
                })
                .toList();
    }
}
