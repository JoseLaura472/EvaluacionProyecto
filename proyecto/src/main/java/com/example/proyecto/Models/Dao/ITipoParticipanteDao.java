package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.proyecto.Models.Entity.TipoParticipante;

public interface ITipoParticipanteDao extends JpaRepository<TipoParticipante, Long> {
    
    @Query("SELECT tip FROM TipoParticipante tip WHERE tip.nombre = ?1 AND tip.estado = 'A'")
    Optional<TipoParticipante> buscarPorNombre(String nombre);

    @Query("SELECT tp FROM TipoParticipante tp WHERE tp.estado = 'A'")
    List<TipoParticipante> listarTipoParticipantes();
}
