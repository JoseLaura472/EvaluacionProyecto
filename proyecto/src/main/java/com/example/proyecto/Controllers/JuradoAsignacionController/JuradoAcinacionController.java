package com.example.proyecto.Controllers.JuradoAsignacionController;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.JuradoAsignacion;
import com.example.proyecto.Models.IService.IActividadService;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IJuradoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/jurado-asignacion")
@RequiredArgsConstructor
public class JuradoAcinacionController {
    
    private final IJuradoAsignacionService juradoAsignacionService;
    private final IActividadService actividadService;
    private final IJuradoService juradoService;

    @GetMapping("/vista")
    public String vista() {
        return "vista/juradoAsignacion/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        model.addAttribute("listarJuradoAsignaciones", juradoAsignacionService.listarJuradoAsignacion());
        return "vista/juradoAsignacion/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, JuradoAsignacion juradoAsignacion) {
        model.addAttribute("listarActividades", actividadService.listarActividades());
        model.addAttribute("listarJurados", juradoService.listarParticipantes());
        return "vista/juradoAsignacion/formulario";
    }

    @PostMapping("/formulario-edit/{idJuradoAsignacion}")
    public String formularioEdit(Model model, @PathVariable("idJuradoAsignacion") Long idJuradoAsignacion)
            throws Exception {
        model.addAttribute("juradoAsignacion", juradoAsignacionService.findById(idJuradoAsignacion));
        model.addAttribute("edit", "true");
        return "vista/juradoAsignacion/formulario";
    }

    @PostMapping("/registrar-juradoAsignacion")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute JuradoAsignacion juradoAsignacion) {

        juradoAsignacion.setEstado("A");
        juradoAsignacionService.save(juradoAsignacion);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-juradoAsignacion")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute JuradoAsignacion juradoAsignacion) {
        juradoAsignacion.setEstado("A");
        juradoAsignacionService.save(juradoAsignacion);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idJuradoAsignacion}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idJuradoAsignacion") Long idJuradoAsignacion)
            throws Exception {
        JuradoAsignacion p = juradoAsignacionService.findById(idJuradoAsignacion);
        p.setEstado("X"); // ó "ELIMINADO"
        juradoAsignacionService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
