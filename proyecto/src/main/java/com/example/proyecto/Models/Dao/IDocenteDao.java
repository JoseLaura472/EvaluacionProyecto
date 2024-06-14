package com.example.proyecto.Models.Dao;

import java.util.List;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Docente;


public interface IDocenteDao extends CrudRepository<Docente, Long>{
    

    @Query("SELECT d FROM Docente d JOIN FETCH d.persona p WHERE d.estado = :estado")
    public List<Docente> listaDocentes(@Param("estado") String estado);
}
