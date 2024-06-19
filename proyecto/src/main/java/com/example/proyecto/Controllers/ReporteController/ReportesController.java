package com.example.proyecto.Controllers.ReporteController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.OutputStream;


import com.example.proyecto.Models.Entity.CategoriaCriterio;
import com.example.proyecto.Models.Entity.Estudiante;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.Entity.Proyecto;
import com.example.proyecto.Models.Service.ICategoriaCriterioService;
import com.example.proyecto.Models.Service.IEvaluacionService;
import com.example.proyecto.Models.Service.IPonderacionService;
import com.example.proyecto.Models.Service.IProyectoService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.Cell;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
@Controller
public class ReportesController {
    
    @Autowired
    private IProyectoService proyectoService;
    @Autowired
    private ICategoriaCriterioService categoriaCriterioService;
    @Autowired
    private IEvaluacionService evaluacionService;

    @Autowired
    private IPonderacionService ponderacionService;


    @GetMapping("/FormReportes")
    public String formReportes(HttpServletRequest request, Model model){
        if (request.getSession().getAttribute("usuario") != null) {

            model.addAttribute("proyectos", proyectoService.findAll());

        return "reportes/formReportes";
        }else{
            return "redirect:LoginR";
        }
    }

    // @GetMapping("/ReporteProyecoctoOne")
    // public String reporteProyecoctoOne(@RequestParam(value = "id_proyecto")Long id_proyecto, Model model){
    //     Proyecto proyecto = proyectoService.findOne(id_proyecto); 
    //     //List<Evaluacion> evaluaciones = proyecto.getEvaluacion();
    //     Set<Evaluacion> evaluacionesSet = proyecto.getEvaluacion();
    //     List<Evaluacion> evaluacionesList = new ArrayList<>(evaluacionesSet);
    //     evaluacionesList.sort(Comparator.comparing(evaluacion -> evaluacion.getId_evaluacion()));
    //     List<Criterio> c1 = new ArrayList<>();
    //     List<Criterio> c2 = new ArrayList<>();
    //     List<Criterio> c3 = new ArrayList<>();
    //     int contador = 0;
    //     for (Evaluacion e : evaluacionesList) {
    //         if (contador == 0) {
    //             for (Criterio c : e.getCriterios()) {
    //                 c1.add(c);
    //             }
    //         } else if (contador == 1) {
    //             for (Criterio c : e.getCriterios()) {
    //                 c2.add(c);
    //             }
    //         } else if (contador == 2) {
    //             for (Criterio c : e.getCriterios()) {
    //                 c3.add(c);
    //             }
    //         }
    //         contador++;
    //         System.out.println(e.getPuntaje_total());
    //     }
    //     c1.sort(Comparator.comparing(criterio -> criterio.getPreguntas().getId_pregunta()));
    //     c2.sort(Comparator.comparing(criterio -> criterio.getPreguntas().getId_pregunta()));
    //     c3.sort(Comparator.comparing(criterio -> criterio.getPreguntas().getId_pregunta()));

    //     model.addAttribute("criterio1", c1);
    //     model.addAttribute("criterio2", c2);
    //     model.addAttribute("criterio3", c3);
    //     model.addAttribute("ev", evaluacionesList);
    //     model.addAttribute("proyecto", proyecto);
    //     model.addAttribute("cat", categoriaCriterioService.findAll());
    //     if (evaluacionesList.size() == 2) {
    //     return "reportes/print2";   
    //     }
    //     return "reportes/print";
    // }

    // @GetMapping("/ReporteProyecoctoOne")
    // public String reporteProyecoctoOne(@RequestParam(value = "id_proyecto")Long id_proyecto, Model model){

    //     Proyecto proyecto = proyectoService.findOne(id_proyecto); 
        
    //     model.addAttribute("proyecto", proyecto);
    //     model.addAttribute("categorias", categoriaCriterioService.obtenerPonderacionesPorProyecto(id_proyecto));
    //     model.addAttribute("ponderaciones", ponderacionService.obtenerPonderacionesPorProyecto(id_proyecto));
    //     model.addAttribute("evaluaciones", evaluacionService.obtenerNotasFinales(id_proyecto));
    //     return "reportes/report_dinamico";
    // }

