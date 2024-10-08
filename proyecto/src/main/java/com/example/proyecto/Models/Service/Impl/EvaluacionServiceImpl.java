package com.example.proyecto.Models.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IEvaluacionDao;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Service.IEvaluacionService;

@Service
public class EvaluacionServiceImpl implements IEvaluacionService {


    @Autowired
    private IEvaluacionDao evaluacionDao;

    @Override
    public List<Evaluacion> findAll() {
    return (List<Evaluacion>) evaluacionDao.findAll();

    }

    @Override
    public void save(Evaluacion evaluacion) {
        evaluacionDao.save(evaluacion);
        
    }

    @Override
    public Evaluacion findOne(Long id) {
        return evaluacionDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        evaluacionDao.deleteById(id);
    }

    @Override
    public  List<Evaluacion>  juradoEvaluacion(Long id_jurado) {
        return (List<Evaluacion>) evaluacionDao.juradoEvaluacion(id_jurado);
    }

    @Override
    public List<Evaluacion> findByProyectoId(Long proyectoId) {
           return (List<Evaluacion>) evaluacionDao.findByProyectoId(proyectoId);
    }

    @Override
    public List<Evaluacion> obtenerNotasFinales(Long id_proyecto) {
        // TODO Auto-generated method stub
        return evaluacionDao.obtenerNotasFinales(id_proyecto);
    }
    
}
