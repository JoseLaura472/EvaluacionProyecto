package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Ponderacion;

public interface IPonderacionService {
    public List<Ponderacion> findAll();
    
    public void save(Ponderacion ponderacion);

	public Ponderacion findOne(Long id);

	public void delete(Long id);
}