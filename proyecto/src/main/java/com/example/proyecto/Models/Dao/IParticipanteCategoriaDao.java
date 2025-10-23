package com.example.proyecto.Models.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Dto.ParticipanteListadoDto;
import com.example.proyecto.Models.Entity.ParticipanteCategoria;

public interface IParticipanteCategoriaDao extends JpaRepository<ParticipanteCategoria, Long>{
    
    @Query("""
        select distinct new com.example.proyecto.Models.Dto.ParticipanteListadoDto(
            p.idParticipante, p.nombre, p.institucion
        )
        from ParticipanteCategoria pc
            join pc.participante p
            join pc.categoriaActividad c
        where c.idCategoriaActividad = :categoriaId
          and (:soloActivos = false or coalesce(pc.estado,'A') = 'A')
          and (:soloActivos = false or coalesce(p.estado,'A') = 'A')
          and (:soloActivos = false or coalesce(c.estado,'A') = 'A')
        order by p.nombre asc
    """)
    List<ParticipanteListadoDto> listarParticipantesPorCategoria(
        @Param("categoriaId") Long categoriaId,
        @Param("soloActivos") boolean soloActivos
    );
}
