package com.example.proyecto.Models.Service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Jurado;


public interface IJuradoService {
    
    public List<Jurado> findAll();
    
    public void save(Jurado jurado);

	public Jurado findOne(Long id);

	public void delete(Long id);

    public Jurado juradoPorIdPersona(Long id_persona);
    List<Jurado> findByProyectoId(@Param("proyectoId") Long proyectoId);

    Jurado findByNombreCompleto(@Param("nombreCompleto") String nombreCompleto);
}