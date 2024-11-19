package com.example.proyecto.Controllers.EvaluacionControllers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Dao.IEvaluacionDao;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Entity.Puntaje;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Service.ICategoriaCriterioService;
import com.example.proyecto.Models.Service.IEvaluacionService;
import com.example.proyecto.Models.Service.IJuradoService;
import com.example.proyecto.Models.Service.IPonderacionService;
import com.example.proyecto.Models.Service.IProyectoService;
import com.example.proyecto.Models.Service.IPuntajeService;
import com.example.proyecto.Models.Service.IUsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EvaluacionController {

    @Autowired
    private IEvaluacionService evaluacionService;

    @Autowired
    private IProyectoService proyectoService;

    @Autowired
    private IJuradoService juradoService;

    @Autowired
    private IEvaluacionDao evaluacionDao;

    @Autowired
    private ICategoriaCriterioService categoriaCriterioService;

    @Autowired
    private IPuntajeService puntajeService;

    @Autowired
    private IPonderacionService ponderacionService;

    @Autowired
    private IUsuarioService usuarioService;

    // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
    @RequestMapping(value = "/ProyectosEvaluacionR", method = RequestMethod.GET) // Pagina principal
    public String EvaluacionR(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {
            HttpSession session = request.getSession();
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Jurado jurado = juradoService.juradoPorIdPersona(usuario.getPersona().getId_persona());
            List<Proyecto> listaProyecto = new ArrayList();

            List<Evaluacion> listaEvaluacion = evaluacionService.juradoEvaluacion(jurado.getId_jurado());

            if (!listaEvaluacion.isEmpty()) {
                List<Long> proyectosEvaluados = new ArrayList<>();
                for (Evaluacion ev : listaEvaluacion) {
                    for (Proyecto p : ev.getProyectos()) {
                        proyectosEvaluados.add(p.getId_proyecto());
                    }
                }

                for (Proyecto proyecto : proyectoService.findByJuradoId(jurado.getId_jurado())) {
                    if (!proyectosEvaluados.contains(proyecto.getId_proyecto())) {
                        listaProyecto.add(proyecto);
                    }
                }
            } else {
                listaProyecto = proyectoService.findByJuradoId(jurado.getId_jurado());
            }

            model.addAttribute("proyectos", listaProyecto);

            model.addAttribute("edit", "true");
            return "evaluacion/gestionar-proyectoEvaluacion";
        } else {
            return "redirect:LoginR";
        }
    }

    // Boton para Editar Documentos
    // @RequestMapping(value = "/form-evaluacion/{id_proyecto}")
    // public String editar_proyecto(@PathVariable("id_proyecto") Long id_proyecto,
    // Model model, HttpSession session, HttpServletRequest request) {

    // if (request.getSession().getAttribute("usuario") != null) {
    // Evaluacion evaluacion = new Evaluacion();
    // Proyecto proyecto = proyectoService.findOne(id_proyecto);

    // model.addAttribute("proyecto", proyecto);
    // model.addAttribute("evaluacion", evaluacion);

    // Long[] idsCriterios = new Long[] {
    // 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L,
    // 18L, 19L, 20L,
    // 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L, 32L, 33L, 34L, 35L,
    // 36L, 37L, 38L, 39L, 40L, 41L, 42L
    // };

    // for (int i = 0; i < idsCriterios.length; i++) {
    // model.addAttribute("criterio" + (i + 1),
    // criterioService.findOne(idsCriterios[i]));
    // }

    // return "evaluacion/form-evaluacion";
    // }else{
    // return "redirect:LoginR";
    // }

    // }

    @RequestMapping(value = "/form-evaluacion/{id_proyecto}")
    public String form_evaluacion(@PathVariable(name = "id_proyecto") Long id_proyecto, Model model,
            HttpSession session,
            HttpServletRequest request) {

        if (request.getSession().getAttribute("usuario") != null) {
            Proyecto proyecto = proyectoService.findOne(id_proyecto);
            Evaluacion evaluacion = new Evaluacion();

            model.addAttribute("proyecto", proyecto);
            model.addAttribute("evaluacion", evaluacion);
            model.addAttribute("criterios", categoriaCriterioService
                    .obtenerCategoriaCriteriosPorTipoProyecto(proyecto.getTipoProyecto().getId_tipoProyecto()));

            if (proyecto.getTipoProyecto().getId_tipoProyecto() == 1) {

                return "evaluacion/form-evaluacion_copia";

            } else if (proyecto.getTipoProyecto().getId_tipoProyecto() == 4) {

                return "evaluacion/form-evaluacion_escuela_tecnica";
            } else if (proyecto.getTipoProyecto().getId_tipoProyecto() == 5) {
                return "evaluacion/form-evaluacion_dicyt_emprende";
            } else if (proyecto.getTipoProyecto().getId_tipoProyecto() == 6) {
                return "evaluacion/form-evaluacion_festival_bandas";
            } else if (proyecto.getTipoProyecto().getId_tipoProyecto() == 7) {
                return "evaluacion/form-evaluacion_fexpo_acef";
            } else {
                return "evaluacion/form-evaluacion";
            }
        } else {
            return "redirect:/LoginR";
        }

    }

    // Boton para Guardar Documento
    @RequestMapping(value = "/GuardarEvaluacionF", method = RequestMethod.POST)
    public String GuardarEvaluacionF(@Validated Evaluacion evaluacion, RedirectAttributes redirectAttrs,
            @RequestParam(value = "criterios", required = false) Long[] id_ponderacion,
            HttpServletRequest request, @RequestParam("proyectos") Long idProyecto) {
        if (request.getSession().getAttribute("usuario") != null) {
            if (id_ponderacion == null || id_ponderacion.length == 0) {
                // Manejar el caso donde no se seleccionaron checkboxes
                redirectAttrs.addFlashAttribute("mensaje", "No se seleccionaron criterios.");
                return "redirect:/ProyectosEvaluacionR?alert=false";
            }

            Proyecto proyecto = proyectoService.findOne(idProyecto);
            HttpSession session = request.getSession();
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Jurado jurado = juradoService.juradoPorIdPersona(usuario.getPersona().getId_persona());
            List<Jurado> listjurado = juradoService.findByProyectoId(idProyecto);
            List<Evaluacion> listEvaluacion = evaluacionService.findByProyectoId(idProyecto);
            int puntajeTotal = 0;
            int cantidadJurados = listjurado.size();

            if (evaluacionDao.validacionEvaluacionJurado(idProyecto, jurado.getId_jurado()).size() >= 1) {
                return "redirect:/ProyectosEvaluacionR?alert=false";
            }

            for (Long id : id_ponderacion) {
                Ponderacion ponderacion = ponderacionService.findOne(id);
                if (ponderacion != null) {
                    int pon = ponderacion.getNum_ponderacion();
                    puntajeTotal += pon;
                }
            }

            Set<Ponderacion> ponderaciones = new HashSet<>();
            if (id_ponderacion != null) {
                for (Long id : id_ponderacion) {
                    Ponderacion ponderacion = ponderacionService.findOne(id);
                    ponderaciones.add(ponderacion);
                }
            }

            evaluacion.setEstado("A");
            evaluacion.setJurado(jurado);
            evaluacion.getProyectos().add(proyecto);
            evaluacion.setPonderaciones(ponderaciones);
            evaluacion.setPuntaje_total(puntajeTotal);
            evaluacionService.save(evaluacion);

            double promedioActual = proyecto.getPromedio_final()
                    + (evaluacion.getPuntaje_total() / (double) cantidadJurados);
            if (promedioActual > 100.0) {
                promedioActual = 100.0;
            }

            // DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            // symbols.setDecimalSeparator('.');
            // DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);
            // promedioActual = Double.parseDouble(decimalFormat.format(promedioActual));

            proyecto.setPromedio_final(promedioActual);
            proyecto.setEstado("A");
            proyecto.setDocente(proyecto.getDocente());
            proyecto.setEstudiante(proyecto.getEstudiante());
            proyecto.setEvaluacion(proyecto.getEvaluacion());
            proyecto.setJurado(proyecto.getJurado());
            proyecto.setPrograma(proyecto.getPrograma());
            proyecto.setNombre_proyecto(proyecto.getNombre_proyecto());

            proyectoService.save(proyecto);

            if (listjurado.size() == listEvaluacion.size() + 1) {
                proyecto.setEstado("E");
                proyectoService.save(proyecto);
            }

            redirectAttrs.addFlashAttribute("mensaje", "Proyecto Evaluado Correctamente");
            return "redirect:/ProyectosEvaluacionR?alert=true";
        } else {
            return "redirect:LoginR";
        }
    }

    @RequestMapping(value = "/GuardarEvaluacionE", method = RequestMethod.POST)
    public String GuardarEvaluacionE(@Validated Evaluacion evaluacion, RedirectAttributes redirectAttrs,
            @RequestParam(value = "criterios", required = false) Long[] values,
            @RequestParam(value = "id_ponderaciones", required = false) Long[] id_ponderaciones,
            HttpServletRequest request, @RequestParam("proyectos") Long idProyecto) {
        if (request.getSession().getAttribute("usuario") != null) {
            if (values == null || values.length == 0) {
                // Manejar el caso donde no se seleccionaron checkboxes
                redirectAttrs.addFlashAttribute("mensaje", "No se seleccionaron criterios.");
                return "redirect:/ProyectosEvaluacionR?alert=false";
            }

            Proyecto proyecto = proyectoService.findOne(idProyecto);
            HttpSession session = request.getSession();
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Jurado jurado = juradoService.juradoPorIdPersona(usuario.getPersona().getId_persona());
            List<Jurado> listjurado = juradoService.findByProyectoId(idProyecto);
            List<Evaluacion> listEvaluacion = evaluacionService.findByProyectoId(idProyecto);
            int puntajeTotal = 0;
            int cantidadJurados = listjurado.size();

            if (evaluacionDao.validacionEvaluacionJurado(idProyecto, jurado.getId_jurado()).size() >= 1) {
                return "redirect:/ProyectosEvaluacionR?alert=false";
            }

            // Set<Ponderacion> ponderaciones = new HashSet<>();
            // if (id_ponderacion != null) {
            // for (Long id : id_ponderacion) {
            // Ponderacion ponderacion = ponderacionService.findOne(id);
            // ponderaciones.add(ponderacion);
            // }
            // }

            for (Long id : values) {
                int pon = id.intValue();
                puntajeTotal += pon;

            }

            evaluacion.setEstado("A");
            evaluacion.setJurado(jurado);
            evaluacion.getProyectos().add(proyecto);
            // evaluacion.setPonderaciones(ponderaciones);
            evaluacion.setPuntaje_total(puntajeTotal);
            evaluacionService.save(evaluacion);

            if (values != null && id_ponderaciones != null && values.length == id_ponderaciones.length) {
                for (int i = 0; i < values.length; i++) {
                    Long value = values[i];
                    Long idPonderacion = id_ponderaciones[i];

                    Puntaje puntaje = new Puntaje();
                    puntaje.setEvaluaciones(evaluacion);
                    puntaje.setPonderacion(ponderacionService.findOne(idPonderacion));
                    puntaje.setValor(value.intValue());
                    puntajeService.save(puntaje);
                }
            }

            double promedioActual = proyecto.getPromedio_final()
                    + (evaluacion.getPuntaje_total() / (double) cantidadJurados);
            if (promedioActual > 100.0) {
                promedioActual = 100.0;
            }

            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            symbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);
            promedioActual = Double.parseDouble(decimalFormat.format(promedioActual));
            proyecto.setPromedio_final(promedioActual);
            proyecto.setEstado("A");
            proyecto.setDocente(proyecto.getDocente());
            proyecto.setEstudiante(proyecto.getEstudiante());
            proyecto.setEvaluacion(proyecto.getEvaluacion());
            proyecto.setJurado(proyecto.getJurado());
            proyecto.setPrograma(proyecto.getPrograma());
            proyecto.setNombre_proyecto(proyecto.getNombre_proyecto());

            proyectoService.save(proyecto);

            if (listjurado.size() == listEvaluacion.size() + 1) {
                proyecto.setEstado("E");
                proyectoService.save(proyecto);
            }

            redirectAttrs.addFlashAttribute("mensaje", "Proyecto Evaluado Correctamente");
            return "redirect:/ProyectosEvaluacionR?alert=true";
        } else {
            return "redirect:LoginR";
        }
    }

    @RequestMapping(value = "/GuardarEvaluacionBanda", method = RequestMethod.POST)
    public String GuardarEvaluacionBanda(@Validated Evaluacion evaluacion, RedirectAttributes redirectAttrs,
            @RequestParam(value = "criterios", required = false) Long[] values,
            @RequestParam(value = "id_ponderaciones", required = false) Long[] id_ponderaciones,
            HttpServletRequest request, @RequestParam("proyectos") Long idProyecto) {
        if (request.getSession().getAttribute("usuario") != null) {
            Proyecto proyecto = proyectoService.findOne(idProyecto);
            HttpSession session = request.getSession();
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Jurado jurado = juradoService.juradoPorIdPersona(usuario.getPersona().getId_persona());
            List<Jurado> listjurado = juradoService.findByProyectoId(idProyecto);
            List<Evaluacion> listEvaluacion = evaluacionService.findByProyectoId(idProyecto);
            int puntajeTotal = 0;
            int cantidadJurados = listjurado.size();

            if (evaluacionDao.validacionEvaluacionJurado(idProyecto, jurado.getId_jurado()).size() >= 1) {
                return "redirect:/ProyectosEvaluacionR?alert=false";
            }

            // Set<Ponderacion> ponderaciones = new HashSet<>();
            // if (id_ponderacion != null) {
            // for (Long id : id_ponderacion) {
            // Ponderacion ponderacion = ponderacionService.findOne(id);
            // ponderaciones.add(ponderacion);
            // }
            // }
            // for (Long id : id_ponderaciones) {
            // Puntaje puntaje =
            // puntajeService.puntajePonderacionJuradoProyecto+(jurado.getId_jurado(), id,
            // proyecto.getId_proyecto());
            // puntajeTotal += puntaje.getValor();
            // }

            evaluacion.setEstado("A");
            evaluacion.setJurado(jurado);
            evaluacion.getProyectos().add(proyecto);
            // evaluacion.setPonderaciones(id_ponderaciones);
            // evaluacion.setPuntaje_total(puntajeTotal);
            evaluacionService.save(evaluacion);

            for (Long id : id_ponderaciones) {
                Puntaje puntaje = puntajeService.puntajePonderacionJuradoProyecto(jurado.getId_jurado(), id,
                        proyecto.getId_proyecto());
                puntaje.setEvaluaciones(evaluacion);
                puntajeService.save(puntaje);
            }

            double promedioActual = proyecto.getPromedio_final()
                    + (evaluacion.getPuntaje_total() / (double) cantidadJurados);
            if (promedioActual > 100.0) {
                promedioActual = 100.0;
            }

            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            symbols.setDecimalSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);
            promedioActual = Double.parseDouble(decimalFormat.format(promedioActual));
            proyecto.setPromedio_final(promedioActual);
            proyecto.setEstado("A");
            proyecto.setDocente(proyecto.getDocente());
            proyecto.setEstudiante(proyecto.getEstudiante());
            proyecto.setEvaluacion(proyecto.getEvaluacion());
            proyecto.setJurado(proyecto.getJurado());
            proyecto.setPrograma(proyecto.getPrograma());
            proyecto.setNombre_proyecto(proyecto.getNombre_proyecto());

            proyectoService.save(proyecto);

            if (listjurado.size() == listEvaluacion.size() + 1) {
                proyecto.setEstado("E");
                proyectoService.save(proyecto);
            }
            // for (Long id : id_ponderaciones) {
            // Puntaje puntaje =
            // puntajeService.puntajePonderacionEvaluacionJurado(jurado.getId_jurado(),
            // evaluacion.getId_evaluacion() ,id);
            // puntaje.setProyecto(proyecto);
            // puntajeService.save(puntaje);
            // }

            redirectAttrs.addFlashAttribute("mensaje", "Proyecto Evaluado Correctamente");
            return "redirect:/ProyectosEvaluacionR?alert=true";
        } else {
            return "redirect:LoginR";
        }

    }

    @PostMapping(value = "/GuardarPuntaje/{ponderacion}/{valor}/{proyecto}")
    public ResponseEntity<String> guardaPuntaje(HttpServletRequest request,
            @PathVariable(value = "ponderacion") Long id_ponderacion,
            @PathVariable(value = "valor") Integer calificacion,
            @PathVariable(value = "proyecto") Long idProyecto) {

        HttpSession session = request.getSession();
        Usuario us = (Usuario) session.getAttribute("usuario");
        Usuario user = usuarioService.findOne(us.getId_usuario());
        Ponderacion ponderacion = ponderacionService.findOne(id_ponderacion);
        Jurado jurado = juradoService.juradoPorIdPersona(user.getPersona().getId_persona());
        Puntaje puntaje = puntajeService.puntajePonderacionJuradoProyecto(jurado.getId_jurado(),
                ponderacion.getId_ponderacion(), idProyecto);
        Proyecto proyecto = proyectoService.findOne(idProyecto);
        if (puntaje == null) {
            puntaje = new Puntaje();
            puntaje.setJurado(jurado);
            puntaje.setProyecto(proyecto);
            puntaje.setPonderacion(ponderacion);
        }
        puntaje.setValor(calificacion);
        puntajeService.save(puntaje);

        // System.out.println("guardado");
        
        return ResponseEntity.ok("Guardado");
    }

}
