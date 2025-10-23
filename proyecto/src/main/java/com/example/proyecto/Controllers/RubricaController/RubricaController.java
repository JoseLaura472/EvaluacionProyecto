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
import com.example.proyecto.Models.IService.ICategoriaActividadService;
import com.example.proyecto.Models.IService.IRubricaService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/rubrica")
@RequiredArgsConstructor
public class RubricaController {
    
    private final IRubricaService rubricaService;
    private final IActividadService actividadService;
    private final ICategoriaActividadService categoriaActividadService;

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
        model.addAttribute("listarCategorias", categoriaActividadService.listarActividades());
        return "vista/rubrica/formulario";
    }

    @PostMapping("/formulario-edit/{idRubrica}")
    public String formularioEdit(Model model, @PathVariable("idRubrica") Long idRubrica)
            throws Exception {
        model.addAttribute("rubrica", rubricaService.findById(idRubrica));
        model.addAttribute("edit", "true");
        model.addAttribute("listarActividades", actividadService.listarActividades());
        model.addAttribute("listarCategorias", categoriaActividadService.listarActividades
    ());
        return "vista/rubrica/formulario";
    }

    @PostMapping("/registrar-rubrica")
    public ResponseEntity<String> registro(HttpServletRequest request,
            @ModelAttribute Rubrica rubrica) {

        String nombre  = rubrica.getNombre()  == null ? "" : rubrica.getNombre().trim();
        String version = rubrica.getVersion() == null ? "" : rubrica.getVersion().trim();

        if (nombre.isEmpty())  return ResponseEntity.badRequest().body("El nombre es obligatorio");
        if (version.isEmpty()) return ResponseEntity.badRequest().body("La versión es obligatoria");

        Long idAct  = (rubrica.getActividad() != null && rubrica.getActividad().getIdActividad() != null)
                        ? rubrica.getActividad().getIdActividad() : null;
        Long idCat  = (rubrica.getCategoriaActividad() != null && rubrica.getCategoriaActividad().getIdCategoriaActividad() != null)
                        ? rubrica.getCategoriaActividad().getIdCategoriaActividad() : null;

        // Regla XOR
        if ((idAct == null && idCat == null) || (idAct != null && idCat != null)) {
            return ResponseEntity.badRequest().body("Debe seleccionar exactamente una opción: Actividad o Categoría (no ambas).");
        }

        // Normalizar: si viene categoría, forzar actividad = null (y viceversa)
        if (idCat != null) rubrica.setActividad(null);
        if (idAct != null) rubrica.setCategoriaActividad(null);

        // Duplicados por (actividad|categoría) + versión, sólo activos
        final String ACTIVO = "A";
        if (idAct != null) {
            if (rubricaService.existsByActividad_IdActividadAndVersionAndEstado(idAct, version, ACTIVO)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe una rúbrica activa con la misma Actividad y Versión.");
            }
        } else { // idCat != null
            if (rubricaService.existsByActividad_IdActividadAndVersionAndEstado(idCat, version, ACTIVO)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe una rúbrica activa con la misma Categoría y Versión.");
            }
        }

        rubrica.setEstado(ACTIVO);
        rubricaService.save(rubrica);
        return ResponseEntity.ok("Se realizó el registro correctamente");
    }

    @PostMapping("/modificar-rubrica")
    public ResponseEntity<String> modificar(HttpServletRequest request,
            @ModelAttribute Rubrica rubrica) {
        if (rubrica.getIdRubrica() == null)
        return ResponseEntity.badRequest().body("ID inválido");

        String nombre  = rubrica.getNombre()  == null ? "" : rubrica.getNombre().trim();
        String version = rubrica.getVersion() == null ? "" : rubrica.getVersion().trim();

        if (nombre.isEmpty())  return ResponseEntity.badRequest().body("El nombre es obligatorio");
        if (version.isEmpty()) return ResponseEntity.badRequest().body("La versión es obligatoria");

        Long idAct = (rubrica.getActividad() != null && rubrica.getActividad().getIdActividad() != null)
                        ? rubrica.getActividad().getIdActividad() : null;
        Long idCat = (rubrica.getCategoriaActividad() != null && rubrica.getCategoriaActividad().getIdCategoriaActividad() != null)
                        ? rubrica.getCategoriaActividad().getIdCategoriaActividad() : null;

        // Regla XOR
        if ((idAct == null && idCat == null) || (idAct != null && idCat != null)) {
            return ResponseEntity.badRequest().body("Debe seleccionar exactamente una opción: Actividad o Categoría (no ambas).");
        }

        if (idCat != null) rubrica.setActividad(null);
        if (idAct != null) rubrica.setCategoriaActividad(null);

        final String ACTIVO = "A";
        Long idRubrica = rubrica.getIdRubrica();

        if (idAct != null) {
            if (rubricaService.existsByActividad_IdActividadAndVersionAndEstadoAndIdRubricaNot(idAct, version, ACTIVO, idRubrica)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe otra rúbrica activa con la misma Actividad y Versión.");
            }
        } else {
            if (rubricaService.existsByActividad_IdActividadAndVersionAndEstadoAndIdRubricaNot(idCat, version, ACTIVO, idRubrica)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Ya existe otra rúbrica activa con la misma Categoría y Versión.");
            }
        }

        rubrica.setEstado(ACTIVO);
        rubricaService.save(rubrica);
        return ResponseEntity.ok("Se realizó la modificación correctamente");
    }

    @PostMapping("/eliminar/{idRubrica}")
    public ResponseEntity<String> eliminar(Model model, @PathVariable("idRubrica") Long idRubrica)
            throws Exception {
        Rubrica p = rubricaService.findById(idRubrica);
        p.setEstado("X");
        rubricaService.save(p);
        return ResponseEntity.ok("Registro Eliminado");
    }
}
