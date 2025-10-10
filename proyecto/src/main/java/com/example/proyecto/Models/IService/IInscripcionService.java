package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Service.IServiceGenerico;

@Service
public interface IInscripcionService extends IServiceGenerico<Inscripcion, Long> {
    List<Inscripcion> listarInscripciones();
    
    List<Inscripcion> findPendientesByActividadAndCategoriaExcluyendoEvaluadas(
        @Param("actId") Long actividadId,
        @Param("catId") Long categoriaId,
        @Param("idJurado") Long idJurado);

    long countByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(Long actividadId, Long categoriaId);

    List<Inscripcion> findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(Long actividadId, Long categoriaId);

    List<Inscripcion> findByActividad_IdActividad(Long actividadId);

    Optional<Inscripcion> fetchFull(@Param("actId") Long actId,
                                    @Param("inscId") Long inscId);
}
