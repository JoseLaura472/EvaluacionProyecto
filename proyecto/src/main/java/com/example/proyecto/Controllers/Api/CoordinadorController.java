package com.example.proyecto.Controllers.Api;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Entity.Participante;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.IService.IActividadService;
import com.example.proyecto.Models.IService.IInscripcionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/coordinador")
@RequiredArgsConstructor
public class CoordinadorController {
    private final IActividadService actividadService;
    private final IInscripcionService inscripcionService;

    /**
     * Vista del panel de control del coordinador
     */
    @GetMapping("/panel-control")
    public String panelControl(HttpSession session, RedirectAttributes flash, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        model.addAttribute("actividad", actividadService.findById(2L));

        // Aquí puedes agregar datos de la actividad activa
        model.addAttribute("usuario", usuario);
        
        return "vista/jurado/control-tiempo";
    }

    /**
     * Vista del panel de control del coordinador
     */
    @GetMapping("/panel-control/{idActividad}")
    public String panelControl(
            @PathVariable Long idActividad,
            HttpSession session, 
            RedirectAttributes flash, 
            Model model) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if (usuario == null) {
            flash.addFlashAttribute("error", "Debes iniciar sesión");
            return "redirect:/LoginR";
        }

        // Verificar que sea admin o coordinador
        String estado = usuario.getEstado() != null ? usuario.getEstado().trim().toUpperCase() : "";
        if (!"A".equals(estado)) {
            flash.addFlashAttribute("warn", "No tienes permisos");
            return "redirect:/inicio";
        }

        // Cargar datos de la actividad
        Actividad actividad = actividadService.findById(idActividad);
        if (actividad == null) {
            flash.addFlashAttribute("error", "Actividad no encontrada");
            return "redirect:/admin/inicio";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("actividad", actividad);
        model.addAttribute("idActividad", idActividad);
        
        return "vista/coordinador/panel-control";
    }

    /**
     * Obtener lista de participantes inscritos en una actividad
     */
    @GetMapping("/participantes/{idActividad}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerParticipantes(
            @PathVariable Long idActividad,
            @RequestParam(required = false) Long idCategoria) {
        try {
            List<Inscripcion> inscripciones;
            
            inscripciones = inscripcionService.findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividadOrderByParticipante_PosicionAsc(idActividad, 4L);
            
            List<Map<String, Object>> resultado = inscripciones.stream()
                .filter(i -> i.getParticipante() != null) // Validar que tenga participante
                .map(i -> {
                    Participante p = i.getParticipante();
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getIdParticipante());
                    map.put("nombre", p.getNombre());
                    map.put("institucion", p.getInstitucion());
                    map.put("categoria", i.getCategoriaActividad() != null ? 
                            i.getCategoriaActividad().getNombre() : "Sin categoría");
                    map.put("estado", "pendiente"); // Puedes agregar lógica para estados
                    return map;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            System.err.println("[Obtener Participantes] Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    /**
     * Obtener participantes agrupados por categoría
     */
    @GetMapping("/participantes/{idActividad}/por-categoria")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerParticipantesPorCategoria(
            @PathVariable Long idActividad) {
        try {
            List<Inscripcion> inscripciones = inscripcionService.findByActividad(idActividad);
            
            // Agrupar por categoría
            Map<String, List<Map<String, Object>>> agrupados = inscripciones.stream()
                .filter(i -> i.getParticipante() != null && i.getCategoriaActividad() != null)
                .collect(Collectors.groupingBy(
                    i -> i.getCategoriaActividad().getNombre(),
                    Collectors.mapping(i -> {
                        Participante p = i.getParticipante();
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", p.getIdParticipante());
                        map.put("nombre", p.getNombre());
                        map.put("institucion", p.getInstitucion());
                        map.put("estado", "pendiente");
                        return map;
                    }, Collectors.toList())
                ));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categorias", agrupados);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
