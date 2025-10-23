package com.example.proyecto.Models.IService;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Dto.ParticipanteListadoDto;
import com.example.proyecto.Models.Entity.ParticipanteCategoria;
import com.example.proyecto.Models.Service.IServiceGenerico;

public interface IParticipanteCategoriaService extends IServiceGenerico<ParticipanteCategoria, Long>{
    List<ParticipanteListadoDto> listarParticipantesPorCategoria(
        @Param("categoriaId") Long categoriaId,
        @Param("soloActivos") boolean soloActivos
    );
}
