package com.example.proyecto.Controllers.TipoParticipanteController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.proyecto.Models.Entity.TipoParticipante;
import com.example.proyecto.Models.IService.ITipoParticipanteService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/tipo-participante")
@RequiredArgsConstructor
public class TipoParticipanteController {

    private final ITipoParticipanteService tipoParticipanteService;

    @GetMapping("/vista")
    public String vista() {
        return "vista/tipoParticipante/vista";
    }

    @PostMapping("/tabla-registros")
    public String tabla(Model model) throws Exception {
        List<TipoParticipante> lista = tipoParticipanteService.findAll();
        model.addAttribute("listarTipoParticipantes", lista);
        return "vista/tipoParticipante/tabla";
    }

    @PostMapping("/formulario")
    public String formulario(Model model, TipoParticipante tipoParticipante) {
        return "vista/tipoParticipante/formulario";
    }

    @PostMapping("/formulario-edit/{idTipoParticipante}")
    public String formularioEdit(Model model, @PathVariable("idTipoParticipante") Long idTipoParticipante)
            throws Exception {
        model.addAttribute("tipoParticipante", tipoParticipanteService.findById(idTipoParticipante));
        model.addAttribute("edit", "true");
        return "vista/tipoParticipante/formulario";
    }

    @PostMapping("/registrar-tipoParticipante")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute TipoParticipante tipoParticipante) {

        String nombre = tipoParticipante.getNombre() == null ? "" : tipoParticipante.getNombre().trim();
        if (nombre.isEmpty()) return ResponseEntity.badRequest().body("El nombre es obligatorio");

        if (tipoParticipanteService.buscarPorNombre(nombre).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un registro activo con ese nombre");
        }

        tipoParticipante.setEstado("A");
        tipoParticipanteService.save(tipoParticipante);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-tipoParticipante")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute TipoParticipante tipoParticipante) {
        tipoParticipante.setEstado("A");
        tipoParticipanteService.save(tipoParticipante);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idTipoParticipante}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idTipoParticipante") Long idTipoParticipante)
            throws Exception {
        TipoParticipante p = tipoParticipanteService.findById(idTipoParticipante);
        p.setEstado("X"); // ó "ELIMINADO"
        tipoParticipanteService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
