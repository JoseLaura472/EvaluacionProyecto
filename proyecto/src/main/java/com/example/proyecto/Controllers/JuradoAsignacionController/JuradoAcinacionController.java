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
import com.example.proyecto.Models.IService.ICategoriaActividadService;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IJuradoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/jurado-asignacion")
@RequiredArgsConstructor
public class JuradoAcinacionController {
    
    private final IJuradoAsignacionService juradoAsignacionService;
    private final IActividadService actividadService;
    private final IJuradoService juradoService;
    private final ICategoriaActividadService categoriaActividadService;

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
        model.addAttribute("listarCategorias", categoriaActividadService.listarActividades());
        model.addAttribute("listarJurados", juradoService.listarParticipantes());
        return "vista/juradoAsignacion/formulario";
    }

    @PostMapping("/formulario-edit/{idJuradoAsignacion}")
    public String formularioEdit(Model model, @PathVariable("idJuradoAsignacion") Long idJuradoAsignacion)
            throws Exception {
        model.addAttribute("juradoAsignacion", juradoAsignacionService.findById(idJuradoAsignacion));
        model.addAttribute("edit", "true");
        model.addAttribute("listarActividades", actividadService.listarActividades());
        model.addAttribute("listarCategorias", categoriaActividadService.listarActividades()); // <-- NUEVO
        model.addAttribute("listarJurados", juradoService.listarParticipantes());
        return "vista/juradoAsignacion/formulario";
    }

    @PostMapping("/registrar-juradoAsignacion")
    public ResponseEntity<String> registro(@ModelAttribute JuradoAsignacion ja) {

        // XOR: exactamente una de las dos
        Long idAct = (ja.getActividad() != null && ja.getActividad().getIdActividad() != null)
                      ? ja.getActividad().getIdActividad() : null;
        Long idCat = (ja.getCategoriaActividad() != null && ja.getCategoriaActividad().getIdCategoriaActividad() != null)
                      ? ja.getCategoriaActividad().getIdCategoriaActividad() : null;

        if ((idAct == null && idCat == null) || (idAct != null && idCat != null)) {
            return ResponseEntity.badRequest().body("Debe seleccionar exactamente una opción: Actividad o Categoría (no ambas).");
        }

        // Normalizar: si viene categoría, actividad = null; si viene actividad, categoría = null.
        if (idCat != null) ja.setActividad(null);
        if (idAct != null) ja.setCategoriaActividad(null);

        if (ja.getJurado() == null || ja.getJurado().getIdJurado() == null) {
            return ResponseEntity.badRequest().body("Debe seleccionar un Jurado.");
        }

        ja.setEstado("A");
        juradoAsignacionService.save(ja);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-juradoAsignacion")
    public ResponseEntity<String> modificar(@ModelAttribute JuradoAsignacion ja) {

        if (ja.getIdJuradoAsignacion() == null) {
            return ResponseEntity.badRequest().body("ID inválido");
        }

        Long idAct = (ja.getActividad() != null && ja.getActividad().getIdActividad() != null)
                      ? ja.getActividad().getIdActividad() : null;
        Long idCat = (ja.getCategoriaActividad() != null && ja.getCategoriaActividad().getIdCategoriaActividad() != null)
                      ? ja.getCategoriaActividad().getIdCategoriaActividad() : null;

        if ((idAct == null && idCat == null) || (idAct != null && idCat != null)) {
            return ResponseEntity.badRequest().body("Debe seleccionar exactamente una opción: Actividad o Categoría (no ambas).");
        }

        if (idCat != null) ja.setActividad(null);
        if (idAct != null) ja.setCategoriaActividad(null);

        if (ja.getJurado() == null || ja.getJurado().getIdJurado() == null) {
            return ResponseEntity.badRequest().body("Debe seleccionar un Jurado.");
        }

        ja.setEstado("A");
        juradoAsignacionService.save(ja);
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
