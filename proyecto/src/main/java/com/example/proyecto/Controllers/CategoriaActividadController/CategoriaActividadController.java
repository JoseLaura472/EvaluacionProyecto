package com.example.proyecto.Controllers.CategoriaActividadController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.CategoriaActividad;
import com.example.proyecto.Models.IService.IActividadService;
import com.example.proyecto.Models.IService.ICategoriaActividadService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categoria-actividad")
@RequiredArgsConstructor
public class CategoriaActividadController {
    
    private final ICategoriaActividadService categoriaActividadService;
    private final IActividadService actividadService;

    @GetMapping("/vista")
    public String vista() {
        return "vista/categoriaActividad/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        model.addAttribute("listarCategoriaActividades", categoriaActividadService.listarActividades());
        return "vista/categoriaActividad/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, CategoriaActividad categoriaActividad) {
        model.addAttribute("listarActividades", actividadService.listarActividades());
        return "vista/categoriaActividad/formulario";
    }

    @PostMapping("/formulario-edit/{idCategoriaActividad}")
    public String formularioEdit(Model model, @PathVariable("idCategoriaActividad") Long idCategoriaActividad)
            throws Exception {
        model.addAttribute("actividad", categoriaActividadService.findById(idCategoriaActividad));
        model.addAttribute("edit", "true");
        return "vista/categoriaActividad/formulario";
    }

    @PostMapping("/registrar-categoriaActividad")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute CategoriaActividad categoriaActividad) {

        String nombre = categoriaActividad.getNombre() == null ? "" : categoriaActividad.getNombre().trim();
        if (nombre.isEmpty()) return ResponseEntity.badRequest().body("El nombre es obligatorio");

        if (categoriaActividadService.buscarPorNombre(nombre).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un registro activo con ese nombre");
        }

        categoriaActividad.setEstado("A");
        categoriaActividadService.save(categoriaActividad);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-categoriaActividad")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute CategoriaActividad categoriaActividad) {
        categoriaActividad.setEstado("A");
        categoriaActividadService.save(categoriaActividad);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idCategoriaActividad}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idCategoriaActividad") Long idCategoriaActividad)
            throws Exception {
        CategoriaActividad p = categoriaActividadService.findById(idCategoriaActividad);
        p.setEstado("X"); // ó "ELIMINADO"
        categoriaActividadService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
