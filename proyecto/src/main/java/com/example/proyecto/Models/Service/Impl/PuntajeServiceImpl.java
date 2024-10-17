package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IPuntajeDao;
import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.Entity.Puntaje;
import com.example.proyecto.Models.Service.IPuntajeService;

@Service
public class PuntajeServiceImpl implements IPuntajeService{
    
    @Autowired
    private IPuntajeDao puntajeDao;

    @Override
    public List<Puntaje> findAll() {
          return (List<Puntaje>) puntajeDao.findAll();
    }

    @Override
    public void save(Puntaje puntaje) {
        puntajeDao.save(puntaje);
    }

    @Override
    public Puntaje findOne(Long id) {
        return puntajeDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        puntajeDao.deleteById(id);
    }

    @Override
    public List<Puntaje> obtenerPuntajesPorProyecto(Long id_proyecto) {
        // TODO Auto-generated method stub
        return puntajeDao.obtenerPuntajesPorProyecto(id_proyecto);
    }

    @Override
    public Puntaje puntajePonderacionEvaluacionJurado(Long idJurado, Long idEvaluacion, Long idPonderacion) {
        return puntajeDao.puntajePonderacionEvaluacionJurado(idJurado, idEvaluacion, idPonderacion);
    }

    @Override
    public List<Puntaje> puntajesEvaluacionJurado(Long idJurado, Long idEvaluacion) {
        return puntajeDao.puntajesEvaluacionJurado(idJurado, idEvaluacion);
    }

    @Override
    public Puntaje puntajePonderacionJurado(Long idJurado, Long idPonderacion) {
        return puntajeDao.puntajePonderacionJurado(idJurado, idPonderacion);
    }

    @Override
    public Puntaje puntajePonderacionJuradoProyecto(Long idJurado, Long idPonderacion, Long idProyecto) {
        return puntajeDao.puntajePonderacionJuradoProyecto(idJurado, idPonderacion, idProyecto);
    }

    @Override
    public List<Puntaje> puntajesPonderacionProyecto(Long idPonderacion, Long idProyecto) {
        return puntajeDao.puntajesPonderacionProyecto(idPonderacion, idProyecto);
    }
    
}
