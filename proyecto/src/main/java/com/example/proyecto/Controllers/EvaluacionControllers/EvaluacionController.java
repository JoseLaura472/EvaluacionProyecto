package com.example.proyecto.Controllers.EvaluacionControllers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Dao.IEvaluacionDao;
import com.example.proyecto.Models.Dao.IJuradoDao;
import com.example.proyecto.Models.Entity.CategoriaCriterio;
import com.example.proyecto.Models.Entity.Criterio;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.Entity.Pregunta;
import com.example.proyecto.Models.Service.ICategoriaCriterioService;
import com.example.proyecto.Models.Service.ICriterioService;
import com.example.proyecto.Models.Service.IEvaluacionService;
import com.example.proyecto.Models.Service.IJuradoService;
import com.example.proyecto.Models.Service.IPonderacionService;
import com.example.proyecto.Models.Service.IProyectoService;

@Controller
public class EvaluacionController {

    @Autowired
    private IEvaluacionService evaluacionService;

    @Autowired
    private ICriterioService criterioService;

    @Autowired
    private IProyectoService proyectoService;

    @Autowired
    private IJuradoService juradoService;

    @Autowired
    private IEvaluacionDao evaluacionDao;

    @Autowired
    private ICategoriaCriterioService categoriaCriterioService;

    @Autowired
    private IPonderacionService ponderacionService;
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
    // public String editar_proyecto(@PathVariable("id_proyecto") Long id_proyecto, Model model, HttpSession session, HttpServletRequest request) {

    //     if (request.getSession().getAttribute("usuario") != null) {
    //         Evaluacion evaluacion = new Evaluacion();
    //     Proyecto proyecto = proyectoService.findOne(id_proyecto);
        
    //     model.addAttribute("proyecto", proyecto);
    //     model.addAttribute("evaluacion", evaluacion);

    
    //     Long[] idsCriterios = new Long[] {
    //         1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L,
    //         21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L, 39L, 40L, 41L, 42L
    //     };
    
    //     for (int i = 0; i < idsCriterios.length; i++) {
    //         model.addAttribute("criterio" + (i + 1), criterioService.findOne(idsCriterios[i]));
    //     }
    
    //     return "evaluacion/form-evaluacion";
    //     }else{
    //         return "redirect:LoginR";
    //     }
        
    // }

    @RequestMapping(value = "/form-evaluacion/{id_proyecto}")
    public String form_evaluacion(@PathVariable(name = "id_proyecto") Long id_proyecto, Model model, HttpSession session,
            HttpServletRequest request) {

        if (request.getSession().getAttribute("usuario") != null) {
            Proyecto proyecto = proyectoService.findOne(id_proyecto);
            Evaluacion evaluacion = new Evaluacion();

            model.addAttribute("proyecto", proyecto);
            model.addAttribute("evaluacion", evaluacion);
            model.addAttribute("criterios", categoriaCriterioService.obtenerCategoriaCriteriosPorTipoProyecto(proyecto.getTipoProyecto().getId_tipoProyecto()));

            if (proyecto.getTipoProyecto().getId_tipoProyecto() == 1) {

                return "evaluacion/form-evaluacion_copia";
                
            }else if(proyecto.getTipoProyecto().getId_tipoProyecto() == 4){
               
                return "evaluacion/form-evaluacion_escuela_tecnica";
            }else{
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
                int pon = ponderacion.getPonderacion();
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
    }

    @RequestMapping(value = "/GuardarEvaluacionE", method = RequestMethod.POST)
    public String GuardarEvaluacionE(@Validated Evaluacion evaluacion, RedirectAttributes redirectAttrs,
            @RequestParam(value = "criterios", required = false) Long[] id_ponderacion,
            HttpServletRequest request, @RequestParam("proyectos") Long idProyecto) {

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
                int pon = ponderacion.getPonderacion();
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
    }

}
