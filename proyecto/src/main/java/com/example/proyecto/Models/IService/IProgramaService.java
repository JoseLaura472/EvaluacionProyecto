package com.example.proyecto.Models.IService;

import java.util.List;

import com.example.proyecto.Models.Entity.Programa;


public interface IProgramaService {
    
    public List<Programa> findAll();
    
    public void save(Programa programa);

	public Programa findOne(Long id);

	public void delete(Long id);
}
