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
}
