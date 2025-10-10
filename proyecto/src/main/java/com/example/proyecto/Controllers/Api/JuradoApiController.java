package com.example.proyecto.Controllers.Api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyecto.Models.Dto.ActividadMiniDto;
import com.example.proyecto.Models.Dto.CategoriaDto;
import com.example.proyecto.Models.Dto.EvaluacionGuardarDto;
import com.example.proyecto.Models.Dto.InscripcionPendienteDto;
import com.example.proyecto.Models.Dto.RubricaDto;
import com.example.proyecto.Models.Dto.SimpleResponse;
import com.example.proyecto.Models.Service.EvaluacionService;
import com.example.proyecto.Models.Service.JuradoContextService;
import com.example.proyecto.Models.Service.JuradoQueryService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jurado")
@RequiredArgsConstructor
public class JuradoApiController {
    private final JuradoContextService ctx;
    private final JuradoQueryService query;
    private final EvaluacionService evaluacionService;

    @GetMapping("/actividades")
    public List<ActividadMiniDto> actividades(HttpSession session) {
        var jurado = ctx.resolveFromSession(session);
        return query.actividadesAsignadas(jurado.getIdJurado());
    }

    @GetMapping("/{actId}/categorias")
    public List<CategoriaDto> categorias(@PathVariable Long actId) {
        return query.categorias(actId);
    }

    @GetMapping("/{actId}/inscripciones-pendientes")
    public List<InscripcionPendienteDto> pendientes(HttpSession session,
            @PathVariable Long actId,
            @RequestParam(required = false) Long categoriaId) {
        var jurado = ctx.resolveFromSession(session);
        return query.pendientes(actId, categoriaId, jurado.getIdJurado());
    }

    @GetMapping("/{actId}/rubrica")
    public RubricaDto rubrica(@PathVariable Long actId) {
        return query.rubricaDeActividad(actId);
    }

    @PostMapping("/evaluar")
    public ResponseEntity<SimpleResponse> evaluar(HttpSession session,
            @RequestBody EvaluacionGuardarDto dto) {
        var jurado = ctx.resolveFromSession(session);
        evaluacionService.guardarEvaluacion(jurado, dto);
        return ResponseEntity.ok(new SimpleResponse(true, "OK"));
    }
}
