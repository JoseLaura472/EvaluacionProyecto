package com.example.proyecto.Controllers.EvaluacionControllers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.example.proyecto.Models.Entity.Criterio;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Service.ICriterioService;
import com.example.proyecto.Models.Service.IEvaluacionService;
import com.example.proyecto.Models.Service.IJuradoService;
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
    @RequestMapping(value = "/form-evaluacion/{id_proyecto}")
    public String editar_proyecto(@PathVariable("id_proyecto") Long id_proyecto, Model model) {
        Evaluacion evaluacion = new Evaluacion();
        Proyecto proyecto = proyectoService.findOne(id_proyecto);
        Long id_criterio1 = (long) 1;
        Long id_criterio2 = (long) 2;
        Long id_criterio3 = (long) 3;
        Long id_criterio4 = (long) 4;
        Long id_criterio5 = (long) 5;
        Long id_criterio6 = (long) 6;
        Long id_criterio7 = (long) 7;
        Long id_criterio8 = (long) 8;
        Long id_criterio9 = (long) 9;
        Long id_criterio10 = (long) 10;
        Long id_criterio11 = (long) 11;
        Long id_criterio12 = (long) 12;
        Long id_criterio13 = (long) 13;
        Long id_criterio14 = (long) 14;
        Long id_criterio15 = (long) 15;
        Long id_criterio16 = (long) 16;
        Long id_criterio17 = (long) 17;
        Long id_criterio18 = (long) 18;
        Long id_criterio19 = (long) 19;
        Long id_criterio20 = (long) 20;
        Long id_criterio21 = (long) 21;
        Long id_criterio22 = (long) 22;
        Long id_criterio23 = (long) 23;
        Long id_criterio24 = (long) 24;
        Long id_criterio25 = (long) 25;
        Long id_criterio26 = (long) 26;
        Long id_criterio27 = (long) 27;
        Long id_criterio28 = (long) 28;
        Long id_criterio29 = (long) 29;
        Long id_criterio30 = (long) 30;
        Long id_criterio31 = (long) 31;
        Long id_criterio32 = (long) 32;
        Long id_criterio33 = (long) 33;
        Long id_criterio34 = (long) 34;
        Long id_criterio35 = (long) 35;
        Long id_criterio36 = (long) 36;
        Long id_criterio37 = (long) 37;
        Long id_criterio38 = (long) 38;
        Long id_criterio39 = (long) 39;
        Long id_criterio40 = (long) 40;
        Long id_criterio41 = (long) 41;
        Long id_criterio42 = (long) 42;
        
        
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("evaluacion", evaluacion);

        model.addAttribute("criterio1", criterioService.findOne(id_criterio1));
        model.addAttribute("criterio2", criterioService.findOne(id_criterio2));
        model.addAttribute("criterio3", criterioService.findOne(id_criterio3));
        model.addAttribute("criterio4", criterioService.findOne(id_criterio4));
        model.addAttribute("criterio5", criterioService.findOne(id_criterio5));
        model.addAttribute("criterio6", criterioService.findOne(id_criterio6));
        model.addAttribute("criterio7", criterioService.findOne(id_criterio7));
        model.addAttribute("criterio8", criterioService.findOne(id_criterio8));
        model.addAttribute("criterio9", criterioService.findOne(id_criterio9));
        model.addAttribute("criterio10", criterioService.findOne(id_criterio10));
        model.addAttribute("criterio11", criterioService.findOne(id_criterio11));
        model.addAttribute("criterio12", criterioService.findOne(id_criterio12));
        model.addAttribute("criterio13", criterioService.findOne(id_criterio13));
        model.addAttribute("criterio14", criterioService.findOne(id_criterio14));
        model.addAttribute("criterio15", criterioService.findOne(id_criterio15));
        model.addAttribute("criterio16", criterioService.findOne(id_criterio16));
        model.addAttribute("criterio17", criterioService.findOne(id_criterio17));
        model.addAttribute("criterio18", criterioService.findOne(id_criterio18));
        model.addAttribute("criterio19", criterioService.findOne(id_criterio19));
        model.addAttribute("criterio20", criterioService.findOne(id_criterio20));
        model.addAttribute("criterio21", criterioService.findOne(id_criterio21));
        model.addAttribute("criterio22", criterioService.findOne(id_criterio22));
        model.addAttribute("criterio23", criterioService.findOne(id_criterio23));
        model.addAttribute("criterio24", criterioService.findOne(id_criterio24));
        model.addAttribute("criterio25", criterioService.findOne(id_criterio25));
        model.addAttribute("criterio26", criterioService.findOne(id_criterio26));
        model.addAttribute("criterio27", criterioService.findOne(id_criterio27));
        model.addAttribute("criterio28", criterioService.findOne(id_criterio28));
        model.addAttribute("criterio29", criterioService.findOne(id_criterio29));
        model.addAttribute("criterio30", criterioService.findOne(id_criterio30));
        model.addAttribute("criterio31", criterioService.findOne(id_criterio31));
        model.addAttribute("criterio32", criterioService.findOne(id_criterio32));
        model.addAttribute("criterio33", criterioService.findOne(id_criterio33));
        model.addAttribute("criterio34", criterioService.findOne(id_criterio34));
        model.addAttribute("criterio35", criterioService.findOne(id_criterio35));
        model.addAttribute("criterio36", criterioService.findOne(id_criterio36));
        model.addAttribute("criterio37", criterioService.findOne(id_criterio37));
        model.addAttribute("criterio38", criterioService.findOne(id_criterio38));
        model.addAttribute("criterio39", criterioService.findOne(id_criterio39));
        model.addAttribute("criterio40", criterioService.findOne(id_criterio40));
        model.addAttribute("criterio41", criterioService.findOne(id_criterio41));
        model.addAttribute("criterio42", criterioService.findOne(id_criterio42));

        return "evaluacion/form-evaluacion";

    }

    // Boton para Guardar Documento
    @RequestMapping(value = "/GuardarEvaluacionF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    public String GuardarEvaluacionF(@Validated Evaluacion evaluacion, RedirectAttributes redirectAttrs,
            @RequestParam(value = "criterios") Long[] id_criterio, HttpServletRequest request,
            @RequestParam("proyectos") Long idProyecto) { // validar los datos capturados (1)
        Proyecto proyecto = proyectoService.findOne(idProyecto);
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Jurado jurado = juradoService.juradoPorIdPersona(usuario.getPersona().getId_persona());
        List<Jurado> listjurado = juradoService.findByProyectoId(idProyecto);
        List<Evaluacion> listEvaluacion = evaluacionService.findByProyectoId(idProyecto);
        int puntajeTotal = 0;
        int cantidadJurados = listjurado.size();

        for (Long id : id_criterio) {
            Criterio criterio = criterioService.findOne(id);
            int ponderacion = criterio.getPonderaciones().getPonderacion();
            puntajeTotal += ponderacion;

        }

        evaluacion.setEstado("A");
        evaluacion.setJurado(jurado);
        evaluacion.getProyectos().add(proyecto);
        evaluacion.setPuntaje_total(puntajeTotal);
        evaluacionService.save(evaluacion);

        double promedioActual = proyecto.getPromedio_final()
                + (evaluacion.getPuntaje_total() / (double) cantidadJurados);
        double parteDecimal = (promedioActual - Math.floor(promedioActual)) * 100; // Multiplicamos por 100 para
                                                                                   // trabajar con los dos decimales

       

        if (promedioActual > 100.0) {
            promedioActual = 100.0;
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#0.000", symbols);
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
        
        if (listjurado.size() == listEvaluacion.size()+1) {
        proyecto.setEstado("E"); 
        proyectoService.save(proyecto);
        }
        redirectAttrs.addFlashAttribute("mensaje", "Proyecto Evaluado Correctamente");
                

        return "redirect:/ProyectosEvaluacionR?alert=true";
    }

}
