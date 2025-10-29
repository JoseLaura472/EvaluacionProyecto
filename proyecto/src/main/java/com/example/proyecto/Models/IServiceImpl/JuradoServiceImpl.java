package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IJuradoDao;
import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.IService.IJuradoService;

@Service
public class JuradoServiceImpl implements IJuradoService {
    
    @Autowired
    private IJuradoDao juradoDao;

    @Override
    public List<Jurado> findAll() {
         return (List<Jurado>) juradoDao.findAll();
    }

    @Override
    public void save(Jurado jurado) {
        juradoDao.save(jurado);
    }

    @Override
    public Jurado findOne(Long id) {
        return juradoDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        juradoDao.deleteById(id);
    }

    @Override
    public Jurado juradoPorIdPersona(Long idPersona) {
        return juradoDao.juradoPorIdPersona(idPersona);
    }

    @Override
    public List<Jurado> findByProyectoId(Long proyectoId) {
      return (List<Jurado>) juradoDao.findByProyectoId(proyectoId);
    }

    @Override
    public Jurado findByNombreCompleto(String nombreCompleto) {
        return juradoDao.findByNombreCompleto(nombreCompleto);
    }

    @Override
    public List<Jurado> listarParticipantes() {
        return juradoDao.listarParticipantes();
    }

    @Override
    public boolean existsByPersona_IdPersona(Long idPersona) {
        return juradoDao.existsByPersona_IdPersona(idPersona);
    }

    @Override
    public Optional<Jurado> findActivoByPersonaId(Long idPersona) {
        return juradoDao.findActivoByPersonaId(idPersona);
    }

    @Override
    public List<Actividad> findActividadesByJurado(Long juradoId) {
        return juradoDao.findActividadesByJurado(juradoId);
    }

    @Override
    public Jurado findByPersonaId(Long idPersona) {
        return juradoDao.findByPersona_IdPersona(idPersona);
    }
}
