package com.example.proyecto.Models.IServiceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IPonderacionDao;
import com.example.proyecto.Models.Dto.ResumenPuntaje;
import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.IService.IPonderacionService;

@Service
public class PonderacionServiceImpl implements IPonderacionService {

    @Autowired
    private IPonderacionDao ponderacionDao;

    @Override
    public List<Ponderacion> findAll() {
        // TODO Auto-generated method stub
        return (List<Ponderacion>) ponderacionDao.findAll();
    }

    @Override
    public void save(Ponderacion ponderacion) {
        // TODO Auto-generated method stub
        ponderacionDao.save(ponderacion);
    }

    @Override
    public Ponderacion findOne(Long id) {
        // TODO Auto-generated method stub
        return ponderacionDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        ponderacionDao.deleteById(id);
    }

    @Override
    public List<Ponderacion> obtenerPonderacionesPorProyecto(Long id_proyecto) {
        // TODO Auto-generated method stub
        return ponderacionDao.obtenerPonderacionesPorProyecto(id_proyecto);
    }

    @Override
    public List<ResumenPuntaje> obtenerResumenPonderacionesPorProyecto(Long id_proyecto) {
        List<Object[]> results = ponderacionDao.obtenerResumenPonderacionesPorProyecto(id_proyecto);
        List<ResumenPuntaje> resumenPuntajes = new ArrayList<>();

        for (Object[] row : results) {
            ResumenPuntaje resumen = new ResumenPuntaje();
            resumen.setIdCategoriaCriterio((Long) row[0]);
            resumen.setIdEvaluacion((Long) row[1]);
            resumen.setPuntaje((Long) row[2]);
            resumenPuntajes.add(resumen);
        }

        return resumenPuntajes;
    }

}
