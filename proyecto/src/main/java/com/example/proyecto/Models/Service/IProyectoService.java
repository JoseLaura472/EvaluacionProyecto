package com.example.proyecto.Models.Service;

import java.util.List;


import com.example.proyecto.Models.Entity.Proyecto;

public interface IProyectoService {

    public List<Proyecto> findAll();
    
    public void save(Proyecto proyecto);

	public Proyecto findOne(Long id);

	public void delete(Long id);
}
