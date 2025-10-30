package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IInscripcionDao;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.IService.IInscripcionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InscripcionServiceImpl implements IInscripcionService{
    
    private final IInscripcionDao dao;

    @Override
    public List<Inscripcion> findAll() {
        return dao.findAll();
    }

    @Override
    public Inscripcion findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public Inscripcion save(Inscripcion entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public List<Inscripcion> listarInscripciones() {
        return dao.listarInscripciones();
    }

    @Override
    public List<Inscripcion> findPendientesByActividadAndCategoriaExcluyendoEvaluadas(Long actividadId,
            Long categoriaId, Long idJurado) {
        return dao.findPendientesByActividadAndCategoriaExcluyendoEvaluadas(actividadId, categoriaId, idJurado);
    }

    @Override
    public long countByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(Long actividadId,
            Long categoriaId) {
        return dao.countByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(actividadId, categoriaId);
    }

    @Override
    public List<Inscripcion> findByActividad_IdActividad(Long actividadId) {
        return dao.findByActividad_IdActividad(actividadId);
    }

    @Override
    public List<Inscripcion> findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(Long actividadId,
            Long categoriaId) {
        return dao.findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(actividadId, categoriaId);
    }

    @Override
    public Optional<Inscripcion> fetchFull(Long actId, Long inscId) {
        return dao.fetchFull(actId, inscId);
    }

    /* para entrada universitaria */

    @Override
    public Inscripcion findByParticipanteAndFase(Long idParticipante, String fase) {
        return dao.findByParticipanteIdParticipanteAndCategoriaActividadFase(idParticipante, fase)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Inscripcion no encontrada para participante " + idParticipante + " y fase " + fase));
    }

    @Override
    public List<Inscripcion> findByCategoria(Long idCategoriaActividad) {
        return dao.findByCategoriaActividadIdCategoriaActividad(idCategoriaActividad);
    }

    @Override
    public List<Inscripcion> findByActividad(Long idActividad) {
        return dao.findByActividad_IdActividad(idActividad);
    }

    @Override
    public Inscripcion findByActividadAndParticipante(Long idActividad, Long idParticipante) {
        return dao.findByActividadIdActividadAndParticipanteIdParticipante(idActividad, idParticipante);
    }

    @Override
    public Inscripcion findByActividadParticipanteAndCategoria(Long idActividad, Long idParticipante,
            Long idCategoriaActividad) {
       return dao.findByActividadIdActividadAndParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(idActividad, idParticipante, idCategoriaActividad);
    }
}
