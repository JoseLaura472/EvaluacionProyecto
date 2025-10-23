package com.example.proyecto.Models.Service;

import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Dao.IJuradoDao;
import com.example.proyecto.Models.Entity.Jurado;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JuradoLookupService {
    private final IJuradoDao juradoDao;

        public Jurado cargarPorPersonaOrThrow(Long idPersona) {
        if (idPersona == null) throw new IllegalArgumentException("Persona inválida");
        return juradoDao
                .findFirstByPersona_IdPersonaAndEstado(idPersona, "A") // o el método que prefieras
                .orElseThrow(() -> new IllegalStateException("No existe un jurado activo para la persona " + idPersona));
    }
}
