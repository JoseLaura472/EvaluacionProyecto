package com.example.proyecto.Models.Dto;

import com.example.proyecto.Models.Entity.Inscripcion;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InscripcionPendienteDto {
    private Long id;
    private String participanteNombre;
    private String institucion;
    private Long categoriaId;
    private String categoriaNombre;
    private Integer orden; // opcional

    public static InscripcionPendienteDto of(Inscripcion i) {
        var d = new InscripcionPendienteDto();
        d.id = i.getIdInscripcion();
        d.participanteNombre = i.getParticipante().getNombre();
        d.institucion = i.getParticipante().getInstitucion();
        d.categoriaId = i.getCategoriaActividad().getIdCategoriaActividad();
        d.categoriaNombre = i.getCategoriaActividad().getNombre();
        d.orden = null;
        return d;
    }
}
