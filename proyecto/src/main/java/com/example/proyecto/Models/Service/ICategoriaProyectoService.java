package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.CategoriaProyecto;

public interface ICategoriaProyectoService {
    
     public List<CategoriaProyecto> findAll();
    
    public void save(CategoriaProyecto categoriaProyecto);

	public CategoriaProyecto findOne(Long id);

	public void delete(Long id);
}
