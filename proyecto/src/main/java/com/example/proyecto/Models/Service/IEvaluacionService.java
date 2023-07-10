package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Jurado;


public interface IEvaluacionService {

     public List<Evaluacion> findAll();
    
    public void save(Evaluacion evaluacion);

	public Evaluacion findOne(Long id);

	public void delete(Long id);

    public  Evaluacion  juradoEvaluacion(Long id_jurado);
}
