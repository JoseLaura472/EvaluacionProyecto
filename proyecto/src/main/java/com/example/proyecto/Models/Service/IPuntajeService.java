package com.example.proyecto.Models.Service;

import java.util.List;

import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.Entity.Puntaje;

public interface IPuntajeService {
    

     public List<Puntaje> findAll();
    
    public void save(Puntaje puntaje);

	public Puntaje findOne(Long id);

	public void delete(Long id);

    public List<Puntaje> obtenerPuntajesPorProyecto(Long id_proyecto);

    Puntaje puntajePonderacionEvaluacionJurado(Long idJurado, Long idEvaluacion, Long idPonderacion);

    List<Puntaje> puntajesEvaluacionJurado(Long idJurado, Long idEvaluacion);

    Puntaje puntajePonderacionJurado(Long idJurado, Long idPonderacion);

}
