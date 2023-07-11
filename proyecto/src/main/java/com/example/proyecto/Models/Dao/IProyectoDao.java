package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Proyecto;



public interface IProyectoDao extends CrudRepository<Proyecto, Long>{
    
    @Query("SELECT p FROM Proyecto p JOIN p.jurado j WHERE j.id = :juradoId")
    List<Proyecto> findByJuradoId(@Param("juradoId") Long juradoId);
}
