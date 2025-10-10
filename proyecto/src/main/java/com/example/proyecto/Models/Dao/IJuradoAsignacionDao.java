package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    
}
