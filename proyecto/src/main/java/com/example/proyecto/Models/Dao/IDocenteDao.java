package com.example.proyecto.Models.Dao;

import org.springframework.data.repository.CrudRepository;

import com.example.proyecto.Models.Entity.Docente;


public interface IDocenteDao extends CrudRepository<Docente, Long>{
    
}
