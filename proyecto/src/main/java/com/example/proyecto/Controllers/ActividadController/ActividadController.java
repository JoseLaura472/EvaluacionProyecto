package com.example.proyecto.Controllers.ActividadController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.IService.IActividadService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/actividad")
@RequiredArgsConstructor
public class ActividadController {
    
    private final IActividadService actividadService;

    @GetMapping("/vista")
    public String vista() {
        return "vista/actividad/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        model.addAttribute("listarActividades", actividadService.listarActividades());
        return "vista/actividad/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, Actividad actividad) {
        return "vista/actividad/formulario";
    }

    @PostMapping("/formulario-edit/{idActividad}")
    public String formularioEdit(Model model, @PathVariable("idActividad") Long idActividad)
            throws Exception {
        model.addAttribute("actividad", actividadService.findById(idActividad));
        model.addAttribute("edit", "true");
        return "vista/actividad/formulario";
    }

    @PostMapping("/registrar-actividad")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute Actividad actividad) {

        String nombre = actividad.getNombre() == null ? "" : actividad.getNombre().trim();
        if (nombre.isEmpty()) return ResponseEntity.badRequest().body("El nombre es obligatorio");

        if (actividadService.buscarPorNombre(nombre).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un registro activo con ese nombre");
        }

        actividad.setEstado("A");
        actividadService.save(actividad);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-actividad")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute Actividad actividad) {
        actividad.setEstado("A");
        actividadService.save(actividad);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idActividad}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idActividad") Long idActividad)
            throws Exception {
        Actividad p = actividadService.findById(idActividad);
        p.setEstado("X"); // ó "ELIMINADO"
        actividadService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
