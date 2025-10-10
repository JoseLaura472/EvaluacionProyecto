package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IEvaluacionDao;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.IService.IEvaluacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluacionServiceImpl implements IEvaluacionService {

    private final IEvaluacionDao evaluacionDao;

    @Override
    public List<Evaluacion> findAll() {
        return evaluacionDao.findAll();
    }

    @Override
    public Evaluacion findById(Long idEntidad) {
        return evaluacionDao.findById(idEntidad).orElse(null);
    }

    @Override
    public Evaluacion save(Evaluacion entidad) {
        return evaluacionDao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        evaluacionDao.deleteById(idEntidad);
    }

    @Override
    public boolean existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJurado(Long actId, Long inscId,
            Long juradoId) {
        return evaluacionDao.existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJurado(actId, inscId, juradoId);
    }

    @Override
    public List<Evaluacion> findByActividad_IdActividad(Long actividadId) {
        return evaluacionDao.findByActividad_IdActividad(actividadId);
    }

    @Override
    public List<Evaluacion> findFullByActividadAndInscripcion(Long actId, Long inscId) {
        return evaluacionDao.findFullByActividadAndInscripcion(actId, inscId);
    }

}
