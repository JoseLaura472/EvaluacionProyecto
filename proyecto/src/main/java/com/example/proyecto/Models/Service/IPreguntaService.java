package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Pregunta;


public interface IPreguntaService {
    
    public List<Pregunta> findAll();
    
    public void save(Pregunta pregunta);

	public Pregunta findOne(Long id);

	public void delete(Long id);
}
