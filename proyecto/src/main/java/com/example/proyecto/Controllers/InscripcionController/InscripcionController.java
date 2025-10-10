package com.example.proyecto.Controllers.InscripcionController;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.IService.IActividadService;
import com.example.proyecto.Models.IService.ICategoriaActividadService;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.example.proyecto.Models.IService.IParticipanteService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/inscripcion")
@RequiredArgsConstructor
public class InscripcionController {
    
    private final IInscripcionService inscripcionService;
    private final IActividadService actividadService;
    private final ICategoriaActividadService categoriaActividadService;
    private final IParticipanteService participanteService;

    @GetMapping("/vista")
    public String vista() {
        return "vista/inscripcion/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        model.addAttribute("listarInscripciones", inscripcionService.listarInscripciones());
        return "vista/inscripcion/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, Inscripcion inscripcion) {
        model.addAttribute("listarActividades", actividadService.listarActividades());
        model.addAttribute("listarCategoriaActividades", categoriaActividadService.listarActividades());
        model.addAttribute("listarParticipantes", participanteService.listarParticipantes());
        return "vista/inscripcion/formulario";
    }

    @PostMapping("/formulario-edit/{idInscripcion}")
    public String formularioEdit(Model model, @PathVariable("idInscripcion") Long idInscripcion)
            throws Exception {
        model.addAttribute("inscripcion", inscripcionService.findById(idInscripcion));
        model.addAttribute("edit", "true");
        return "vista/inscripcion/formulario";
    }

    @PostMapping("/registrar-inscripcion")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute Inscripcion inscripcion) {

        inscripcion.setEstado("A");
        inscripcionService.save(inscripcion);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-inscripcion")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute Inscripcion inscripcion) {
        inscripcion.setEstado("A");
        inscripcionService.save(inscripcion);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idInscripcion}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idInscripcion") Long idInscripcion)
            throws Exception {
        Inscripcion p = inscripcionService.findById(idInscripcion);
        p.setEstado("X"); // ó "ELIMINADO"
        inscripcionService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}