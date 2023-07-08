package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Criterio;

public interface ICriterioService {
    public List<Criterio> findAll();
    
    public void save(Criterio criterio);

	public Criterio findOne(Long id);

	public void delete(Long id);
}
