package com.example.proyecto.Controllers.EvaluacionControllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Service.ICriterioService;
import com.example.proyecto.Models.Service.IEvaluacionService;
import com.example.proyecto.Models.Service.IProyectoService;

@Controller
public class EvaluacionController {
    
    @Autowired
	private IEvaluacionService evaluacionService;

    @Autowired
	private ICriterioService criterioService;

    @Autowired
	private IProyectoService proyectoService;


        // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
	@RequestMapping(value = "/ProyectosEvaluacionR", method = RequestMethod.GET) // Pagina principal
	public String EvaluacionR(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

			
			model.addAttribute("proyectos", proyectoService.findAll());
    

			return "evaluacion/gestionar-proyectoEvaluacion";
		} else {
			return "redirect:LoginR";
		}
	}

      // Boton para Editar Documentos
    @RequestMapping(value = "/form-evaluacion/{id_proyecto}")
    public String editar_proyecto(@PathVariable("id_proyecto") Long id_proyecto, Model model) {
       
        Proyecto proyecto = proyectoService.findOne(id_proyecto);

        model.addAttribute("proyecto", proyecto);
        model.addAttribute("criterios", criterioService.findAll());

        return "evaluacion/form-evaluacion";

    }



}
