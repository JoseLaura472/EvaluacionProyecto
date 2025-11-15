package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Evaluacion;

public interface IEvaluacionDao extends JpaRepository<Evaluacion, Long>{
    
    boolean existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJurado(Long actId, Long inscId, Long juradoId);

    boolean existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJuradoAndRubrica_IdRubrica(
    Long actividadId, Long inscripcionId, Long juradoId, Long rubricaId);


    List<Evaluacion> findByActividad_IdActividad(Long actividadId);

    @Query("""
        select distinct e
        from Evaluacion e
        join fetch e.jurado j
        left join fetch j.persona pj
        left join fetch e.detalles d
        left join fetch d.rubricaCriterio rc
        where e.actividad.idActividad = :actId
        and e.inscripcion.idInscripcion = :inscId
        order by j.idJurado asc, rc.idRubricaCriterio asc
    """)
    List<Evaluacion> findFullByActividadAndInscripcion(@Param("actId") Long actId,
                                                        @Param("inscId") Long inscId);

    @Query("""
        select e
        from Evaluacion e
          join e.jurado j
          join j.persona p
        where e.estado<>'X'
          and p.idPersona = :idPersonaJurado
          and e.participante.idParticipante = :idParticipante
          and e.rubrica.idRubrica = :idRubrica
    """)
    Optional<Evaluacion> findByJuradoPersonaAndParticipanteAndRubrica(
        @Param("idPersonaJurado") Long idPersonaJurado,
        @Param("idParticipante") Long idParticipante,
        @Param("idRubrica") Long idRubrica
    );

    @Query("""
        select count(distinct e.rubrica.idRubrica)
        from Evaluacion e
        where e.inscripcion.idInscripcion = :inscId
          and e.jurado.idJurado = :juradoId
    """)
    long countRubricasEvaluadasPorJurado(@Param("inscId") Long inscId,
                                         @Param("juradoId") Long juradoId);

  boolean existsByInscripcionIdInscripcionAndJuradoIdJurado(Long idInscripcion, Long idJurado);
  boolean existsByJuradoIdJuradoAndParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(
        Long idJurado, 
        Long idParticipante, 
        Long idCategoria
    );

    Evaluacion findByJuradoIdJuradoAndParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(Long idJurado, Long idParticipante, Long idCategoria);
    List<Evaluacion> findByParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(Long idParticipante, Long idCategoria);
    int countByParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(Long idParticipante, Long idCategoria);

    /* FEXCOIN */
    /**
     * ✅ Verifica si ya existe evaluación (usa índice compuesto)
     */
    boolean existsByJurado_IdJuradoAndInscripcion_IdInscripcionAndRubrica_IdRubrica(
        Long juradoId, Long inscripcionId, Long rubricaId
    );
    
    /**
     * ✅ Busca evaluación existente para actualizar
     */
    Optional<Evaluacion> findByJurado_IdJuradoAndInscripcion_IdInscripcionAndRubrica_IdRubrica(
        Long juradoId, Long inscripcionId, Long rubricaId
    );
    
    /**
     * ✅ Obtiene todas las evaluaciones de un jurado en una categoría
     */
    @Query("SELECT e FROM Evaluacion e " +
           "JOIN FETCH e.rubrica " +
           "WHERE e.jurado.idJurado = :juradoId " +
           "AND e.categoriaActividad.idCategoriaActividad = :categoriaId " +
           "AND e.estado = 'A'")
    List<Evaluacion> findByJuradoAndCategoria(
        @Param("juradoId") Long juradoId, 
        @Param("categoriaId") Long categoriaId
    );
    
    /**
     * ✅ Obtiene evaluaciones de un participante con detalles
     */
    @Query("SELECT DISTINCT e FROM Evaluacion e " +
           "LEFT JOIN FETCH e.detalles d " +
           "LEFT JOIN FETCH d.rubricaCriterio " +
           "WHERE e.participante.idParticipante = :participanteId " +
           "AND e.categoriaActividad.idCategoriaActividad = :categoriaId " +
           "AND e.estado = 'A'")
    List<Evaluacion> findByParticipanteAndCategoriaWithDetalles(
        @Param("participanteId") Long participanteId,
        @Param("categoriaId") Long categoriaId
    );
    
    /**
     * ✅ Cuenta cuántas rúbricas ha evaluado el jurado para un participante
     */
    @Query("SELECT COUNT(DISTINCT e.rubrica.idRubrica) FROM Evaluacion e " +
           "WHERE e.jurado.idJurado = :juradoId " +
           "AND e.participante.idParticipante = :participanteId " +
           "AND e.categoriaActividad.idCategoriaActividad = :categoriaId " +
           "AND e.estado = 'A'")
    long countRubricasEvaluadas(
        @Param("juradoId") Long juradoId,
        @Param("participanteId") Long participanteId,
        @Param("categoriaId") Long categoriaId
    );

    /**
     * ✅ NUEVO: Obtiene todas las evaluaciones de una categoría con datos del jurado
     */
    @Query("SELECT e FROM Evaluacion e " +
           "JOIN FETCH e.jurado j " +
           "JOIN FETCH j.persona p " +
           "JOIN FETCH e.participante part " +
           "LEFT JOIN FETCH e.rubrica r " +
           "WHERE e.categoriaActividad.idCategoriaActividad = :categoriaId " +
           "AND e.estado = 'A' " +
           "ORDER BY part.nombre, j.idJurado, r.idRubrica")
    List<Evaluacion> findAllByCategoriaWithJuradoAndParticipante(
        @Param("categoriaId") Long categoriaId
    );
    
    /**
     * ✅ NUEVO: Suma el total acumulado por jurado y participante
     */
    @Query("SELECT " +
           "  e.participante.idParticipante as participanteId, " +
           "  e.jurado.idJurado as juradoId, " +
           "  SUM(e.totalPonderacion) as totalAcumulado, " +
           "  COUNT(DISTINCT e.rubrica.idRubrica) as rubricasEvaluadas " +
           "FROM Evaluacion e " +
           "WHERE e.categoriaActividad.idCategoriaActividad = :categoriaId " +
           "AND e.estado = 'A' " +
           "GROUP BY e.participante.idParticipante, e.jurado.idJurado")
    List<Object[]> findTotalesAcumuladosPorJuradoYParticipante(
        @Param("categoriaId") Long categoriaId
    );

    @Query("SELECT e FROM Evaluacion e " +
        "WHERE e.jurado.idJurado = :juradoId " +
        "AND e.inscripcion.participante.idParticipante = :participanteId " +
        "AND e.inscripcion.categoriaActividad.idCategoriaActividad = :categoriaId")
    List<Evaluacion> findByJuradoAndParticipanteAndCategoria(
        @Param("juradoId") Long juradoId,
        @Param("participanteId") Long participanteId,
        @Param("categoriaId") Long categoriaId
    );

}
