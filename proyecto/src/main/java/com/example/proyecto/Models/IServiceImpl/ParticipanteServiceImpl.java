package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IParticipanteDao;
import com.example.proyecto.Models.Entity.Participante;
import com.example.proyecto.Models.IService.IParticipanteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipanteServiceImpl implements IParticipanteService {

    private final IParticipanteDao dao;

    @Override
    public List<Participante> findAll() {
        return dao.findAll();
    }

    @Override
    public Participante findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public Participante save(Participante entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public Optional<Participante> buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    @Override
    public List<Participante> listarParticipantes() {
        return dao.listarParticipantes();
    }
}
