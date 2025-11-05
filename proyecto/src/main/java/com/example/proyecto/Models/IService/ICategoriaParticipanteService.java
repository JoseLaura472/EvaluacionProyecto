package com.example.proyecto.Models.IService;

import com.example.proyecto.Models.Entity.CategoriaParticipante;
import com.example.proyecto.Models.Service.IServiceGenerico;

public interface ICategoriaParticipanteService extends IServiceGenerico<CategoriaParticipante, Long>{
    CategoriaParticipante findByNombre (String nombre);
}
