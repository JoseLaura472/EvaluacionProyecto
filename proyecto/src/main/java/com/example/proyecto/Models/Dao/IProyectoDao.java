package com.example.proyecto.Models.Dao;

import org.springframework.data.repository.CrudRepository;

import com.example.proyecto.Models.Entity.Proyecto;



public interface IProyectoDao extends CrudRepository<Proyecto, Long>{
    
}
