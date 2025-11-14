package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Persona;

public interface IPersonaDao extends CrudRepository<Persona, Long>{
    
    @Query("Select p from Persona p where p.ci = ?1")
    public Persona getPersonaCI(String ci);

    @Query("SELECT p FROM Persona p WHERE p.estado = :estado")
    List<Persona> listarPersona(@Param("estado") String estado);

    Persona findByNombres(String nombres);
}