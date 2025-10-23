package com.example.proyecto.Controllers.PersonaControllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.Estudiante;
import com.example.proyecto.Models.IService.IEstudianteService;
import com.example.proyecto.Models.IService.IParticipanteService;
import com.example.proyecto.Models.IService.IPersonaService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/estudiante")
@RequiredArgsConstructor
public class EstudianteController {

	private final IEstudianteService estudianteService;
	private final IPersonaService personaService;
	private final IParticipanteService participanteService;

	@GetMapping("/vista")
    public String vista() {
        return "vista/estudiante/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        model.addAttribute("listasEstudiantes", estudianteService.listaEstudiantes("A"));
        return "vista/estudiante/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, Estudiante estudiante) {
        model.addAttribute("listasPersonas", personaService.listarPersona("ESTUDIANTE"));
        model.addAttribute("listasParticipante", participanteService.listarParticipantes());
        return "vista/estudiante/formulario";
    }

    @PostMapping("/formulario-edit/{idEstudiante}")
    public String formularioEdit(Model model, @PathVariable("idEstudiante") Long idEstudiante)
            throws Exception {
        model.addAttribute("estudiante", estudianteService.findOne(idEstudiante));
        model.addAttribute("edit", "true");
		model.addAttribute("listasPersonas", personaService.listarPersona("ESTUDIANTE"));
        model.addAttribute("listasParticipante", participanteService.listarParticipantes());
        return "vista/estudiante/formulario";
    }

    @PostMapping("/registrar-estudiante")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute Estudiante estudiante) {

        estudiante.setEstado("A");
        estudianteService.save(estudiante);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-estudiante")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute Estudiante estudiante) {
        estudiante.setEstado("A");
        estudianteService.save(estudiante);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idEstudiante}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idEstudiante") Long idEstudiante)
            throws Exception {
        Estudiante p = estudianteService.findOne(idEstudiante);
        p.setEstado("X"); // ó "ELIMINADO"
        estudianteService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
