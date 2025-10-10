package com.example.proyecto.Controllers.RubricaCriterioController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.RubricaCriterio;
import com.example.proyecto.Models.IService.IRubricaCriterioServcie;
import com.example.proyecto.Models.IService.IRubricaService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/rubrica-criterio")
@RequiredArgsConstructor
public class RubricaCriterioController {
    
    private final IRubricaCriterioServcie rubricaCriterioServcie;
    private final IRubricaService rubricaService;
    
    @GetMapping("/vista")
    public String vista() {
        return "vista/rubricaCriterio/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        model.addAttribute("listarRubricaCriterios", rubricaCriterioServcie.listarRubricaCriterio());
        return "vista/rubricaCriterio/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, RubricaCriterio rubricaCriterio) {
        model.addAttribute("listarRubricas", rubricaService.listarRubrica());
        return "vista/rubricaCriterio/formulario";
    }

    @PostMapping("/formulario-edit/{idRubricaCriterio}")
    public String formularioEdit(Model model, @PathVariable("idRubricaCriterio") Long idRubricaCriterio)
            throws Exception {
        model.addAttribute("rubricaCriterio", rubricaCriterioServcie.findById(idRubricaCriterio));
        model.addAttribute("edit", "true");
        return "vista/rubricaCriterio/formulario";
    }

    @PostMapping("/registrar-rubricaCriterio")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute RubricaCriterio rubricaCriterio) {

        String nombre = rubricaCriterio.getNombre() == null ? "" : rubricaCriterio.getNombre().trim();
        if (nombre.isEmpty()) return ResponseEntity.badRequest().body("El nombre es obligatorio");

        if (rubricaCriterioServcie.buscarPorNombre(nombre).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un registro activo con ese nombre");
        }

        rubricaCriterio.setEstado("A");
        rubricaCriterioServcie.save(rubricaCriterio);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-rubricaCriterio")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute RubricaCriterio rubricaCriterio) {
        rubricaCriterio.setEstado("A");
        rubricaCriterioServcie.save(rubricaCriterio);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idRubricaCriterio}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idRubricaCriterio") Long idRubricaCriterio)
            throws Exception {
        RubricaCriterio p = rubricaCriterioServcie.findById(idRubricaCriterio);
        p.setEstado("X"); // ó "ELIMINADO"
        rubricaCriterioServcie.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }

}
