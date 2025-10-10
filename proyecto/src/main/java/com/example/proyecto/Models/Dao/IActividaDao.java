package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.proyecto.Models.Entity.Actividad;

public interface IActividaDao extends JpaRepository<Actividad, Long> {
    @Query("SELECT a FROM Actividad a WHERE a.nombre = ?1 AND a.estado = 'A'")
    Optional<Actividad> buscarPorNombre(String nombre);

    @Query("SELECT a FROM Actividad a WHERE a.estado = 'A'")
    List<Actividad> listarActividades();

    @Query("select a from Actividad a order by a.fecha desc")
    List<Actividad> findAllOrderByFechaDesc();
}
