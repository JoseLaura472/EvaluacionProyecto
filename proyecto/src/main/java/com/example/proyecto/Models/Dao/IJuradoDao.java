package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Jurado;
public interface IJuradoDao  extends CrudRepository<Jurado, Long>{

    @Query(value = "Select * From jurado where id_persona = ?1", nativeQuery = true)
    public Jurado juradoPorIdPersona(Long id_persona);

    @Query("SELECT j FROM Jurado j JOIN j.proyecto p WHERE p.id = :proyectoId and j.estado != 'X' ")
    List<Jurado> findByProyectoId(@Param("proyectoId") Long proyectoId);

    @Query("SELECT j FROM Jurado j " +
       "WHERE CONCAT(j.persona.nombres, ' ', j.persona.paterno, ' ', j.persona.materno) = :nombreCompleto")
    Jurado findByNombreCompleto(@Param("nombreCompleto") String nombreCompleto);
}