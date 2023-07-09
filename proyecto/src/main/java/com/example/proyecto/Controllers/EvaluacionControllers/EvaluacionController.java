package com.example.proyecto.Controllers.EvaluacionControllers;

import java.util.HashSet;
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

			
			model.addAttribute("proyectos", proyectoService.findAll());
    

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
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("criterio1", criterioService.findOne(id_criterio1));
        model.addAttribute("criterio2", criterioService.findOne(id_criterio2));
        model.addAttribute("criterio3", criterioService.findOne(id_criterio3));
        model.addAttribute("criterio4", criterioService.findOne(id_criterio4));

        return "evaluacion/form-evaluacion";

    }

    // Boton para Guardar Documento
    @RequestMapping(value = "/GuardarEvaluacionF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    public String GuardarEvaluacionF(@Validated Proyecto proyecto,@Validated Evaluacion evaluacion, RedirectAttributes redirectAttrs,
             @RequestParam(value = "criterios") Long[] id_criterio, HttpServletRequest request,
             @RequestParam(value = "proyectos") Long[] id_proyecto)
              { // validar los datos capturados (1)
                HttpSession session = request.getSession();
                Usuario usuario = (Usuario) session.getAttribute("usuario");
                Jurado jurado = juradoService.juradoPorIdPersona(usuario.getPersona().getId_persona());
              
            evaluacion.setEstado("A");
            evaluacion.setJurado(jurado);
            evaluacionService.save(evaluacion);
      

      
        redirectAttrs
                .addFlashAttribute("mensaje2", "Datos del Documento Actualizados Correctamente")
                .addFlashAttribute("clase2", "success alert-dismissible fade show");

        return "redirect:/ProyectosEvaluacionR";
    }



}
