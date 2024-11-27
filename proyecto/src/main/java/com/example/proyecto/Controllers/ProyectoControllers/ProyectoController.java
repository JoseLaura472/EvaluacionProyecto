package com.example.proyecto.Controllers.ProyectoControllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.proyecto.Models.Entity.ArchivoAdjunto;
import com.example.proyecto.Models.Entity.Estudiante;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Service.IArchivoAdjuntoService;
import com.example.proyecto.Models.Service.ICategoriaProyectoService;
import com.example.proyecto.Models.Service.IDocenteService;
import com.example.proyecto.Models.Service.IEstudianteService;
import com.example.proyecto.Models.Service.IJuradoService;
import com.example.proyecto.Models.Service.IProgramaService;
import com.example.proyecto.Models.Service.IProyectoService;
import com.example.proyecto.Models.Service.IPuntajeService;
import com.example.proyecto.Models.Service.ITipoProyectoService;
import com.example.proyecto.Models.Utils.AdjuntarArchivo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ProyectoController {

    @Autowired
    private IEstudianteService estudianteService;

    @Autowired
    private IDocenteService docenteService;

    @Autowired
    private IProyectoService proyectoService;

    @Autowired
    private IProgramaService programaService;

    @Autowired
    private IJuradoService juradoService;

    @Autowired
    private IArchivoAdjuntoService archivoAdjuntoService;

    @Autowired
    private ITipoProyectoService tipoProyectoService;

    @Autowired
    private ICategoriaProyectoService categoriaProyectoService;

    @Autowired
    private IPuntajeService puntajeService;

    // FUNCION PARA LA VISUALIZACION DE REGISTRO DE MNACIONALIDAD
    @RequestMapping(value = "/ProyectoR", method = RequestMethod.GET) // Pagina principal
    public String ProyectoR(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("tiposProyectos", tipoProyectoService.findAll());

            return "proyecto/gestionar-proyectos";
        } else {
            return "redirect:/LoginR";
        }
    }

    @GetMapping("/ProyectoForm/{id_tipoProyecto}")
    public String ProyectoForm(Model model, @PathVariable(name = "id_tipoProyecto") Long id_tipoProyecto) {

        // Definir un array con los nombres de los fragmentos
        String[] fragments = { "card_body1", "card_body2", "card_body3", "card_body4", "card_body5", "card_body6",
                "card_body7", "card_body8", "card_body9", "card_body10", "card_body11" };

        // Verificar si el id_tipoProyecto es válido
        if (id_tipoProyecto >= 1 && id_tipoProyecto <= fragments.length) {
            // Devolver el fragmento correspondiente
            model.addAttribute("categorias", categoriaProyectoService.getCategoriasPorTipoProyecto(id_tipoProyecto));
            model.addAttribute("proyecto", new Proyecto());
            model.addAttribute("jurados", juradoService.findAll());
            model.addAttribute("docentes", docenteService.findAll());
            model.addAttribute("estudiantes", estudianteService.findAll());
            model.addAttribute("carreras", programaService.findAll());
            model.addAttribute("id_tipoProyecto", id_tipoProyecto);
            return "Content/form_proyecto :: " + fragments[id_tipoProyecto.intValue() - 1];
        } else {
            // Manejar el caso en el que el id_tipoProyecto no es válido
            return "Content/form_proyecto :: default_fragment";
        }
    }

    @GetMapping("/lista_proyectos/{id_tipoProyecto}")
    public String lista_proyectos(@PathVariable(name = "id_tipoProyecto") Long id_tipoProyecto, Model model) {
        // Definir un array con los nombres de los fragmentos
        String[] fragments = { "table1", "table2", "table3", "table4", "table5", "table6", "table7", "table8", "table9",
                "table10", "table11" };

        // Verificar si el id_tipoProyecto es válido
        if (id_tipoProyecto >= 1 && id_tipoProyecto <= fragments.length) {
            // Devolver el fragmento correspondiente

            model.addAttribute("proyectos", proyectoService.obtenerProyectosPorTipoProyecto(id_tipoProyecto).stream()
                    .sorted((p1, p2) -> p1.getCategoriaProyecto().getId_categoriaProyecto()
                            .compareTo(p2.getCategoriaProyecto().getId_categoriaProyecto()))
                    .collect(Collectors.toList()));

            return "Content/lista_proyecto :: " + fragments[id_tipoProyecto.intValue() - 1];
        } else {
            // Manejar el caso en el que el id_tipoProyecto no es válido
            return "Content/lista_proyecto :: default_fragment";
        }
    }

    // PARA FEXCOIN, DICYT EMPRENDE, ACYT, FEXPOACEF O SIMILARES
    @RequestMapping(value = "/ProyectoF", method = RequestMethod.POST)
    public ResponseEntity<String> ProyectoF(@Validated Proyecto proyecto, RedirectAttributes redirectAttrs,
            @RequestParam(name = "estudiante", required = false) Long[] id_estudiantes,
            @RequestParam(name = "docente", required = false) Long id_docente,
            @RequestParam(name = "categoriaProyecto", required = false) Long id_categoriaProyecto,
            @RequestParam(name = "id_tipoProyecto") Long id_tipoProyecto,
            @RequestParam(name = "jurado") Long[] id_jurados) throws FileNotFoundException, IOException {

        MultipartFile multipartFile = proyecto.getFile();
        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
        AdjuntarArchivo adjuntarArchivo = new AdjuntarArchivo();

        Path rootPath = Paths.get("archivos/");
        Path rootAbsolutPath = rootPath.toAbsolutePath();
        String rutaDirectorio = rootAbsolutPath.toString();

        String rutaArchivo = adjuntarArchivo.crearSacDirectorio(rutaDirectorio);
        List<ArchivoAdjunto> listArchivos = archivoAdjuntoService.listarArchivoAdjunto();
        Integer ad = adjuntarArchivo.adjuntarArchivoProyecto(proyecto, rutaArchivo);
        proyecto.setNombreArchivo((listArchivos.size() + 1) + ".pdf");

        ArchivoAdjunto archivoAdjunt = new ArchivoAdjunto();
        archivoAdjunt.setNombre_archivo(proyecto.getNombreArchivo());
        archivoAdjunt.setRuta_archivo(rutaArchivo);
        archivoAdjunt.setEstado("A");
        archivoAdjuntoService.registrarArchivoAdjunto(archivoAdjunt);
        proyecto.setArchivoAdjunto(archivoAdjunt);

        proyecto.setDocente(docenteService.findOne(id_docente));
        proyecto.setCategoriaProyecto(categoriaProyectoService.findOne(id_categoriaProyecto));
        proyecto.setTipoProyecto(tipoProyectoService.findOne(id_tipoProyecto));
        proyecto.setEstado("A");
        proyectoService.save(proyecto);
        redirectAttrs
                .addFlashAttribute("mensaje", "Registro Exitoso del Documento")
                .addFlashAttribute("clase", "success alert-dismissible fade show");

        return ResponseEntity.ok("1");
    }

    @RequestMapping(value = "/danza-entrada", method = RequestMethod.POST)
    public ResponseEntity<String> DanzaEntrada(@Validated Proyecto proyecto, RedirectAttributes redirectAttrs,
            @RequestParam(name = "categoriaProyecto") Long id_categoriaProyecto,
            @RequestParam(name = "id_tipoProyecto") Long id_tipoProyecto,
            @RequestParam(name = "jurado") Long[] id_jurados) throws FileNotFoundException, IOException {

        // MultipartFile multipartFile = proyecto.getFile();
        // ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
        // AdjuntarArchivo adjuntarArchivo = new AdjuntarArchivo();

        // Path rootPath = Paths.get("archivos/");
        // Path rootAbsolutPath = rootPath.toAbsolutePath();
        // String rutaDirectorio = rootAbsolutPath.toString();

        // String rutaArchivo = adjuntarArchivo.crearSacDirectorio(rutaDirectorio);
        // List<ArchivoAdjunto> listArchivos =
        // archivoAdjuntoService.listarArchivoAdjunto();
        // Integer ad = adjuntarArchivo.adjuntarArchivoProyecto(proyecto, rutaArchivo);
        // proyecto.setNombreArchivo((listArchivos.size() + 1) + ".pdf");

        // ArchivoAdjunto archivoAdjunt = new ArchivoAdjunto();
        // archivoAdjunt.setNombre_archivo(proyecto.getNombreArchivo());
        // archivoAdjunt.setRuta_archivo(rutaArchivo);
        // archivoAdjunt.setEstado("A");
        // archivoAdjuntoService.registrarArchivoAdjunto(archivoAdjunt);
        // proyecto.setArchivoAdjunto(archivoAdjunt);

        proyecto.setCategoriaProyecto(categoriaProyectoService.findOne(id_categoriaProyecto));
        proyecto.setTipoProyecto(tipoProyectoService.findOne(id_tipoProyecto));
        proyecto.setEstado("A");
        proyectoService.save(proyecto);
        redirectAttrs
                .addFlashAttribute("mensaje", "Registro Exitoso del Documento")
                .addFlashAttribute("clase", "success alert-dismissible fade show");

        return ResponseEntity.ok("1");
    }

    // JERU PUJI
    @RequestMapping(value = "/ProyectoFBanda", method = RequestMethod.POST)
    public ResponseEntity<String> ProyectoFBanda(@Validated Proyecto proyecto, RedirectAttributes redirectAttrs,
            @RequestParam(name = "categoriaProyecto") Long id_categoriaProyecto,
            @RequestParam(name = "id_tipoProyecto") Long id_tipoProyecto,
            @RequestParam(name = "jurado") Long[] id_jurados) throws FileNotFoundException, IOException {

        MultipartFile multipartFile = proyecto.getFile();
        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
        AdjuntarArchivo adjuntarArchivo = new AdjuntarArchivo();

        Path rootPath = Paths.get("archivos/");
        Path rootAbsolutPath = rootPath.toAbsolutePath();
        String rutaDirectorio = rootAbsolutPath.toString();

        String rutaArchivo = adjuntarArchivo.crearSacDirectorio(rutaDirectorio);
        List<ArchivoAdjunto> listArchivos = archivoAdjuntoService.listarArchivoAdjunto();
        Integer ad = adjuntarArchivo.adjuntarArchivoProyecto(proyecto, rutaArchivo);
        proyecto.setNombreArchivo((listArchivos.size() + 1) + ".pdf");

        ArchivoAdjunto archivoAdjunt = new ArchivoAdjunto();
        archivoAdjunt.setNombre_archivo(proyecto.getNombreArchivo());
        archivoAdjunt.setRuta_archivo(rutaArchivo);
        archivoAdjunt.setEstado("A");
        archivoAdjuntoService.registrarArchivoAdjunto(archivoAdjunt);
        proyecto.setArchivoAdjunto(archivoAdjunt);

        proyecto.setCategoriaProyecto(categoriaProyectoService.findOne(id_categoriaProyecto));
        proyecto.setTipoProyecto(tipoProyectoService.findOne(id_tipoProyecto));
        proyecto.setEstado("A");
        proyectoService.save(proyecto);

        redirectAttrs
                .addFlashAttribute("mensaje", "Registro Exitoso del Documento")
                .addFlashAttribute("clase", "success alert-dismissible fade show");

        return ResponseEntity.ok("1");
    }

    @RequestMapping(value = "/editar_proyecto/{id_proyecto}/{id_tipoProyecto}")
    public String editar_proyecto(@PathVariable("id_proyecto") Long id_proyecto,
            @PathVariable(name = "id_tipoProyecto") Long id_tipoProyecto, Model model) {

        // Definir un array con los nombres de los fragmentos
        String[] fragments = { "card_body1", "card_body2", "card_body3", "card_body4", "card_body5", "card_body6",
                "card_body7", "card_body8", "card_body9", "card_body10", "card_body11" };

        // Verificar si el id_tipoProyecto es válido
        if (id_tipoProyecto >= 1 && id_tipoProyecto <= fragments.length) {

            Proyecto proyecto = proyectoService.findOne(id_proyecto);

            model.addAttribute("proyecto", proyecto);
            // Devolver el fragmento correspondiente
            model.addAttribute("categorias", categoriaProyectoService.getCategoriasPorTipoProyecto(id_tipoProyecto));
            model.addAttribute("jurados", juradoService.findAll());
            model.addAttribute("docentes", docenteService.findAll());
            model.addAttribute("estudiantes", estudianteService.findAll());
            model.addAttribute("carreras", programaService.findAll());
            model.addAttribute("id_tipoProyecto", id_tipoProyecto);
            model.addAttribute("edit", true);
            return "Content/form_proyecto :: " + fragments[id_tipoProyecto.intValue() - 1];
        } else {
            // Manejar el caso en el que el id_tipoProyecto no es válido
            return "Content/form_proyecto :: default_fragment";
        }

    }

    // Boton para Guardar Documento
    @RequestMapping(value = "/ProyectoModF", method = RequestMethod.POST) // Enviar datos de Registro a Lista
    public ResponseEntity<String> ProyectoModF(@Validated Proyecto proyecto, RedirectAttributes redirectAttrs,
            @RequestParam(name = "estudiante", required = false) Long[] id_estudiantes,
            @RequestParam(name = "categoriaProyecto", required = true) Long id_categoriaProyecto,
            @RequestParam(name = "id_tipoProyecto") Long id_tipoProyecto,
            @RequestParam(name = "docente", required = false) Long id_docente,
            @RequestParam(name = "jurado", required = true) Long[] id_jurados)
            throws FileNotFoundException, IOException {

        Proyecto p = proyectoService.findOne(proyecto.getId_proyecto());
        p.setNombre_proyecto(proyecto.getNombre_proyecto());
        p.setNro_stand(proyecto.getNro_stand());
        p.setCategoriaProyecto(categoriaProyectoService.findOne(id_categoriaProyecto));
        if (id_docente != null) {
            p.setDocente(docenteService.findOne(id_docente));
        }
        p.getEstudiante().clear();

        for (Long idEstudiante : id_estudiantes) {
            Estudiante estudiante = estudianteService.findOne(idEstudiante);
            p.getEstudiante().add(estudiante);
        }
        // Elimina todos los jurados actuales del proyecto
        p.getJurado().clear();

        // Asigna la nueva lista de jurados al proyecto
        for (Long idJurado : id_jurados) {
            Jurado jurado = juradoService.findOne(idJurado); // Asegúrate de tener un método para encontrar el jurado
                                                             // por ID
            if (jurado != null) {
                p.getJurado().add(jurado);
            }
        }
        MultipartFile multipartFile = p.getFile();
        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
        AdjuntarArchivo adjuntarArchivo = new AdjuntarArchivo();

        Path rootPath = Paths.get("archivos");
        Path rootAbsolutPath = rootPath.toAbsolutePath();
        String rutaDirectorio = rootAbsolutPath + "";
        String rutaArchivo = adjuntarArchivo.crearSacDirectorio(rutaDirectorio);

        List<ArchivoAdjunto> listArchivos = archivoAdjuntoService.listarArchivoAdjunto();
        p.setNombreArchivo((listArchivos.size() + 1) + "-mod" + ".pdf");

        Integer ad = adjuntarArchivo.adjuntarArchivoProyecto(p, rutaArchivo);
        if (ad == 1) {
            ArchivoAdjunto barchivoAdjunto = archivoAdjuntoService
                    .buscarArchivoAdjuntoPorProyecto(p.getId_proyecto());
            if (barchivoAdjunto == null) {
                ArchivoAdjunto archivoAdjunt = new ArchivoAdjunto();
                archivoAdjunt.setNombre_archivo(p.getNombreArchivo());
                archivoAdjunt.setRuta_archivo(rutaArchivo);
                archivoAdjunt.setEstado("A");
                archivoAdjuntoService.registrarArchivoAdjunto(archivoAdjunt);
                p.setArchivoAdjunto(archivoAdjunt);
            } else {
                barchivoAdjunto.setNombre_archivo(p.getNombreArchivo());
                barchivoAdjunto.setRuta_archivo(rutaArchivo);
                archivoAdjuntoService.modificarArchivoAdjunto(barchivoAdjunto);
            }
        } else if (ad == 2) {
            // Opcional: Manejar el caso donde no se adjuntó ningún archivo, si es necesario
            System.out.println("No se adjuntó ningún archivo, pero el proyecto fue actualizado.");
        }

        p.setEstado("A");

        proyectoService.save(p);
        redirectAttrs
                .addFlashAttribute("mensaje2", "Datos del Documento Actualizados Correctamente")
                .addFlashAttribute("clase2", "success alert-dismissible fade show");

        return ResponseEntity.ok("2");
    }

    @RequestMapping(value = "/eliminar_proyecto/{id_proyecto}")
    @ResponseBody
    public void eliminar_proyecto(@PathVariable("id_proyecto") Long id_proyecto) {

        Proyecto proyecto = proyectoService.findOne(id_proyecto);

        proyecto.setEstado("X");

        proyectoService.save(proyecto);

    }

    @RequestMapping(value = "/ProyectoE", method = RequestMethod.GET) // Pagina principal
    public String ProyectoE(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("proyectos", proyectoService.proyectosEvaluados());
            model.addAttribute("docentes", docenteService.findAll());
            model.addAttribute("estudiantes", estudianteService.findAll());
            model.addAttribute("programas", programaService.findAll());
            model.addAttribute("jurados", juradoService.findAll());

            return "proyecto/proyectos-evaluados";
        } else {
            return "redirect:LoginR";
        }
    }

    @RequestMapping(value = "/openFileProyecto/{id}", method = RequestMethod.GET, produces = "application/pdf")
    public @ResponseBody FileSystemResource abrirArchivoMedianteResourse(HttpServletResponse response,
            @PathVariable("id") long id_proyecto) throws FileNotFoundException {
        ArchivoAdjunto ArchivoAdjuntos = archivoAdjuntoService.buscarArchivoAdjuntoPorProyecto(id_proyecto);

        File file = new File(ArchivoAdjuntos.getRuta_archivo() + ArchivoAdjuntos.getNombre_archivo());
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        return new FileSystemResource(file);
    }

    @GetMapping(value = "/openFileProyectoN/{id}", produces = "application/pdf")
    public void abrirDocumentoPDF(@PathVariable("id") long id_proyecto, HttpServletResponse response)
            throws IOException {
        ArchivoAdjunto archivoAdjunto = archivoAdjuntoService.buscarArchivoAdjuntoPorProyecto(id_proyecto);

        File file = new File(archivoAdjunto.getRuta_archivo() + archivoAdjunto.getNombre_archivo());
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));

        try (InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

}
