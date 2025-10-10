package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IEvaluacionDetalleDao;
import com.example.proyecto.Models.Entity.EvaluacionDetalle;
import com.example.proyecto.Models.IService.IEvaluacionDetalleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluacionDetalleServiceImpl implements IEvaluacionDetalleService {
    
    private final IEvaluacionDetalleDao dao;

    @Override
    public List<EvaluacionDetalle> findAll() {
        return dao.findAll();
    }

    @Override
    public EvaluacionDetalle findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public EvaluacionDetalle save(EvaluacionDetalle entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }
}