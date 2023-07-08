package com.example.proyecto.Models.Dao;

import org.springframework.data.repository.CrudRepository;


import com.example.proyecto.Models.Entity.Evaluacion;

public interface IEvaluacionDao extends CrudRepository<Evaluacion, Long>{
    
}
