package com.example.proyecto.Models.IServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IRubricaCriterioDao;
import com.example.proyecto.Models.Dao.IRubricaDao;
import com.example.proyecto.Models.Dto.RubricaCriterioDto;
import com.example.proyecto.Models.Dto.RubricaDto;
import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.IService.IRubricaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RubricaServiceimpl implements IRubricaService {
    
    private final IRubricaDao dao;
    private final IRubricaCriterioDao rubricaCriterioDao;

    @Override
    public List<Rubrica> findAll() {
        return dao.findAll();
    }

    @Override
    public Rubrica findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public Rubrica save(Rubrica entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public Optional<Rubrica> buscarPorNombre(String nombre) {
        return dao.buscarPorNombre(nombre);
    }

    @Override
    public List<Rubrica> listarRubrica() {
        return dao.listarRubrica();
    }

    @Override
    public List<Rubrica> findByActividadOrder(Long actId) {
        return dao.findByActividadOrder(actId);
    }

    @Override
    public boolean existsByActividad_IdActividadAndVersionAndEstado(Long idActividad, String version, String estado) {
        return dao.existsByActividad_IdActividadAndVersionAndEstado(idActividad, version, estado);
    }

    @Override
    public boolean existsByCategoriaActividad_IdCategoriaActividadAndVersionAndEstado(Long idCategoria, String version,
            String estado) {
        return dao.existsByCategoriaActividad_IdCategoriaActividadAndVersionAndEstado(idCategoria, version, estado);
    }

    @Override
    public boolean existsByActividad_IdActividadAndVersionAndEstadoAndIdRubricaNot(Long idActividad, String version,
            String estado, Long idRubrica) {
        return dao.existsByActividad_IdActividadAndVersionAndEstadoAndIdRubricaNot(idActividad, version, estado, idRubrica);
    }

    @Override
    public boolean existsByCategoriaActividad_IdCategoriaActividadAndVersionAndEstadoAndIdRubricaNot(Long idCategoria,
            String version, String estado, Long idRubrica) {
        return dao.existsByCategoriaActividad_IdCategoriaActividadAndVersionAndEstadoAndIdRubricaNot(idCategoria, version, estado, idRubrica);
    }

    @Override
    public RubricaDto obtenerRubricaVigentePorCategoria(Long categoriaId) {
       if (categoriaId == null) throw new IllegalArgumentException("categoriaId es requerido");

        // Tomamos la última activa (por id desc) para la categoría
        List<Rubrica> listado = dao.findActivasPorCategoriaOrderDesc(categoriaId);
        if (listado.isEmpty()) {
            throw new IllegalStateException("No existe rúbrica activa para la categoría seleccionada");
        }
        Rubrica r = listado.get(0);

        List<RubricaCriterioDto> criterios = rubricaCriterioDao.listarCriteriosDto(r.getIdRubrica());

        return new RubricaDto(
            r.getIdRubrica(),
            r.getNombre(),
            r.getVersion(),
            criterios
        );
    }

    @Override
    public List<RubricaDto> obtenerRubricasActivasPorCategoria(Long categoriaId) {
        if (categoriaId == null) throw new IllegalArgumentException("categoriaId es requerido");

        // Trae TODAS las activas, ordenadas desc (o asc como prefieras)
        List<Rubrica> rubricas = dao.findActivasPorCategoriaOrderDesc(categoriaId);
        if (rubricas.isEmpty())
            throw new IllegalStateException("No existen rúbricas activas para la categoría seleccionada");

        return rubricas.stream().map(r -> {
            List<RubricaCriterioDto> criterios =
                rubricaCriterioDao.listarCriteriosDto(r.getIdRubrica());
            return new RubricaDto(r.getIdRubrica(), r.getNombre(), r.getVersion(), criterios);
        }).toList();
    }

    @Override
    public long countActivasByCategoria(Long categoriaId) {
        return dao.countActivasByCategoria(categoriaId);
    }
}
