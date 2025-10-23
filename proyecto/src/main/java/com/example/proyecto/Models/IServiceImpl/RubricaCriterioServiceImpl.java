package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IRubricaCriterioDao;
import com.example.proyecto.Models.Dto.RubricaCriterioDto;
import com.example.proyecto.Models.Entity.RubricaCriterio;
import com.example.proyecto.Models.IService.IRubricaCriterioServcie;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RubricaCriterioServiceImpl implements IRubricaCriterioServcie {
    
    private final IRubricaCriterioDao dao;

    @Override
    public List<RubricaCriterio> findAll() {
        return dao.findAll();
    }

    @Override
    public RubricaCriterio findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public RubricaCriterio save(RubricaCriterio entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public Optional<RubricaCriterio> buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    @Override
    public List<RubricaCriterio> listarRubricaCriterio() {
        return dao.listarRubricaCriterio();
    }

    @Override
    public List<RubricaCriterio> findByRubrica(Long rubricaId) {
        return dao.findByRubrica(rubricaId);
    }

    @Override
    public List<RubricaCriterioDto> listarCriteriosDto(Long rubricaId) {
        return dao.listarCriteriosDto(rubricaId);
    }
}