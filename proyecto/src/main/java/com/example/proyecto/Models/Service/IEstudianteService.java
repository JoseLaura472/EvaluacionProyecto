package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Estudiante;


public interface IEstudianteService {

    public List<Estudiante> findAll();
    
    public void save(Estudiante estudiante);

	public Estudiante findOne(Long id);

	public void delete(Long id);
}
