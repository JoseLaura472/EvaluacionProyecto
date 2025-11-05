package com.example.proyecto.Models.Dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.proyecto.Models.Entity.CategoriaParticipante;

public interface ICategoriaParticipanteDao extends JpaRepository<CategoriaParticipante, Long> {
    CategoriaParticipante findByNombre (String nombre);
}
