package com.example.proyecto.Controllers.PersonaControllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IJuradoService;
import com.example.proyecto.Models.IService.IPersonaService;
import com.example.proyecto.Models.IService.IUsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/jurado")
@RequiredArgsConstructor
public class JuradoController {
    
    private final IPersonaService personaService;
    private final IJuradoService juradoService;
    private final IUsuarioService usuarioService;
    private final IJuradoAsignacionService asignacionService;

	@GetMapping("/vista")
    public String inicioJurado() {
        return "vista/jurado/vista";
    }

    @GetMapping("/panel")
    public String panelJurado(HttpSession session, Model model,
                                @RequestParam(name="actId", required=false) Long actId) {

        // 1) validar sesión
        var usuario = (Usuario) session.getAttribute("usuario");
        var persona = (Persona) session.getAttribute("persona");
        if (usuario == null || persona == null) {
        return "redirect:/LoginR";
        }

        // 2) resolver jurado por persona (ajusta a tu finder)
        Jurado jurado = juradoService.findActivoByPersonaId(persona.getIdPersona()).orElse(null);
        if (jurado == null) {
        model.addAttribute("error", "Tu usuario no está registrado como jurado.");
        return "vista/jurado/vista"; // o una página de aviso
        }

        // 3) actividades asignadas al jurado
        List<Actividad> actividades = asignacionService.findActividadesAsignadas(jurado.getIdJurado());

        // actividad actual (si viene en query param, úsalo; si no, la primera)
        Actividad actividadActual = null;
        if (!actividades.isEmpty()) {
        if (actId != null) {
            actividadActual = actividades.stream()
                .filter(a -> a.getIdActividad().equals(actId))
                .findFirst().orElse(actividades.get(0));
        } else {
            actividadActual = actividades.get(0);
        }
        }

        // 4) mandar al modelo lo necesario para el header
        model.addAttribute("usuarioNom", usuario.getUsuario());
        model.addAttribute("personaNom", persona.getNombres());
        model.addAttribute("personaApe", persona.getPaterno()); // si lo tienes
        model.addAttribute("juradoId", jurado.getIdJurado());
        model.addAttribute("actividades", actividades);
        model.addAttribute("actividadActual", actividadActual);
        model.addAttribute("actividadId", actividadActual != null ? actividadActual.getIdActividad() : null);

        return "vista/jurado/panel";
    }

	@PostMapping("/tabla-registros")
    public String tablaJurado(Model model) throws Exception {
        List<Jurado> listaJurados = juradoService.listarParticipantes();
        model.addAttribute("listaJurados", listaJurados);
        return "vista/jurado/tabla";
    }

	@PostMapping("/formulario")
    public String formularioJurado(Model model, Jurado jurado) {
		model.addAttribute("listaPersonas", personaService.listarParticipantes());
        return "vista/jurado/formulario";
    }

	@PostMapping("/formulario-edit/{id_jurado}")
    public String formularioEditJurado(Model model, @PathVariable("id_jurado") Long idJurado) throws Exception{
        model.addAttribute("jurado", juradoService.findOne(idJurado));
		model.addAttribute("persona", new Persona());
        model.addAttribute("edit", "true");
        return "vista/jurado/formulario";
    }

    @PostMapping("/registrar-jurado")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String,Object>> registrarJurado(@Validated Jurado jurado,
        @RequestParam(name="usuarioNom", required=false) String usuarioNom,
        @RequestParam(name="contrasenaPlano") String contrasenaPlano) {

        // 1) Validaciones
        if (jurado.getPersona()==null || jurado.getPersona().getIdPersona()==null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "Debe seleccionar persona"));
        }
        Persona persona = personaService.findOne(jurado.getPersona().getIdPersona());
        if (persona == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "Persona no encontrada"));
        }
        if (juradoService.existsByPersona_IdPersona(persona.getIdPersona())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "La persona ya es jurado"));
        }

            // 2) Guardar jurado
        jurado.setPersona(persona);
        jurado.setEstado("A");
        juradoService.save(jurado);

        // 3) Crear/asegurar usuario con contraseña DEFINIDA
        Usuario usuario = usuarioService.crearUsuarioParaJuradoSiNoExiste(persona, usuarioNom, contrasenaPlano);

        return ResponseEntity.ok(Map.of(
            "ok", true,
            "msg", "Se registró el jurado correctamente",
            "usuario", usuario.getUsuario()
        ));
    }

	@PostMapping(value = "/modificar-jurado")
    public ResponseEntity<String> modificarJurado(HttpServletRequest request, Jurado jurado,
            RedirectAttributes redirectAttrs) {
        jurado.setEstado("A");
        juradoService.save(jurado);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

	@PostMapping("/eliminar/{id_rol}")
    public ResponseEntity<String> eliminarJurado(Model model, @PathVariable("id_rol") Long idJurado) throws Exception {
        Jurado jurado = juradoService.findOne(idJurado);
        jurado.setEstado("ELIMINADO");
        juradoService.save(jurado);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
