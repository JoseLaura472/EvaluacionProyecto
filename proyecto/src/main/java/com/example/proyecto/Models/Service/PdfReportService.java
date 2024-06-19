package com.example.proyecto.Models.Service;

import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.example.proyecto.Models.Entity.CategoriaCriterio;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.Ponderacion;
import com.example.proyecto.Models.Entity.Proyecto;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.nio.charset.StandardCharsets;


public class PdfReportService {
    
    // @Autowired
    // private TemplateEngine templateEngine;

    // public ByteArrayInputStream generateReport(Proyecto proyecto, List<CategoriaCriterio> categorias, List<Ponderacion> ponderaciones, List<Evaluacion> evaluaciones) {
    //     Context context = new Context();
    //     context.setVariable("proyecto", proyecto);
    //     context.setVariable("categorias", categorias);
    //     context.setVariable("ponderaciones", ponderaciones);
    //     context.setVariable("evaluaciones", evaluaciones);

    //     // String htmlContent = templateEngine.process("reportes/report_dinamico", context);
        
    //     OutputStream outputStream = new ByteArrayOutputStream();

    //     //  izquierda, derecha, arriba, abajo
    //     Document document = new Document();
    //            try {
            
    //         PdfWriter writer = PdfWriter.getInstance(document, outputStream);
    //         document.open();

    //         // String rutaImagen = Paths.get("").toAbsolutePath().toString() + "/planificacion/src/main/resources/static/assets/memo/fondo.png";
    //         // Image imagenFondo = Image.getInstance(rutaImagen);
    //         // imagenFondo.scaleAbsolute(document.getPageSize());
    //         // imagenFondo.setAbsolutePosition(0, 0);
            
    //         // document.add(imagenFondo);
    //         // String nom_completo = personal.getPersona().getNombre() + " " + personal.getPersona().getApellido();
    //         // addTitle(numero, personal.getCargo_funcionario(), nom_completo, document);
    //         // addCuerpo(document);
    //         document.add(new Phrase("hola Mundo"));
    //         document.close();
    //         writer.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
    // }

    // @GetMapping(value = "/generarPDF/{id_unidad_funcional}/{id_personal_administrativo}", produces = MediaType.APPLICATION_PDF_VALUE)
    // public ResponseEntity<byte[]> generarPDF(
    //         @PathVariable(value = "id_unidad_funcional") Long id_unidad_funcional,
    //         @PathVariable(value = "id_personal_administrativo") Long id_personal_administrativo,
    //         @RequestParam(value = "dato", required = false) String numero) throws IOException {

    //     PersonalAdministrativo personal = administrativoService.findOne(id_personal_administrativo);
    //     System.out.println("cite:> "+numero);
        
    //     OutputStream outputStream = new ByteArrayOutputStream();
    //     // izquierda, derecha, arriba, abajo
    //     Document document = new Document(PageSize.LETTER, 70, 70, 88, 33);

    //     try {
            
    //         PdfWriter writer = PdfWriter.getInstance(document, outputStream);
    //         document.open();

    //         String rutaImagen = Paths.get("").toAbsolutePath().toString() + "/planificacion/src/main/resources/static/assets/memo/fondo.png";
    //         Image imagenFondo = Image.getInstance(rutaImagen);
    //         imagenFondo.scaleAbsolute(document.getPageSize());
    //         imagenFondo.setAbsolutePosition(0, 0);
            
    //         document.add(imagenFondo);
    //         String nom_completo = personal.getPersona().getNombre() + " " + personal.getPersona().getApellido();
    //         addTitle(numero, personal.getCargo_funcionario(), nom_completo, document);
    //         addCuerpo(document);
    //         document.close();
    //         writer.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     byte[] contenidoPDF = ((ByteArrayOutputStream) outputStream).toByteArray();
    //     return ResponseEntity
    //             .status(HttpStatus.OK)
    //             .contentType(MediaType.APPLICATION_PDF)
    //             .body(contenidoPDF);
    // }
}
