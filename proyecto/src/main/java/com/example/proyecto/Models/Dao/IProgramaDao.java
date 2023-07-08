package com.example.proyecto.Models.Dao;

import org.springframework.data.repository.CrudRepository;


import com.example.proyecto.Models.Entity.Programa;

public interface IProgramaDao extends CrudRepository<Programa, Long>{
    
}
