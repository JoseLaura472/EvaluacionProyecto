package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.TipoProyecto;

public interface ITipoProyectoService {
    public List<TipoProyecto> findAll();
    
    public void save(TipoProyecto tipoProyecto);

	public TipoProyecto findOne(Long id);

	public void delete(Long id);
}
