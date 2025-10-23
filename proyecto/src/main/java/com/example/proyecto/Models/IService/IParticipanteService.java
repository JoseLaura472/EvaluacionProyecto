package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dto.ParticipanteListadoDto;
import com.example.proyecto.Models.Entity.Participante;
import com.example.proyecto.Models.Service.IServiceGenerico;

@Service
public interface IParticipanteService extends IServiceGenerico<Participante, Long> {
    Optional<Participante> buscarPorNombre(String nombre);
    List<Participante> listarParticipantes();
    List<ParticipanteListadoDto> listarPorCategoria(Long categoriaId);
    List<ParticipanteListadoDto> listarPendientesPorCategoria(Long categoriaId, Long juradoId);

}
