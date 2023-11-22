package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Proyecto;



public interface IProyectoDao extends CrudRepository<Proyecto, Long>{
    
    @Query("SELECT p FROM Proyecto p JOIN p.jurado j WHERE j.id = :juradoId")
    List<Proyecto> findByJuradoId(@Param("juradoId") Long juradoId);

    @Query(value = "SELECT * FROM proyecto as p ORDER BY p.promedio_final DESC LIMIT 1", nativeQuery = true)
    public List<Proyecto> Primerlugar();

    @Query(value = "SELECT * FROM proyecto as p ORDER BY p.promedio_final DESC LIMIT 1 OFFSET 1", nativeQuery = true)
    public List<Proyecto> Segundolugar();

    @Query(value = "SELECT * FROM ( SELECT *, ROW_NUMBER() OVER (ORDER BY promedio_final DESC) AS row_num FROM proyecto ) AS subquery WHERE row_num = 3", nativeQuery = true)
    public List<Proyecto> Tercerlugar();

    @Query(value = "Select * from proyecto Where estado = 'E'", nativeQuery = true)
    List<Proyecto> proyectosEvaluados();

    @Query(value = "SELECT * FROM proyecto as p ORDER BY p.promedio_final DESC", nativeQuery = true)
    List<Proyecto> proyectosRanking();

    @Query(value = "SELECT * FROM proyecto as p WHERE p.categoria_proyecto='INNOVACIÓN TECNOLÓGICA EN LA AMAZONIA' AND p.estado != 'X' ORDER BY p.promedio_final DESC", nativeQuery = true)
    List<Proyecto> proyectosRankingTecnologia();

    @Query(value = "SELECT * FROM proyecto as p WHERE p.categoria_proyecto='INNOVACIÓN Y EMPRENDIMIENTO' AND p.estado != 'X' ORDER BY p.promedio_final DESC", nativeQuery = true)
    List<Proyecto> proyectosRankingEmprendimiento();

    @Query(value = "SELECT * FROM proyecto as p WHERE p.categoria_proyecto='SALUD PUBLICA, DERECHOS HUMANOS Y JUSTICIA SOCIAL' AND p.estado != 'X' ORDER BY p.promedio_final DESC", nativeQuery = true)
    List<Proyecto> proyectosRankingSalud();
}
