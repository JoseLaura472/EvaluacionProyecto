package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.ICategoriaParticipanteDao;
import com.example.proyecto.Models.Entity.CategoriaParticipante;
import com.example.proyecto.Models.IService.ICategoriaParticipanteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaParticipanteServiceImpl implements ICategoriaParticipanteService {
    
    private final ICategoriaParticipanteDao dao;
    
    @Override
    public List<CategoriaParticipante> findAll() {
        return dao.findAll();
    }

    @Override
    public CategoriaParticipante findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public CategoriaParticipante save(CategoriaParticipante entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public CategoriaParticipante findByNombre(String nombre) {
        return dao.findByNombre(nombre);
    }
    
}
