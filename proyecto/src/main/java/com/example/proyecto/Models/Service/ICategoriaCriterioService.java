package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.CategoriaCriterio;

public interface ICategoriaCriterioService {
    public List<CategoriaCriterio> findAll();
    
    public void save(CategoriaCriterio categoriaCriterio);

	public CategoriaCriterio findOne(Long id);

	public void delete(Long id);
}
