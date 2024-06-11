package com.example.proyecto.Controllers.ReporteController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.proyecto.Models.Entity.Criterio;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Service.ICategoriaCriterioService;
import com.example.proyecto.Models.Service.IProyectoService;

@Controller
public class ReportesController {
    
    @Autowired
    private IProyectoService proyectoService;
    @Autowired
    private ICategoriaCriterioService categoriaCriterioService;

    @GetMapping("/FormReportes")
    public String formReportes(HttpServletRequest request, Model model){
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("proyectos", proyectoService.findAll());

        return "reportes/formReportes";
        }else{
            return "redirect:LoginR";
        }
    }

    @GetMapping("/ReporteProyecoctoOne")
    public String reporteProyecoctoOne(@RequestParam(value = "id_proyecto")Long id_proyecto, Model model){


        Proyecto proyecto = proyectoService.findOne(id_proyecto); 
        
        //List<Evaluacion> evaluaciones = proyecto.getEvaluacion();
        Set<Evaluacion> evaluacionesSet = proyecto.getEvaluacion();
        List<Evaluacion> evaluacionesList = new ArrayList<>(evaluacionesSet);
        evaluacionesList.sort(Comparator.comparing(evaluacion -> evaluacion.getId_evaluacion()));

        
        List<Criterio> c1 = new ArrayList<>();
        List<Criterio> c2 = new ArrayList<>();
        List<Criterio> c3 = new ArrayList<>();
        int contador = 0;
        for (Evaluacion e : evaluacionesList) {
            if (contador == 0) {
                for (Criterio c : e.getCriterios()) {
                    c1.add(c);
                }
            } else if (contador == 1) {
                for (Criterio c : e.getCriterios()) {
                    c2.add(c);
                }
            } else if (contador == 2) {
                for (Criterio c : e.getCriterios()) {
                    c3.add(c);
                }
            }
            contador++;
            System.out.println(e.getPuntaje_total());
        }
        c1.sort(Comparator.comparing(criterio -> criterio.getPreguntas().getId_pregunta()));
        c2.sort(Comparator.comparing(criterio -> criterio.getPreguntas().getId_pregunta()));
        c3.sort(Comparator.comparing(criterio -> criterio.getPreguntas().getId_pregunta()));

        model.addAttribute("criterio1", c1);
        model.addAttribute("criterio2", c2);
        model.addAttribute("criterio3", c3);

        model.addAttribute("ev", evaluacionesList);
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("cat", categoriaCriterioService.findAll());
        if (evaluacionesList.size() == 2) {
        return "reportes/print2";   
        }
        return "reportes/print";
    }
}
