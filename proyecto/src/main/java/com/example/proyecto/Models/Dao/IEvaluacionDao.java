package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Evaluacion;

public interface IEvaluacionDao extends CrudRepository<Evaluacion, Long>{
    

    @Query("select e from Evaluacion e INNER join e.jurado j INNER join e.proyectos p WHERE j.id_jurado = ?1")
    public  List<Evaluacion> juradoEvaluacion(Long id_jurado);

    @Query("SELECT e FROM Evaluacion e JOIN e.proyectos p WHERE p.id = :proyectoId")
    List<Evaluacion> findByProyectoId(@Param("proyectoId") Long proyectoId);

    // @Query(value = "SELECT e FROM evaluacion_proyecto ep \n" + //
    //             "LEFT JOIN evaluacion e  ON e.id_evaluacion = ep.id_evaluacion \n" + //
    //             "LEFT JOIN  proyecto p ON p.id_proyecto = ep.id_proyecto \n" + //
    //             "LEFT JOIN  jurado j ON j.id_jurado  = e.id_jurado \n" + //
    //             "WHERE p.id_proyecto = ?1 AND j.id_jurado = ?2",nativeQuery = true)
    // List<Evaluacion> validacionEvaluacionJurado(Long id_proyecto , Long id_jurado);

    @Query(value = "SELECT e.* FROM evaluacion_proyecto ep " +
               "LEFT JOIN evaluacion e ON e.id_evaluacion = ep.id_evaluacion " +
               "LEFT JOIN proyecto p ON p.id_proyecto = ep.id_proyecto " +
               "LEFT JOIN jurado j ON j.id_jurado = e.id_jurado " +
               "WHERE p.id_proyecto = ?1 AND j.id_jurado = ?2", nativeQuery = true)
    List<Evaluacion> validacionEvaluacionJurado(Long id_proyecto, Long id_jurado);

    @Query(value = "SELECT e.* FROM evaluacion e \n" + //
                "LEFT JOIN evaluacion_proyecto ep ON ep.id_evaluacion = e.id_evaluacion \n" + //
                "LEFT JOIN proyecto p ON p.id_proyecto = ep.id_proyecto \n" + //
                "WHERE p.id_proyecto =?1",nativeQuery = true)
    public List<Evaluacion> obtenerNotasFinales(Long id_proyecto);
}
