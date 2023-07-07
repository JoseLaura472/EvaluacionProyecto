package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Jurado;


public interface IJuradoService {
    
    public List<Jurado> findAll();
    
    public void save(Jurado jurado);

	public Jurado findOne(Long id);

	public void delete(Long id);
}
