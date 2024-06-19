package com.example.proyecto.Controllers.PersonaControllers;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.validation.annotation.Validated;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Service.IPersonaService;
import com.example.proyecto.Models.Service.IUsuarioService;

@Controller
public class PersonaController {

 

    @Autowired
    private IPersonaService personaService;

    @Autowired
    private IUsuarioService usuarioService;

    // FUNCION PARA LISTAR LOS REGISTRO DE PERSONA
    @RequestMapping(value = "/PersonasL", method = RequestMethod.GET) // Pagina principal
    public String facultadL(Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute("usuario") != null) {
            model.addAttribute("personas", personaService.findAll());
            return "persona/gestionarPersona";
        } else {
            return "redirect:LoginR";
        }
    }

    // FUNCION PARA LA VISUALIZACION DEL REGISTRO DE PERSONA
    @RequestMapping(value = "/PersonaR", method = RequestMethod.GET) // Pagina principal
    public String Persona(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("persona", new Persona());
            model.addAttribute("usuario", new Usuario());
            model.addAttribute("personas", personaService.findAll());
  

            return "persona/formPersona";
        } else {
            return "redirect:LoginR";
        }
    }

    @RequestMapping(value = "/PersonaF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    public String PersonaF(@Validated Persona persona, Model model) {

        persona.setEstado("A");

        personaService.save(persona);

        return "redirect:/PersonasL";
    }

    // FUNCION PARA EDITAR EL REGISTRO DE PERSONA
    @RequestMapping(value = "/editar-persona/{id_persona}")
    public String editar_p(@PathVariable("id_persona") Long id_persona, Model model) {

        Persona persona = personaService.findOne(id_persona);

        model.addAttribute("persona", persona);
        model.addAttribute("personas", personaService.findAll());

        model.addAttribute("edit", "true");

        return "persona/formPersona";

    }

    @RequestMapping(value = "/PersonaModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    public String PersonaMod(@Validated Persona persona, Model model) {

        persona.setEstado("A");
        personaService.save(persona);

        return "redirect:/PersonasL";
    }

    // FUNCION PARA ELIMINAR EL REGISTRO DE PERSONA
    @RequestMapping(value = "/eliminar-persona/{id_persona}")
    public String eliminar_p(@PathVariable("id_persona") Long id_persona) {

        Persona persona = personaService.findOne(id_persona);

        persona.setEstado("X");

        personaService.save(persona);
        return "redirect:/PersonasL";

    }
}
