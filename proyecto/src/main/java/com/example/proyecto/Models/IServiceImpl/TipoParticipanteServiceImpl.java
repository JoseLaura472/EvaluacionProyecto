package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.ITipoParticipanteDao;
import com.example.proyecto.Models.Entity.TipoParticipante;
import com.example.proyecto.Models.IService.ITipoParticipanteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoParticipanteServiceImpl implements ITipoParticipanteService {
    private final ITipoParticipanteDao dao;

    @Override
    public List<TipoParticipante> findAll() {
        return dao.findAll();
    }

    @Override
    public TipoParticipante findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public TipoParticipante save(TipoParticipante entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public Optional<TipoParticipante> buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    @Override
    public List<TipoParticipante> listarTipoParticipantes() {
        return dao.listarTipoParticipantes();
    }
}
