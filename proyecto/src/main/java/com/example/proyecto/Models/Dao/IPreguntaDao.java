package com.example.proyecto.Models.Dao;

import org.springframework.data.repository.CrudRepository;

import com.example.proyecto.Models.Entity.Pregunta;

public interface IPreguntaDao extends CrudRepository<Pregunta, Long>{
    
}
