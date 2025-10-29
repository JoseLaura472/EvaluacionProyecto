package com.example.proyecto.Models.IService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Service.IServiceGenerico;

public interface IEvaluacionService extends IServiceGenerico<Evaluacion, Long>{
    boolean existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJurado(Long actId, Long inscId, Long juradoId);
    
    boolean existsByActividad_IdActividadAndInscripcion_IdInscripcionAndJurado_IdJuradoAndRubrica_IdRubrica(
    Long actividadId, Long inscripcionId, Long juradoId, Long rubricaId);

    List<Evaluacion> findByActividad_IdActividad(Long actividadId);

    List<Evaluacion> findFullByActividadAndInscripcion(@Param("actId") Long actId,
                                                        @Param("inscId") Long inscId);

    Optional<Evaluacion> findByJuradoPersonaAndParticipanteAndRubrica(
        @Param("idPersonaJurado") Long idPersonaJurado,
        @Param("idParticipante") Long idParticipante,
        @Param("idRubrica") Long idRubrica
    );

    /* para entrada universitaria */
    boolean existsByInscripcionAndJurado(Long idInscripcion, Long idJurado);
}
