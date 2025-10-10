package com.example.proyecto.Controllers.RubricaController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.IService.IActividadService;
import com.example.proyecto.Models.IService.IRubricaService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/rubrica")
@RequiredArgsConstructor
public class RubricaController {
    
    private final IRubricaService rubricaService;
    private final IActividadService actividadService;

    @GetMapping("/vista")
    public String vista() {
        return "vista/rubrica/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        model.addAttribute("listarRubricas", rubricaService.listarRubrica());
        return "vista/rubrica/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, Rubrica rubrica) {
        model.addAttribute("listarActividades", actividadService.listarActividades());
        return "vista/rubrica/formulario";
    }

    @PostMapping("/formulario-edit/{idRubrica}")
    public String formularioEdit(Model model, @PathVariable("idRubrica") Long idRubrica)
            throws Exception {
        model.addAttribute("rubrica", rubricaService.findById(idRubrica));
        model.addAttribute("edit", "true");
        return "vista/rubrica/formulario";
    }

    @PostMapping("/registrar-rubrica")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute Rubrica rubrica) {

        String nombre = rubrica.getNombre() == null ? "" : rubrica.getNombre().trim();
        if (nombre.isEmpty()) return ResponseEntity.badRequest().body("El nombre es obligatorio");

        if (rubricaService.buscarPorNombre(nombre).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un registro activo con ese nombre");
        }

        rubrica.setEstado("A");
        rubricaService.save(rubrica);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-rubrica")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute Rubrica rubrica) {
        rubrica.setEstado("A");
        rubricaService.save(rubrica);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idRubrica}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idRubrica") Long idRubrica)
            throws Exception {
        Rubrica p = rubricaService.findById(idRubrica);
        p.setEstado("X"); // ó "ELIMINADO"
        rubricaService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
