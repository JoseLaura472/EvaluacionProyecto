package com.example.proyecto.Models.Dto;

import java.util.Date;

import com.example.proyecto.Models.Entity.Actividad;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ActividadMiniDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Date fecha;

    public static ActividadMiniDto of(Actividad a) {
        var d = new ActividadMiniDto();
        d.id = a.getIdActividad();
        d.nombre = a.getNombre();
        d.descripcion = a.getDescripcion();
        return d;
    }
}
