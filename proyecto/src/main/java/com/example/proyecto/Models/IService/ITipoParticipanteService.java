package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Entity.TipoParticipante;
import com.example.proyecto.Models.Service.IServiceGenerico;

@Service
public interface ITipoParticipanteService extends IServiceGenerico<TipoParticipante, Long>{
    Optional<TipoParticipante> buscarPorNombre(String nombre);
    List<TipoParticipante> listarTipoParticipantes();
}
