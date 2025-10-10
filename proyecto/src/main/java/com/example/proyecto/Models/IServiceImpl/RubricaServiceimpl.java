package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IRubricaDao;
import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.IService.IRubricaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RubricaServiceimpl implements IRubricaService {
    
    private final IRubricaDao dao;

    @Override
    public List<Rubrica> findAll() {
        return dao.findAll();
    }

    @Override
    public Rubrica findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public Rubrica save(Rubrica entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public Optional<Rubrica> buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    @Override
    public List<Rubrica> listarRubrica() {
        return dao.listarRubrica();
    }

    @Override
    public List<Rubrica> findByActividadOrder(Long actId) {
        return dao.findByActividadOrder(actId);
    }
}
