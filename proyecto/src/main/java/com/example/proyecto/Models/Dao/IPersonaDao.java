package com.example.proyecto.Models.Dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.proyecto.Models.Entity.Persona;

public interface IPersonaDao extends CrudRepository<Persona, Long>{
    
    @Query("Select p from Persona p where p.ci = ?1")
    public Persona getPersonaCI(String ci);

}
