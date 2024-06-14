package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.proyecto.Models.Entity.Ponderacion;

public interface IPonderacionDao extends CrudRepository<Ponderacion, Long>{
    
    @Query(value = "SELECT p2.* FROM categoria_criterio cc \n" + //
                "LEFT JOIN pregunta p ON p.id_categoria_criterio = cc.id_categoria_criterio \n" + //
                "LEFT JOIN ponderacion p2 ON p2.id_pregunta = p.id_pregunta \n" + //
                "LEFT JOIN evaluacion_ponderacion ep ON ep.id_ponderacion = p2.id_ponderacion \n" + //
                "LEFT JOIN evaluacion e ON e.id_evaluacion = ep.id_evaluacion \n" + //
                "LEFT JOIN evaluacion_proyecto ep2 ON ep2.id_evaluacion = ep.id_evaluacion \n" + //
                "LEFT JOIN proyecto p3 ON p3.id_proyecto = ep2.id_proyecto \n" + //
                "WHERE p3.id_proyecto = ?1",nativeQuery = true)
    public List<Ponderacion> obtenerPonderacionesPorProyecto(Long id_proyecto);
}
