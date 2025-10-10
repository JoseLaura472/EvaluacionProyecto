package com.example.proyecto.Models.IService;

import java.util.List;

import com.example.proyecto.Models.Entity.CategoriaCriterio;
import com.example.proyecto.Models.Entity.Puntaje;

public interface ICategoriaCriterioService {
    public List<CategoriaCriterio> findAll();
    
    public void save(CategoriaCriterio categoriaCriterio);

	public CategoriaCriterio findOne(Long id);

	public void delete(Long id);

    public List<CategoriaCriterio> obtenerPonderacionesPorProyecto(Long id_proyecto);

    public List<CategoriaCriterio> obtenerCategoriaCriteriosPorTipoProyecto(Long id_tipo_proyecto);

    public List<Puntaje> obtenerPuntajesPorProyecto(Long id_proyecto);


}
