package com.example.proyecto.Controllers.ParticipanteController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.Participante;
import com.example.proyecto.Models.IService.IParticipanteService;
import com.example.proyecto.Models.IService.ITipoParticipanteService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/participante")
@RequiredArgsConstructor
public class ParticipanteController {

    private final IParticipanteService participanteService;
    private final ITipoParticipanteService tipoParticipanteService;

    @GetMapping("/vista")
    public String vista() {
        return "vista/participante/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        model.addAttribute("listarParticipantes", participanteService.listarParticipantes());
        return "vista/participante/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, Participante participante) {
        model.addAttribute("listaTipoParticipantes", tipoParticipanteService.listarTipoParticipantes());
        return "vista/participante/formulario";
    }

    @PostMapping("/formulario-edit/{idParticipante}")
    public String formularioEdit(Model model, @PathVariable("idParticipante") Long idParticipante)
            throws Exception {
        model.addAttribute("participante", participanteService.findById(idParticipante));
        model.addAttribute("edit", "true");
        return "vista/participante/formulario";
    }

    @PostMapping("/registrar-participante")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute Participante participante) {

        String nombre = participante.getNombre() == null ? "" : participante.getNombre().trim();
        if (nombre.isEmpty()) return ResponseEntity.badRequest().body("El nombre es obligatorio");

        if (participanteService.buscarPorNombre(nombre).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un registro activo con ese nombre");
        }

        participante.setEstado("A");
        participanteService.save(participante);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-participante")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute Participante participante) {
        participante.setEstado("A");
        participanteService.save(participante);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idParticipante}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idParticipante") Long idParticipante)
            throws Exception {
        Participante p = participanteService.findById(idParticipante);
        p.setEstado("X"); // ó "ELIMINADO"
        participanteService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
