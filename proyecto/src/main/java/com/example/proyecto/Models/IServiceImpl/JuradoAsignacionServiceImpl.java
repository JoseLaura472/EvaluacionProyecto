package com.example.proyecto.Models.IServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IJuradoAsignacionDao;
import com.example.proyecto.Models.Dto.CategoriaDto;
import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.JuradoAsignacion;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JuradoAsignacionServiceImpl implements IJuradoAsignacionService {
    
    private final IJuradoAsignacionDao dao;

    @Override
    public List<JuradoAsignacion> findAll() {
        return dao.findAll();
    }

    @Override
    public JuradoAsignacion findById(Long idEntidad) {
        return dao.findById(idEntidad).orElse(null);
    }

    @Override
    public JuradoAsignacion save(JuradoAsignacion entidad) {
        return dao.save(entidad);
    }

    @Override
    public void deleteById(Long idEntidad) {
        dao.deleteById(idEntidad);
    }

    @Override
    public List<JuradoAsignacion> listarJuradoAsignacion() {
        return dao.listarJuradoAsignacion();
    }

    @Override
    public List<Actividad> findActividadesAsignadas(Long idJurado) {
        return dao.findActividadesAsignadas(idJurado);
    }

    @Override
    public List<Jurado> findJuradosByActividadOrdenAsignacion(Long actId) {
        return dao.findJuradosByActividadOrdenAsignacion(actId);
    }

    @Override
    public boolean existsCategoriaAsignadaByPersona(Long idPersona) {
        return dao.existsCategoriaAsignadaByPersona(idPersona);
    }

    @Override
    public List<CategoriaDto> listarCategoriasDeJuradoPorPersona(Long idPersona) {
        return dao.listarCategoriasDeJuradoPorPersona(idPersona);
    }

    @Override
    public List<Jurado> findJuradosByActividadAndCategoriaOrdenAsignacion(Long actividadId, Long categoriaId) {
        return dao.findJuradosByActividadAndCategoriaOrdenAsignacion(actividadId, categoriaId);
    }

}
