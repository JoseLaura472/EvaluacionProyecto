package com.example.proyecto.Models.Service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Jurado;


public interface IEvaluacionService {

     public List<Evaluacion> findAll();
    
    public void save(Evaluacion evaluacion);

	public Evaluacion findOne(Long id);

	public void delete(Long id);

    public  List<Evaluacion>  juradoEvaluacion(Long id_jurado);

    List<Evaluacion> findByProyectoId(@Param("proyectoId") Long proyectoId);

    public List<Evaluacion> obtenerNotasFinales(Long id_proyecto);
}
