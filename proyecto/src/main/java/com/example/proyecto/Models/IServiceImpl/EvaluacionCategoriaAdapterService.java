package com.example.proyecto.Models.IServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IInscripcionDao;
import com.example.proyecto.Models.Dto.Detalle;
import com.example.proyecto.Models.Dto.EvaluacionDetalleCategoriaDto;
import com.example.proyecto.Models.Dto.EvaluacionGuardarCategoriaDto;
import com.example.proyecto.Models.Dto.EvaluacionGuardarDto;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.Entity.RubricaCriterio;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IRubricaCriterioServcie;
import com.example.proyecto.Models.IService.IRubricaService;
import com.example.proyecto.Models.Service.EvaluacionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluacionCategoriaAdapterService {
    private final IJuradoAsignacionService juradoAsignacionService;
    private final IInscripcionDao inscripcionRepository;
    private final IRubricaService rubricaService;
    private final IRubricaCriterioServcie criterioService;
    private final EvaluacionService evaluacionLegacy;

    /**
     * MÉTODO PRINCIPAL: Convierte evaluación por categoría al formato legacy
     * 
     * FLUJO:
     * 1. Validar que el jurado tenga la categoría asignada
     * 2. Verificar que la rúbrica pertenezca a la categoría
     * 3. Convertir puntajes de escala (0-maxPuntos) a porcentaje (0-100)
     * 4. Delegar al servicio legacy uwur
     */ 

    @Transactional
    public void guardarEvaluacionCategoria(Jurado jurado, EvaluacionGuardarCategoriaDto dtoCat) {

        // ========== VALIDACIONES BÁSICAS ==========
        if (jurado == null || jurado.getIdJurado() == null) {
            throw new IllegalArgumentException("Jurado inválido");
        }
        
        if (dtoCat == null) {
            throw new IllegalArgumentException("Datos de evaluación requeridos");
        }
        
        if (dtoCat.categoriaId() == null || dtoCat.participanteId() == null || dtoCat.rubricaId() == null) {
            throw new IllegalArgumentException("categoriaId, participanteId y rubricaId son obligatorios");
        }
        
        if (dtoCat.detalles() == null || dtoCat.detalles().isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar al menos un detalle de puntaje");
        }

        // ========== VERIFICAR ASIGNACIÓN DEL JURADO ==========
        boolean tieneCat = juradoAsignacionService.existsCategoriaAsignadaByPersona(jurado.getPersona().getIdPersona());
        if (!tieneCat) throw new IllegalStateException("El jurado no tiene categoría asignada");

        // 3) Cargar rúbrica y validar que pertenece a la categoría
        Rubrica rub = rubricaService.findById(dtoCat.rubricaId());
        if (rub.getCategoriaActividad() == null ||
            !rub.getCategoriaActividad().getIdCategoriaActividad().equals(dtoCat.categoriaId()))
            throw new IllegalStateException("La rúbrica no pertenece a la categoría seleccionada");
        if (!"A".equalsIgnoreCase(rub.getEstado()))
            throw new IllegalStateException("La rúbrica no está activa");

        // 4) Ubicar la Inscripción por (categoriaId, participanteId)
        Inscripcion insc = inscripcionRepository
                .findByCategoriaAndParticipante(dtoCat.categoriaId(), dtoCat.participanteId())
                .orElseThrow(() -> new IllegalStateException("No existe inscripción para la categoría y participante"));

        // 5) Cargar criterios de la rúbrica
        List<RubricaCriterio> criterios = criterioService.findByRubrica(rub.getIdRubrica());
        if (criterios.isEmpty())
            throw new IllegalStateException("La rúbrica no tiene criterios");

        // 6) Construir DTO legacy (Actividad + Inscripción + puntajes 0..100)
        EvaluacionGuardarDto legacy = new EvaluacionGuardarDto();
        legacy.setActividadId(insc.getActividad().getIdActividad());
        legacy.setInscripcionId(insc.getIdInscripcion());
        legacy.setRubricaId(rub.getIdRubrica());

        // Mapa de criterios para conversión de escala
        Map<Long, RubricaCriterio> map = criterios.stream()
                .collect(Collectors.toMap(RubricaCriterio::getIdRubricaCriterio, c -> c));

        List<Detalle> detallesLegacy = new ArrayList<>();

        for (EvaluacionDetalleCategoriaDto d : dtoCat.detalles()) {
            RubricaCriterio crit = map.get(d.criterioId());
            if (crit == null) throw new IllegalArgumentException("Criterio inválido: " + d.criterioId());

            double puntaje100;

            if (crit.getMaxPuntos() != null) {
                // ✅ CASO 1: Criterio con escala de puntos (ej: 0-10, 0-5)
                int max = crit.getMaxPuntos();
                int valorRecibido = Optional.ofNullable(d.puntaje()).orElse(0);
                
                if (valorRecibido < 0 || valorRecibido > max) {
                    throw new IllegalArgumentException(
                        String.format("Puntaje fuera de rango para '%s' (0-%d): %d", 
                                      crit.getNombre(), max, valorRecibido)
                    );
                }
                
                // Normalizar a escala 0-100
                puntaje100 = (max == 0) ? 0.0 : (valorRecibido * 100.0 / max);
                
            } else {
                // ✅ CASO 2: Criterio con porcentaje directo (0-100)
                int valorRecibido = Optional.ofNullable(d.puntaje()).orElse(0);
                
                if (valorRecibido < 0 || valorRecibido > 100) {
                    throw new IllegalArgumentException(
                        String.format("Puntaje fuera de rango (0-100) para '%s': %d", 
                                      crit.getNombre(), valorRecibido)
                    );
                }
                
                puntaje100 = valorRecibido;
            }

            // Armar detalle legacy
            Detalle det = new Detalle();
            det.setCriterioId(d.criterioId());
            det.setPuntaje(puntaje100); // tu legacy espera Double 0..100
            detallesLegacy.add(det);
        }

        legacy.setDetalles(detallesLegacy);

        evaluacionLegacy.guardarEvaluacion(jurado, legacy);
    }
}