    @GetMapping("/ReporteProyecoctoOne")
    public ResponseEntity<byte[]> reporteProyecoctoOne(@RequestParam(value = "id_proyecto") Long id_proyecto) {
        Proyecto proyecto = proyectoService.findOne(id_proyecto);
        List<CategoriaCriterio> categorias = categoriaCriterioService.obtenerPonderacionesPorProyecto(id_proyecto);
        List<Ponderacion> ponderaciones = ponderacionService.obtenerPonderacionesPorProyecto(id_proyecto);
        List<Evaluacion> evaluaciones = evaluacionService.obtenerNotasFinales(id_proyecto);

        OutputStream outputStream = new ByteArrayOutputStream();
        // izquierda, derecha, arriba, abajo
        Document document = new Document(PageSize.LETTER, 70, 70, 88, 33);

        try {
            
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            String rutaImagen = Paths.get("").toAbsolutePath().toString() + "/proyecto/src/main/resources/static/assets/images/menbretado_uap.png";
            Image imagenFondo = Image.getInstance(rutaImagen);
            imagenFondo.scaleAbsolute(document.getPageSize());
            imagenFondo.setAbsolutePosition(0, 0);
            
            document.add(imagenFondo);
            // String nom_completo = personal.getPersona().getNombre() + " " + personal.getPersona().getApellido();
            // addTitle(numero, personal.getCargo_funcionario(), nom_completo, document);
            // addCuerpo(document);
            // document.add(new Paragraph("hola"));
            addTitle(proyecto, document);
            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] contenidoPDF = ((ByteArrayOutputStream) outputStream).toByteArray();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_PDF)
                .body(contenidoPDF);
    }

    public static void addTitle(Proyecto proyecto, Document document) throws DocumentException {
        // BaseFont baseFont = BaseFont.createFont(Paths.get("").toAbsolutePath().toString()+ "/planificacion/src/main/resources/static/assets/memo/timesnewromanbold.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font negrita_titulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font negrita = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        // BaseFont baseFont2 = BaseFont.createFont(Paths.get("").toAbsolutePath().toString()+ "/planificacion/src/main/resources/static/assets/memo/timesnewroman.ttf",BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font normal = new Font(Font.FontFamily.HELVETICA, 10);


        Paragraph title = new Paragraph();

        Chunk ins = new Chunk("REPORTE \n", negrita_titulo);
        ins.setUnderline(0.1f, -1.3f);
        title.add(ins);

        title.setAlignment(Element.ALIGN_CENTER);
        title.setLeading(13.2f); // salto de lineas de texto en un parrafo
        title.setSpacingBefore(10);// salto de filas antes de parrafo
        title.setSpacingAfter(15);// salto de filas despues de parrafo
        document.add(title);

        PdfPTable table = new PdfPTable(2);
        
        // Crear una celda con colspan
        PdfPCell cell = new PdfPCell(createCell("DETALLE DEL PROYECTO", negrita));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setMinimumHeight(20);
        table.addCell(cell);
        
        // Establecer los anchos de las columnas
        float[] columnWidths = {3f, 5f}; // Proporción de los anchos de las columnas
        table.setWidths(columnWidths);
        // Añadir más filas a la tabla
        PdfPCell celll = createCell("PROGRAMA :", negrita);
        celll.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(celll);
        table.addCell(createCell(proyecto.getPrograma().getNombre_programa().toUpperCase(), normal));
        table.addCell(createCell("CATEGORIA:", negrita));  
        table.addCell(createCell(proyecto.getCategoria_proyecto(), normal));  
        table.addCell(createCell("TITULO DEL PROYECTO:", negrita));  
        table.addCell(createCell(proyecto.getNombre_proyecto(), normal));  
        table.addCell(createCell("PARTICIPANTES:", negrita)); 
        String nombres = "";
        for (Estudiante estudiante   : proyecto.getEstudiante()) {
            nombres += estudiante.getPersona().getNombres()+" "+estudiante.getPersona().getPaterno()+" "+estudiante.getPersona().getMaterno()+ " \n" ;
        }
        table.addCell(createCell(nombres,normal)); 
        table.addCell(createCell("DOCENTE ASESOR:", negrita)); 
        String docente =  proyecto.getDocente().getPersona().getNombres()+" "+proyecto.getDocente().getPersona().getPaterno()+" "+proyecto.getDocente().getPersona().getMaterno();
        table.addCell(createCell(docente, normal));  

        document.add(table);
       
    }
    
    private static PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorderColor(BaseColor.BLACK);
        
        return cell;
    }
}
