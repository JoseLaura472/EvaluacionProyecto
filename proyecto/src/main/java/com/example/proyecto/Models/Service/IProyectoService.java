package com.example.proyecto.Models.Service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Proyecto;

public interface IProyectoService {

    public List<Proyecto> findAll();
    
    public void save(Proyecto proyecto);

	public Proyecto findOne(Long id);

	public void delete(Long id);

    List<Proyecto> findByJuradoId(@Param("juradoId") Long juradoId);

    public List<Proyecto> Primerlugar();

    public List<Proyecto> Segundolugar();

    public List<Proyecto> Tercerlugar();
    
    List<Proyecto> proyectosEvaluados();

    List<Proyecto> proyectosRanking();

    List<Proyecto> proyectosRankingTecnologia();

    List<Proyecto> proyectosRankingEmprendimiento();

    List<Proyecto> proyectosRankingSalud();

    public List<Proyecto> obtenerProyectosPorTipoProyecto(Long id_tipo_proyecto);


}
