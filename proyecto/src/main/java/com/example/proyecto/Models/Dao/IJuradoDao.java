package com.example.proyecto.Models.Dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.proyecto.Models.Entity.Jurado;


public interface IJuradoDao  extends CrudRepository<Jurado, Long>{

    @Query(value = "Select * From jurado where id_persona = ?1", nativeQuery = true)
    public Jurado juradoPorIdPersona(Long id_persona);

}
