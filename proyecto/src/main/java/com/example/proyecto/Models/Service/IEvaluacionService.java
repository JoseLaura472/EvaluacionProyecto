package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Evaluacion;


public interface IEvaluacionService {

     public List<Evaluacion> findAll();
    
    public void save(Evaluacion evaluacion);

	public Evaluacion findOne(Long id);

	public void delete(Long id);
}
