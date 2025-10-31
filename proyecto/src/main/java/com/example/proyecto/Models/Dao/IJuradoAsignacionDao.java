package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Dto.CategoriaDto;
import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.JuradoAsignacion;

public interface IJuradoAsignacionDao extends JpaRepository<JuradoAsignacion, Long>{
    @Query("SELECT p FROM JuradoAsignacion p WHERE p.estado = 'A'")
    List<JuradoAsignacion> listarJuradoAsignacion();

    @Query("""
        select distinct a from JuradoAsignacion ja
        join ja.actividad a
        where ja.jurado.idJurado = :idJurado
        order by a.fecha asc, a.nombre asc
    """)
    List<Actividad> findActividadesAsignadas(@Param("idJurado") Long idJurado);

    @Query("""
        select j
        from JuradoAsignacion ja
        join ja.jurado j
        left join j.persona p
        where ja.actividad.idActividad = :actId
        order by ja.idJuradoAsignacion asc
    """)
    List<Jurado> findJuradosByActividadOrdenAsignacion(@Param("actId") Long actId);

    @Query("""
        select (count(ja) > 0)
        from JuradoAsignacion ja
            join ja.jurado j
            join j.persona p
        where p.idPersona = :idPersona
          and ja.estado = 'A'
          and ja.categoriaActividad is not null
    """)
    boolean existsCategoriaAsignadaByPersona(@Param("idPersona") Long idPersona);

    @Query("""
        select distinct new com.example.proyecto.Models.Dto.CategoriaDto(ca.idCategoriaActividad, ca.nombre)
        from JuradoAsignacion ja
        join ja.jurado j
        join j.persona p
        join ja.categoriaActividad ca
        where p.idPersona = :idPersona
        and ja.estado = 'A'
        and ca.estado = 'A'
        order by ca.nombre asc
    """)
    List<CategoriaDto> listarCategoriasDeJuradoPorPersona(@Param("idPersona") Long idPersona);

    @Query("""
    select ja.jurado
    from JuradoAsignacion ja
        join ja.jurado j
        join ja.categoriaActividad c
        join c.actividad a
    where a.idActividad = :actividadId
        and c.idCategoriaActividad = :categoriaId
        and coalesce(ja.estado,'A') = 'A'
        and coalesce(j.estado,'A') = 'A'
    order by ja.id asc
    """)
    List<Jurado> findJuradosByActividadAndCategoriaOrdenAsignacion(
    @Param("actividadId") Long actividadId,
    @Param("categoriaId") Long categoriaId
    );


    JuradoAsignacion findFirstByJurado_IdJurado(Long idJurado);
    
    List<JuradoAsignacion> findByJurado_IdJurado(Long idJurado);
    
    boolean existsByJurado_Persona_IdPersona(Long idPersona);
    
    // Buscar por actividad y jurado
    List<JuradoAsignacion> findByActividad_IdActividadAndJurado_IdJurado(
        Long idActividad, Long idJurado);

    List<JuradoAsignacion> findByActividadIdActividad(Long idActividad);
    List<JuradoAsignacion> findByActividadIdActividadAndCategoriaActividadIdCategoriaActividad(Long idActividad, Long idCategoria);
    int countByActividadIdActividadAndCategoriaActividadIdCategoriaActividad(Long idActividad, Long idCategoria);
    List<JuradoAsignacion> findByCategoriaActividadIdCategoriaActividad(Long idCategoriaActividad);
}
