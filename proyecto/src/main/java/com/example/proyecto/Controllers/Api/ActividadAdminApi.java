package com.example.proyecto.Controllers.Api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyecto.Models.Dto.ActividadMiniDto;
import com.example.proyecto.Models.IService.IActividadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/actividades")
@RequiredArgsConstructor
public class ActividadAdminApi {
    private final IActividadService actividadRepo;

    @GetMapping
    public List<ActividadMiniDto> listar() {
        return actividadRepo.findAllOrderByFechaDesc().stream().map(a -> {
            var d = new ActividadMiniDto();
            d.setId(a.getIdActividad());
            d.setNombre(a.getNombre());
            d.setDescripcion(a.getDescripcion());
            d.setFecha(a.getFecha());
            return d;
        }).toList();
    }
}
