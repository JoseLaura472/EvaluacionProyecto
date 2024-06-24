package com.example.proyecto.Controllers.CriterioControllers;

import jakarta.servlet.http.HttpServletRequest;

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
import com.example.proyecto.Models.Service.ICategoriaCriterioService;
import com.example.proyecto.Models.Service.IPonderacionService;
import com.example.proyecto.Models.Service.IPreguntaService;

@Controller
public class CriterioController {


    @Autowired
    private ICategoriaCriterioService categoriaCriterioService;

    @Autowired
    private IPonderacionService ponderacionService;

    @Autowired
    private IPreguntaService preguntaService;

    // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
    @RequestMapping(value = "/CriterioR", method = RequestMethod.GET) // Pagina principal
    public String CriterioR(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {

            // model.addAttribute("criterio", new Criterio());
            // model.addAttribute("criterios", criterioService.findAll());
            model.addAttribute("categoriaC", categoriaCriterioService.findAll());
            model.addAttribute("ponderacioneslist", ponderacionService.findAll());
            model.addAttribute("preguntaslist", preguntaService.findAll());

            return "criterio/gestionar-criterio";
        } else {
            return "redirect:LoginR";
        }
    }

    // // Boton para Guardar Documento
    // @RequestMapping(value = "/CriterioF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    // public String CriterioF(@Validated Criterio criterio, RedirectAttributes redirectAttrs) { // validar los datos
    //                                                                                           // capturados (1)

    //     criterio.setEstado("A");
    //     criterioService.save(criterio);
    //     redirectAttrs
    //             .addFlashAttribute("mensaje", "Registro Exitoso del Documento")
    //             .addFlashAttribute("clase", "success alert-dismissible fade show");

    //     return "redirect:/CriterioR";
    // }

    // Boton para Editar Documentos
    @RequestMapping(value = "/editar-criterio/{id_criterio}")
    public String editar_criterio(@PathVariable("id_criterio") Long id_criterio, Model model) {
        // Criterio criterio = criterioService.findOne(id_criterio);
    
      
        model.addAttribute("categoriaC", categoriaCriterioService.findAll());
        model.addAttribute("ponderacioneslist", ponderacionService.findAll());
        model.addAttribute("preguntaslist", preguntaService.findAll());
        model.addAttribute("edit", "true");
        return "criterio/gestionar-criterio";

    }

    // Boton para Guardar Documento
    // @RequestMapping(value = "/CriterioModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    // public String CriterioModF(@Validated Criterio criterio, RedirectAttributes redirectAttrs)
    //           { // validar los datos capturados (1)
      
    //     criterio.setEstado("A");
    //     criterioService.save(criterio);
    //     redirectAttrs
    //             .addFlashAttribute("mensaje2", "Datos del Documento Actualizados Correctamente")
    //             .addFlashAttribute("clase2", "success alert-dismissible fade show");

    //     return "redirect:/CriterioR";
    // }

    // @RequestMapping(value = "/eliminar-criterio/{id_criterio}")
	// public String eliminar_criterio(@PathVariable("id_criterio") Long id_criterio) {

    //     Criterio criterio = criterioService.findOne(id_criterio);


	// 	criterio.setEstado("X");

	// 	criterioService.save(criterio);
	// 	return "redirect:/CriterioR";

	// }

}
