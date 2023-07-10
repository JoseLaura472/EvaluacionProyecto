package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Jurado;

public interface IEvaluacionDao extends CrudRepository<Evaluacion, Long>{
    

    @Query("select e from Evaluacion e INNER join e.jurado j INNER join e.proyectos p WHERE j.id_jurado = ?1")
    public  Evaluacion juradoEvaluacion(Long id_jurado);
}
