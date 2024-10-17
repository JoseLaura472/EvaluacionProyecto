package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Proyecto;

public interface IProyectoDao extends CrudRepository<Proyecto, Long> {

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

        @Query(value = """
                SELECT pr.*, tp.nom_tipo_proyecto 
                FROM proyecto pr 
                JOIN tipo_proyecto tp ON pr.id_tipo_proyecto = tp.id_tipo_proyecto 
                WHERE pr.estado != 'X' AND tp.id_tipo_proyecto = ?1 
                ORDER BY pr.promedio_final DESC;
                """, nativeQuery = true)
        List<Proyecto> RankingPorTipoProyecto(Long idTipoProyecto);

        @Query(value = "SELECT * FROM proyecto as p WHERE p.categoria_proyecto='INNOVACIÓN TECNOLÓGICA EN LA AMAZONIA' AND p.estado != 'X' ORDER BY p.promedio_final DESC", nativeQuery = true)
        List<Proyecto> proyectosRankingTecnologia();

        @Query(value = "SELECT * FROM proyecto as p WHERE p.categoria_proyecto='INNOVACIÓN Y EMPRENDIMIENTO' AND p.estado != 'X' ORDER BY p.promedio_final DESC", nativeQuery = true)
        List<Proyecto> proyectosRankingEmprendimiento();

        @Query(value = "SELECT * FROM proyecto as p WHERE p.categoria_proyecto='SALUD PUBLICA, DERECHOS HUMANOS Y JUSTICIA SOCIAL' AND p.estado != 'X' ORDER BY p.promedio_final DESC", nativeQuery = true)
        List<Proyecto> proyectosRankingSalud();

        @Query(value = "SELECT p.* FROM proyecto p \n" + //
                        "LEFT JOIN tipo_proyecto tp ON tp.id_tipo_proyecto = p.id_tipo_proyecto \n" + //
                        "WHERE tp.id_tipo_proyecto = ?1", nativeQuery = true)
        public List<Proyecto> obtenerProyectosPorTipoProyecto(Long id_tipo_proyecto);

        @Query(value = """
                                SELECT p.* FROM proyecto p LEFT JOIN tipo_proyecto tp ON tp.id_tipo_proyecto = p.id_tipo_proyecto
                        LEFT JOIN categoria_proyecto cp ON cp.id_tipo_proyecto = tp.id_tipo_proyecto WHERE p.estado = 'E'
                        AND p.id_categoria_proyecto = ?1 GROUP BY p.id_proyecto ORDER BY p.promedio_final DESC;
                            """, nativeQuery = true)
        public List<Proyecto> obternerProyectosPorCategoriaProyecto(Long id_categoria_proyecto);

        @Query(value = "SELECT p.* \n" + //
                        "FROM proyecto p \n" + //
                        "LEFT JOIN tipo_proyecto tp ON tp.id_tipo_proyecto = p.id_tipo_proyecto \n" + //
                        "LEFT JOIN categoria_proyecto cp ON cp.id_categoria_proyecto = p.id_categoria_proyecto \n" + //
                        "WHERE p.estado = 'E' AND p.id_categoria_proyecto = ?1 \n" + //
                        "GROUP BY p.id_proyecto \n" + //
                        "ORDER BY p.promedio_final DESC \n" + //
                        "LIMIT 3", nativeQuery = true)
        public List<Proyecto> obtenerRankingDeProyectosPorCategoria(Long id_categoria_proyecto);
}
