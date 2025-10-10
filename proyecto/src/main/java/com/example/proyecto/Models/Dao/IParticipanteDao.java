package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.proyecto.Models.Entity.Participante;

public interface IParticipanteDao extends JpaRepository<Participante, Long>{
    
    @Query("SELECT p FROM Participante p WHERE p.nombre = ?1 AND p.estado = 'A'")
    Optional<Participante> buscarPorNombre(String nombre);

    @Query("SELECT p FROM Participante p WHERE p.estado = 'A'")
    List<Participante> listarParticipantes();
}
