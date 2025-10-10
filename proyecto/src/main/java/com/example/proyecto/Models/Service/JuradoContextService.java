package com.example.proyecto.Models.Service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.IService.IJuradoService;
import com.example.proyecto.Models.IService.IUsuarioService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JuradoContextService {
    private final IJuradoService juradoRepo;
    private final IUsuarioService usuarioService;

    public Jurado resolveFromSession(HttpSession session) {
        Object obj = session.getAttribute("usuario");
        if (obj == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        Usuario u = (Usuario) obj;

        // Si la Persona viene null por pereza/cambio de clase por DevTools, recargar
        // desde BD
        if (u.getPersona() == null || u.getPersona().getIdPersona() == null) {
            u = usuarioService.findByUsuario(u.getUsuario())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida (usuario)"));
            session.setAttribute("usuario", u); // rehidrata sesión
        }

        Long idPersona = u.getPersona().getIdPersona();
        return juradoRepo.findActivoByPersonaId(idPersona)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Persona no es jurado activo"));
    }
}
