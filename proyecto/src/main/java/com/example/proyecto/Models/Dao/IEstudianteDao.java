package com.example.proyecto.Models.Dao;

import org.springframework.data.repository.CrudRepository;


import com.example.proyecto.Models.Entity.Estudiante;

public interface IEstudianteDao extends CrudRepository<Estudiante, Long>{
    
}
