package com.example.proyecto.Controllers.PersonaControllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.IService.IPersonaService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/persona")
@RequiredArgsConstructor
public class PersonaController {

    private final IPersonaService personaService;

    @GetMapping("/vista")
    public String inicioPersona() {
        return "vista/persona/vista";
    }

    @PostMapping("/tabla-registros")
    public String tablaRegistrosPersona(Model model) throws Exception {
        List<Persona> lista = personaService.findAll();
        List<String> encryptedIds = new ArrayList<>();
        model.addAttribute("listasPersonas", lista);
        model.addAttribute("id_encryptado", encryptedIds);
        return "vista/persona/tabla";
    }

    @PostMapping("/formulario")
    public String formularioPersona(Model model, Persona persona) {
        // si quieres precargar algo, setéalo en model
        return "vista/persona/formulario";
    }

    @PostMapping("/formulario-edit/{id_persona}")
    public String formularioEditPersona(Model model, @PathVariable("id_persona") Long idPersona) throws Exception {
        model.addAttribute("persona", personaService.findOne(idPersona));
        model.addAttribute("edit", "true");
        return "vista/persona/formulario";
    }

    @PostMapping("/registrar-persona")
    public ResponseEntity<String> registrarPersona(HttpServletRequest request, @ModelAttribute Persona persona) {
        // Valida unicidad si quieres (por CI)
        persona.setEstado("A");
        personaService.save(persona);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-persona")
    public ResponseEntity<String> modificarPersona(HttpServletRequest request, @ModelAttribute Persona persona) {
        persona.setEstado("A");
        personaService.save(persona);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{id_persona}")
    public ResponseEntity<String> eliminarPersona(Model model, @PathVariable("id_persona") Long idPersona) throws Exception {
        Persona p = personaService.findOne(idPersona);
        p.setEstado("X"); // ó "ELIMINADO"
        personaService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
