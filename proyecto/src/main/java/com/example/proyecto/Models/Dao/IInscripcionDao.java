package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.CategoriaActividad;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Entity.Participante;

public interface IInscripcionDao extends JpaRepository<Inscripcion, Long> {
    @Query("SELECT p FROM Inscripcion p WHERE p.estado = 'A'")
    List<Inscripcion> listarInscripciones();

    @Query("""
                select i from Inscripcion i
                join i.participante p
                join i.categoriaActividad ca
                where i.actividad.idActividad = :actId
                and (:catId is null or ca.idCategoriaActividad = :catId)
                and not exists (
                    select 1 from Evaluacion e
                    where e.actividad.idActividad = i.actividad.idActividad
                        and e.inscripcion.idInscripcion = i.idInscripcion
                        and e.jurado.idJurado = :idJurado
                )
                order by ca.nombre asc, p.nombre asc
            """)
    List<Inscripcion> findPendientesByActividadAndCategoriaExcluyendoEvaluadas(
            @Param("actId") Long actividadId,
            @Param("catId") Long categoriaId,
            @Param("idJurado") Long idJurado);

    long countByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(Long actividadId, Long categoriaId);

    // opcional: por actividad y categoría (si quieres filtrar desde DB)
    List<Inscripcion> findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividadOrderByParticipante_PosicionAsc(
            Long actividadId,
            Long categoriaId);

    List<Inscripcion> findByActividad_IdActividad(Long actividadId);

    @Query("""
                select i
                from Inscripcion i
                join fetch i.actividad a
                join fetch i.categoriaActividad c
                join fetch i.participante p
                where i.idInscripcion = :inscId and a.idActividad = :actId
            """)
    Optional<Inscripcion> fetchFull(@Param("actId") Long actId,
            @Param("inscId") Long inscId);

    @Query("""
                select i
                from Inscripcion i
                join i.actividad a
                join i.categoriaActividad c
                join i.participante p
                where coalesce(i.estado,'A') = 'A'
                and c.idCategoriaActividad = :categoriaId
                and p.idParticipante = :participanteId
            """)
    Optional<Inscripcion> findByCategoriaAndParticipante(@Param("categoriaId") Long categoriaId,
            @Param("participanteId") Long participanteId);

    /* para entrada univeristaria */
    // Buscar por participante (id) y fase (campo en CategoriaActividad)
    Optional<Inscripcion> findByParticipanteIdParticipanteAndCategoriaActividadFase(Long idParticipante, String fase);

    // Buscar todas las inscripciones por id de categoría
    List<Inscripcion> findByCategoriaActividadIdCategoriaActividad(Long idCategoriaActividad);

    @Query("SELECT i FROM Inscripcion i " +
            "LEFT JOIN FETCH i.participante p " +
            "LEFT JOIN FETCH i.categoriaActividad c " +
            "WHERE i.actividad.idActividad = :idActividad " +
            "AND i.estado = 'A' " +
            "ORDER BY c.idCategoriaActividad, p.nombre")
    List<Inscripcion> findByActividad(@Param("idActividad") Long idActividad);

    Inscripcion findByActividadIdActividadAndParticipanteIdParticipante(Long idActividad, Long idParticipante);

    Inscripcion findByActividadIdActividadAndParticipanteIdParticipanteAndCategoriaActividadIdCategoriaActividad(
            Long idActividad,
            Long idParticipante,
            Long idCategoriaActividad);

    /* FEXCOIN */
    Optional<Inscripcion> findByParticipanteAndCategoriaActividad(
        Participante participante, 
        CategoriaActividad categoria
    );
}
