package com.example.proyecto.Controllers.CategoriaPartController;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.CategoriaParticipante;
import com.example.proyecto.Models.IService.ICategoriaParticipanteService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categoria-participante")
@RequiredArgsConstructor
public class CategoriaParticipanteController {

    private final ICategoriaParticipanteService categoriaParticipanteService;

    @GetMapping("/vista")
    public String vista() {
        return "vista/categoria-participante/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        model.addAttribute("listarCategoriaParticipante", categoriaParticipanteService.findAll());
        return "vista/categoria-participante/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, CategoriaParticipante categoriaParticipante) {
        return "vista/categoria-participante/formulario";
    }

    @PostMapping("/formulario-edit/{idCategoriaParticipante}")
    public String formularioEdit(Model model, @PathVariable("idCategoriaParticipante") Long idCategoriaParticipante)
            throws Exception {
        model.addAttribute("categoriaParticipante", categoriaParticipanteService.findById(idCategoriaParticipante));
        model.addAttribute("edit", "true");
        return "vista/categoria-participante/formulario";
    }

    @PostMapping("/registrar-categoria-participante")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute CategoriaParticipante categoriaParticipante) {
        String nombre = categoriaParticipante.getNombre();
        CategoriaParticipante encontrado = categoriaParticipanteService.findByNombre(nombre);
        if (encontrado != null) {
            return ResponseEntity.ok("Ya existe esta categoria participante");
        }
        
        categoriaParticipante.setEstado("ACTIVO");
        categoriaParticipanteService.save(categoriaParticipante);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-categoriaParticipante")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute CategoriaParticipante categoriaParticipante) {
        categoriaParticipante.setNombre(categoriaParticipante.getNombre());
        categoriaParticipante.setDescripcion(categoriaParticipante.getDescripcion());
        categoriaParticipante.setEstado("ACTIVO");
        categoriaParticipanteService.save(categoriaParticipante);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idCategoriaParticipante}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idCategoriaParticipante") Long idCategoriaParticipante)
            throws Exception {
        CategoriaParticipante p = categoriaParticipanteService.findById(idCategoriaParticipante);
        p.setEstado("X");
        categoriaParticipanteService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
