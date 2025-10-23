package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IParticipanteCategoriaDao;
import com.example.proyecto.Models.Dto.ParticipanteListadoDto;
import com.example.proyecto.Models.Entity.ParticipanteCategoria;
import com.example.proyecto.Models.IService.IParticipanteCategoriaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipantecategoriaServiceImpl implements IParticipanteCategoriaService {
    private final IParticipanteCategoriaDao dao;

    @Override
    public List<ParticipanteCategoria> findAll() {
        return dao.findAll();
    }

    @Override
    public ParticipanteCategoria findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public ParticipanteCategoria save(ParticipanteCategoria entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public List<ParticipanteListadoDto> listarParticipantesPorCategoria(Long categoriaId, boolean soloActivos) {
        return dao.listarParticipantesPorCategoria(categoriaId, soloActivos);
    } 
}
