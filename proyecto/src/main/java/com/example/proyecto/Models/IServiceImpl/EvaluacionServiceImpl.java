package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Evaluacion> findByJuradoPersonaAndParticipanteAndRubrica(Long idPersonaJurado, Long idParticipante,
            Long idRubrica) {
        return evaluacionDao.findByJuradoPersonaAndParticipanteAndRubrica(idPersonaJurado, idParticipante, idRubrica);
    }

    @Override
    public boolean existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJuradoAndRubrica_IdRubrica(
            Long actividadId, Long inscripcionId, Long juradoId, Long rubricaId) {
        return evaluacionDao.existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJuradoAndRubrica_IdRubrica(
            actividadId, inscripcionId, juradoId, rubricaId);
    }

    /* para evaluacion entrada univeristaria */

    @Override
    public boolean existsByInscripcionAndJurado(Long idInscripcion, Long idJurado) {
        return evaluacionDao.existsByInscripcionIdInscripcionAndJuradoIdJurado(idInscripcion, idJurado);
    }

    @Override
    public boolean existeEvaluacion(Long idJurado, Long idParticipante, Long idCategoria) {
        return evaluacionDao.existsByJuradoIdJuradoAndParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(idJurado, idParticipante, idCategoria);
    }

    @Override
    public Evaluacion findByJuradoAndParticipanteAndCategoria(Long idJurado, Long idParticipante, Long idCategoria) {
        return evaluacionDao.findByJuradoIdJuradoAndParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(idJurado, idParticipante, idCategoria);
    }

    @Override
    public List<Evaluacion> findByParticipanteAndCategoria(Long idParticipante, Long idCategoria) {
        return evaluacionDao.findByParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(idParticipante, idCategoria);
    }

    @Override
    public int countByParticipanteAndCategoria(Long idParticipante, Long idCategoria) {
        return evaluacionDao.countByParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(idParticipante, idCategoria);
    }
}
