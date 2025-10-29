package com.example.proyecto.Controllers.PersonaControllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Dto.EvaluacionGuardarCategoriaDto;
import com.example.proyecto.Models.Dto.ParticipanteListadoDto;
import com.example.proyecto.Models.Dto.RubricaDto;
import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.JuradoAsignacion;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.IService.IEvaluacionService;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IJuradoService;
import com.example.proyecto.Models.IService.IParticipanteService;
import com.example.proyecto.Models.IService.IPersonaService;
import com.example.proyecto.Models.IService.IRubricaService;
import com.example.proyecto.Models.IService.IUsuarioService;
import com.example.proyecto.Models.IServiceImpl.EvaluacionCategoriaAdapterService;
import com.example.proyecto.Models.Service.EvaluacionService;
import com.example.proyecto.Models.Service.JuradoLookupService;

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
    private final IJuradoAsignacionService juradoAsignacionService;
    private final IParticipanteService participanteService;
    private final IRubricaService rubricaService;
    private final EvaluacionCategoriaAdapterService evaluacionCategoriaAdapterService;
    private final JuradoLookupService juradoLookupService;
    private final EvaluacionService evaluacionService;

    @GetMapping("/vista")
    public String inicioJurado() {
        return "vista/jurado/vista";
    }

    @GetMapping("/panel")
    public String panelJurado(HttpSession session, Model model,
            @RequestParam(name = "actId", required = false) Long actId) {

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
        model.addAttribute("listaPersonas", personaService.listarPersona("A"));
        return "vista/jurado/formulario";
    }

    @PostMapping("/formulario-edit/{id_jurado}")
    public String formularioEditJurado(Model model, @PathVariable("id_jurado") Long idJurado) throws Exception {
        model.addAttribute("jurado", juradoService.findOne(idJurado));
        model.addAttribute("persona", new Persona());
        model.addAttribute("edit", "true");
        return "vista/jurado/formulario";
    }

    @PostMapping("/registrar-jurado")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> registrarJurado(@Validated Jurado jurado,
            @RequestParam(name = "usuarioNom", required = false) String usuarioNom,
            @RequestParam(name = "contrasenaPlano") String contrasenaPlano) {

        // 1) Validaciones
        if (jurado.getPersona() == null || jurado.getPersona().getIdPersona() == null) {
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
                "usuario", usuario.getUsuario()));
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

    @GetMapping("/panel-categoria")
    public String vistaPanelCategoria(HttpSession session, RedirectAttributes flash) {
        return "vista/jurado/panel-categoria";
    }

    // 1) Mi categoría (la primera asignada; si hay varias, devuelve la primera)
    @ResponseBody
    @GetMapping("/api/mi-categoria")
    public ResponseEntity<?> miCategoria(HttpSession session) {
        Persona persona = (Persona) session.getAttribute("persona");
        Long idPersona = (persona != null) ? persona.getIdPersona() : null;
        if (idPersona == null) {
            return ResponseEntity.status(401).body("Sesión inválida");
        }

        var cats = juradoAsignacionService.listarCategoriasDeJuradoPorPersona(idPersona);
        if (cats == null || cats.isEmpty()) {
            // 404 semántico: el recurso “mi categoría” no existe para este usuario
            return ResponseEntity.status(404).body("No tienes categoría asignada");
        }
        return ResponseEntity.ok(cats.get(0));
    }

    // 2) Todas las rúbricas activas de la categoría (las 4)
    @ResponseBody
    @GetMapping("/api/rubricas")
    public List<RubricaDto> rubricas(@RequestParam Long categoriaId) {
        return rubricaService.obtenerRubricasActivasPorCategoria(categoriaId);
    }

    // 3) Participantes de la categoría
    @ResponseBody
    @GetMapping("/api/participantes")
    public List<ParticipanteListadoDto> participantes(@RequestParam Long categoriaId,
            HttpSession session,
            @RequestParam(name = "soloPendientes", defaultValue = "true") boolean soloPendientes) {
        Persona persona = (Persona) session.getAttribute("persona");
        if (persona == null)
            return List.of();
        Jurado jurado = juradoLookupService.cargarPorPersonaOrThrow(persona.getIdPersona());

        if (soloPendientes) {
            long totalRubricas = rubricaService.countActivasByCategoria(categoriaId);
            System.out.println("[DEBUG] totalRubricasActivas=" + totalRubricas + " cat=" + categoriaId);
        }

        List<ParticipanteListadoDto> r = (soloPendientes)
                ? participanteService.listarPendientesPorCategoria(categoriaId, jurado.getIdJurado())
                : participanteService.listarPorCategoria(categoriaId);

        System.out.println("[/api/participantes] cat=" + categoriaId + " soloPend=" + soloPendientes + " -> " + r.size()
                + " filas");
        return r;
    }

    // 4) Guardar evaluación (por rúbrica actual)
    @ResponseBody
    @PostMapping("/api/evaluar")
    public ResponseEntity<?> evaluar(@RequestBody EvaluacionGuardarCategoriaDto dto, HttpSession session) {
        Persona persona = (Persona) session.getAttribute("persona");
        if (persona == null)
            return ResponseEntity.status(401).body("Sesión inválida");
        Jurado jurado = juradoLookupService.cargarPorPersonaOrThrow(persona.getIdPersona());
        evaluacionCategoriaAdapterService.guardarEvaluacionCategoria(jurado, dto);
        return ResponseEntity.ok().build();
    }

    /* ENTRADA UNIVERSITARIA */
    // Mostrar vista principal

    @GetMapping("/panel-entrada")
    public String vistaPanelEntrada(HttpSession session, RedirectAttributes flash, Model model) {

        // Verificar usuario logueado
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            flash.addFlashAttribute("error", "Debes iniciar sesión primero.");
            return "redirect:/LoginR";
        }

        // Verificar que es jurado
        String estado = (usuario.getEstado() != null) ? usuario.getEstado().trim().toUpperCase() : "";
        if (!"J".equals(estado)) {
            flash.addFlashAttribute("warn", "No tienes permiso para acceder a este panel.");
            return "redirect:/inicio";
        }

        // Obtener idJurado de la sesión
        Long idJurado = (Long) session.getAttribute("idJurado");
        if (idJurado == null) {
            flash.addFlashAttribute("error", "No se encontró información del jurado.");
            return "redirect:/LoginR";
        }

        try {
            // Buscar asignación del jurado
            JuradoAsignacion asignacion = asignacionService.findFirstByJuradoId(idJurado);

            if (asignacion == null || asignacion.getActividad() == null) {
                flash.addFlashAttribute("warn", "No tienes actividades asignadas actualmente.");
                return "redirect:/jurado/panel";
            }

            // Obtener datos para la vista
            Jurado jurado = juradoService.findOne(idJurado);
            Actividad actividad = asignacion.getActividad();

            // Agregar al modelo
            model.addAttribute("jurado", jurado);
            model.addAttribute("idActividad", actividad.getIdActividad());
            model.addAttribute("actividad", actividad);

            System.out.println("[Panel] Jurado: " + jurado.getIdJurado() +
                    " - Actividad: " + actividad.getIdActividad() +
                    " - " + actividad.getNombre());

            return "vista/jurado/panel-entrada";

        } catch (Exception e) {
            System.err.println("[Panel] Error: " + e.getMessage());
            e.printStackTrace();
            flash.addFlashAttribute("error", "Error al cargar la actividad: " + e.getMessage());
            return "redirect:/jurado/panel";
        }
    }

    // Obtener datos para evaluar
    @GetMapping(value = "/datos/{idActividad}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerDatos(
            @PathVariable Long idActividad,
            HttpSession session) {

        Long idJurado = (Long) session.getAttribute("idJurado");
        if (idJurado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "No autorizado"));
        }

        try {
            Map<String, Object> datos = evaluacionService.obtenerDatosEvaluacion(idActividad, idJurado);
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            // log.error("Error al obtener datos de evaluación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Guardar evaluación
    @PostMapping("/guardar")
    @ResponseBody
    public Map<String, Object> guardarEvaluacion(
            @RequestBody Map<String, Object> datos,
            HttpSession session) {

        Long idJurado = (Long) session.getAttribute("idJurado");

        try {
            evaluacionService.guardarEvaluacion(datos, idJurado);
            return Map.of("success", true);
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @PostMapping("/finalizar")
    @ResponseBody
    public Map<String, Object> finalizarEvaluacion(HttpSession session) {
        Long idJurado = (Long) session.getAttribute("idJurado");

        if (idJurado == null) {
            return Map.of("success", false, "message", "No autorizado");
        }

        try {
            // Aquí puedes agregar lógica adicional si necesitas
            // Por ejemplo: marcar como finalizadas, enviar notificaciones, etc.
            return Map.of("success", true, "message", "Evaluaciones finalizadas correctamente");
        } catch (Exception e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }
}
