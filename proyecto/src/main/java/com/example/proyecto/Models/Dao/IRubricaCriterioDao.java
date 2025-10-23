package com.example.proyecto.Models.Dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Dto.RubricaCriterioDto;
import com.example.proyecto.Models.Entity.RubricaCriterio;

public interface IRubricaCriterioDao extends JpaRepository<RubricaCriterio, Long> {
    
    @Query("SELECT rc FROM RubricaCriterio rc WHERE rc.nombre = ?1 AND rc.estado = 'A'")
    Optional<RubricaCriterio> buscarPorNombre(String nombre);

    @Query("SELECT rc FROM RubricaCriterio rc WHERE rc.estado = 'A'")
    List<RubricaCriterio> listarRubricaCriterio();

    @Query("select c from RubricaCriterio c where c.rubrica.idRubrica = :rubId order by c.idRubricaCriterio asc")
    List<RubricaCriterio> findByRubrica(@Param("rubId") Long rubricaId);

    @Query("""
        select new com.example.proyecto.Models.Dto.RubricaCriterioDto(rc.idRubricaCriterio, rc.nombre, rc.porcentaje, rc.maxPuntos, rc.descripcion)
        from RubricaCriterio rc
        where rc.estado='A' and rc.rubrica.idRubrica = :rubricaId
        order by rc.idRubricaCriterio asc
    """)
    List<RubricaCriterioDto> listarCriteriosDto(@Param("rubricaId") Long rubricaId);
}
