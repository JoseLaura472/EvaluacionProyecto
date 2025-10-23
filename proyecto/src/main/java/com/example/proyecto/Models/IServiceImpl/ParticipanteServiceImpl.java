package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IEvaluacionDao;
import com.example.proyecto.Models.Dao.IInscripcionDao;
import com.example.proyecto.Models.Dao.IParticipanteCategoriaDao;
import com.example.proyecto.Models.Dao.IParticipanteDao;
import com.example.proyecto.Models.Dto.ParticipanteListadoDto;
import com.example.proyecto.Models.Entity.Participante;
import com.example.proyecto.Models.IService.IParticipanteService;
import com.example.proyecto.Models.IService.IRubricaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipanteServiceImpl implements IParticipanteService {

    private final IParticipanteDao dao;
    private final IParticipanteCategoriaDao participanteCategoriaRepository;
    private final IRubricaService rubricaService;
    private final IInscripcionDao inscripcionService;
    private final IEvaluacionDao evaluacionService;

    @Override
    public List<Participante> findAll() {
        return dao.findAll();
    }

    @Override
    public Participante findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public Participante save(Participante entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public Optional<Participante> buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    @Override
    public List<Participante> listarParticipantes() {
        return dao.listarParticipantes();
    }

    @Override
    public List<ParticipanteListadoDto> listarPorCategoria(Long categoriaId) {
        if (categoriaId == null) return List.of();
        return participanteCategoriaRepository.listarParticipantesPorCategoria(categoriaId, false);
    }

    @Override
    public List<ParticipanteListadoDto> listarPendientesPorCategoria(Long categoriaId, Long juradoId) {
        if (categoriaId == null || juradoId == null) return List.of();

        final long totalRubricas = rubricaService.countActivasByCategoria(categoriaId);
        if (totalRubricas == 0) return List.of();

        // Trae todos los participantes de la categoría
        List<ParticipanteListadoDto> todos = participanteCategoriaRepository
                .listarParticipantesPorCategoria(categoriaId, true);

                System.out.println("[PENDIENTES] cat=" + categoriaId + " jurado=" + juradoId 
    + " totalRubricas=" + totalRubricas);


        // Filtra dejando solo los que NO completaron todas las rúbricas con este jurado
        return todos.stream().filter(dto -> {
            var inscOpt = inscripcionService.findByCategoriaAndParticipante(categoriaId, dto.id());
            if (inscOpt.isEmpty()) {
                System.out.println("[PENDIENTE] p=" + dto.id() + " sin inscripcion -> incluir=true");
                return true; 
            }
            long evaluadas = evaluacionService
                    .countRubricasEvaluadasPorJurado(inscOpt.get().getIdInscripcion(), juradoId);
            boolean incluir = evaluadas < totalRubricas;
            System.out.println("[PENDIENTE] p=" + dto.id() + " eval=" + evaluadas + "/" + totalRubricas + " -> incluir=" + incluir);
            return incluir;
        }).toList();

    }
}
