package com.example.proyecto.Controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Entity.Usuario;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IProyectoService;
import com.example.proyecto.Models.IService.ITipoProyectoService;
import com.example.proyecto.Models.IService.IUsuarioService;
import com.example.proyecto.Models.Service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class loginController {

    private final IUsuarioService usuarioService;
    private final IProyectoService proyectoService;
    private final ITipoProyectoService tipoProyectoService;
    private final AuthService authService;
    private final IJuradoAsignacionService juradoAsignacionService;

    // Funcion de visualizacion de iniciar sesiòn administrador
    @RequestMapping(value = "/LoginR", method = RequestMethod.GET)
    public String LoginR(Model model) {

        return "index";
    }
    @RequestMapping(value = "/Login", method = RequestMethod.GET)
    public String LoginRr(Model model) {

        return "index2";
    }

    // Funciòn de iniciar sesiòn administrador
    @PostMapping("/LogearseF")
    public String logearseF(@RequestParam("usuario") String user,
                            @RequestParam("contrasena") String contrasena,
                            HttpServletRequest request,
                            RedirectAttributes flash) {

        Usuario usuario = authService.autenticar(user, contrasena);
        if (usuario == null) {
            flash.addFlashAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/LoginR";
        }

        // Normaliza estado
        String estado = (usuario.getEstado() == null) ? "" : usuario.getEstado().trim().toUpperCase();

        // Cuenta bloqueada
        if ("X".equals(estado)) {
            HttpSession s = request.getSession(false);
            if (s != null) s.invalidate();
            flash.addFlashAttribute("warn", "Tu cuenta está bloqueada.");
            return "redirect:/cerrar_sesionAdm";
        }

        // Asegura persona cargada
        if (usuario.getPersona() == null || usuario.getPersona().getIdPersona() == null) {
            usuario = usuarioService.findByUsuario(usuario.getUsuario()).orElse(usuario);
        }

        // === MUY IMPORTANTE: reinicia sesión para no arrastrar flags previos ===
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        HttpSession session = request.getSession(true);

        // Guarda datos mínimos en sesión
        session.setAttribute("usuario", usuario);
        session.setAttribute("persona", usuario.getPersona());

        flash.addFlashAttribute("success",
                (usuario.getPersona() != null ? usuario.getPersona().getNombres() : usuario.getUsuario()));

        // === Resolución de “roles” por estado (ajusta si además usas tabla de roles) ===
        boolean esAdmin  = "A".equalsIgnoreCase(estado) /* || usuario.tieneRol("ADMINISTRADOR") */;
        boolean esJurado = "J".equalsIgnoreCase(estado) /* || usuario.tieneRol("JURADO") */;

        Long idPersona = (usuario.getPersona() != null) ? usuario.getPersona().getIdPersona() : null;

        // ADMIN tiene prioridad SIEMPRE
        if (esAdmin) {
            // Limpia cualquier bandera de jurado por si acaso
            session.removeAttribute("juradoCategoria");
            System.out.println("[logearseF] usuario admin, redirigiendo a /admin/inicio");
            return "redirect:/admin/inicio";
        }

        if (esJurado) {
            boolean tieneCategoria = false;
            if (idPersona != null) {
                try {
                    tieneCategoria = juradoAsignacionService.existsCategoriaAsignadaByPersona(idPersona);
                } catch (Exception ignore) {}
            }
            session.setAttribute("juradoCategoria", tieneCategoria);
            return tieneCategoria ? "redirect:/jurado/panel-categoria" : "redirect:/jurado/panel";
        }

        // Fallback para otros estados/tipos de usuario
        session.removeAttribute("juradoCategoria");
        return "redirect:/inicio";
    }


    @GetMapping("/admin/inicio")
    public String adminInicio(HttpServletRequest request, Model model) {
        // pequeña protección: si no hay usuario o no es A, enviar fuera
        HttpSession session = request.getSession(false);
        if (session == null) return "redirect:/LoginR";
        Usuario u = (Usuario) session.getAttribute("usuario");
        if (u == null || !"A".equalsIgnoreCase(u.getEstado())) return "redirect:/LoginR";

        return "vista-admin";
    }

    // Funcion de visualizaciòn de la pagina principal
    @RequestMapping(value = "/AdmPG", method = RequestMethod.GET) // Pagina principal
	public String Inicio(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

            HttpSession sessionAdministrador = request.getSession();
            Usuario usuario = (Usuario) sessionAdministrador.getAttribute("usuario");
            
            List<Proyecto> PrimerL = proyectoService.Primerlugar();
            List<Proyecto> SegundoL = proyectoService.Segundolugar();
            List<Proyecto> TercerL = proyectoService.Tercerlugar();
            // List<Proyecto> Ranking = proyectoService.proyectosRanking();

            model.addAttribute("tiposProyectos", tipoProyectoService.findAll());
            model.addAttribute("PrimerL", PrimerL);
			model.addAttribute("SegundoL", SegundoL);
            model.addAttribute("TercerL", TercerL);
            // model.addAttribute("Ranking", Ranking);

            model.addAttribute("usuario", usuario);
			return "Inicio";
		} else {
			return "redirect:LoginR";
		}
	}

    @PostMapping("/tabla-ranking-tipo-proyecto/{idTp}")
	public String cargarTablaRegistrosEstudiante(Model model, @PathVariable Long idTp) throws Exception {
		// List<Estudiante> listaEstudiantes = iEstudianteService.findAll();
		List<Proyecto> listaProyectos = proyectoService.RankingPorTipoProyecto(idTp);

		model.addAttribute("listaProyectos", listaProyectos);

		return "proyecto/tabla-registro-proyecto-tp";
	}

    @RequestMapping(value = "/Tecnologia", method = RequestMethod.GET) // Pagina principal
	public String InicioA(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

            HttpSession sessionAdministrador = request.getSession();
            Usuario usuario = (Usuario) sessionAdministrador.getAttribute("usuario");
            
            List<Proyecto> Ranking = proyectoService.proyectosRankingTecnologia();

            model.addAttribute("RankingT", Ranking);
            model.addAttribute("usuario", usuario);

			return "ranking/ranking-tecnologia";
		} else {
			return "redirect:LoginR";
		}
	}

    @RequestMapping(value = "/Empredimiento", method = RequestMethod.GET) // Pagina principal
	public String InicioB(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

            HttpSession sessionAdministrador = request.getSession();
            Usuario usuario = (Usuario) sessionAdministrador.getAttribute("usuario");
            
            List<Proyecto> Ranking = proyectoService.proyectosRankingEmprendimiento();

            model.addAttribute("RankingE", Ranking);
            model.addAttribute("usuario", usuario);

			return "ranking/ranking-emprendimiento";
		} else {
			return "redirect:LoginR";
		}
	}

    @RequestMapping(value = "/Salud", method = RequestMethod.GET) // Pagina principal
	public String InicioC(HttpServletRequest request, Model model) {
		if (request.getSession().getAttribute("usuario") != null) {

            HttpSession sessionAdministrador = request.getSession();
            Usuario usuario = (Usuario) sessionAdministrador.getAttribute("usuario");
            
            List<Proyecto> Ranking = proyectoService.proyectosRankingSalud();

            model.addAttribute("RankingS", Ranking);
            model.addAttribute("usuario", usuario);

			return "ranking/ranking-salud";
		} else {
			return "redirect:LoginR";
		}
	}

    // Funcion de cerrar sesion de administrador
    @RequestMapping("/cerrar_sesionAdm")
    public String cerrarSesion2(HttpServletRequest request, RedirectAttributes flash) {
        HttpSession sessionAdministrador = request.getSession();
        if (sessionAdministrador != null) {
            sessionAdministrador.invalidate();
            flash.addAttribute("validado", "Se cerro sesion con exito!");
        }
        return "redirect:/LoginR";
    }

}
