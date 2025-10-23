package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dto.RubricaCriterioDto;
import com.example.proyecto.Models.Entity.RubricaCriterio;
import com.example.proyecto.Models.Service.IServiceGenerico;

@Service
public interface IRubricaCriterioServcie extends IServiceGenerico<RubricaCriterio, Long>{
    
    Optional<RubricaCriterio> buscarPorNombre(String nombre);
    List<RubricaCriterio> listarRubricaCriterio();
    List<RubricaCriterio> findByRubrica(@Param("rubId") Long rubricaId);
    List<RubricaCriterioDto> listarCriteriosDto(@Param("rubricaId") Long rubricaId);
}
