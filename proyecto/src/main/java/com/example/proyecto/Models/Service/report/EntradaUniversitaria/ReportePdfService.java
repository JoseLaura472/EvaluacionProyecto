package com.example.proyecto.Models.Service.report.EntradaUniversitaria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.proyecto.Models.Entity.Actividad;
import com.example.proyecto.Models.Entity.CategoriaActividad;
import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.EvaluacionDetalle;
import com.example.proyecto.Models.Entity.Inscripcion;
import com.example.proyecto.Models.Entity.Jurado;
import com.example.proyecto.Models.Entity.JuradoAsignacion;
import com.example.proyecto.Models.Entity.Participante;
import com.example.proyecto.Models.Entity.Rubrica;
import com.example.proyecto.Models.Entity.RubricaCriterio;
import com.example.proyecto.Models.IService.IActividadService;
import com.example.proyecto.Models.IService.ICategoriaActividadService;
import com.example.proyecto.Models.IService.IEvaluacionService;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.example.proyecto.Models.IService.IJuradoAsignacionService;
import com.example.proyecto.Models.IService.IParticipanteService;
import com.example.proyecto.Models.IService.IRubricaService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportePdfService {
    private final IActividadService actividadService;
    private final ICategoriaActividadService categoriaService;
    private final IParticipanteService participanteService;
    private final IInscripcionService inscripcionService;
    private final IEvaluacionService evaluacionService;
    private final IJuradoAsignacionService juradoAsignacionService;
    private final IRubricaService rubricaService;

    @Value("${app.logo.izquierdo:}")
    private String logoIzquierdoPath;

    @Value("${app.logo.derecho:}")
    private String logoDerechoPath;

    // HELPER

    /**
     * Genera una página de evaluación individual con MÚLTIPLES rúbricas
     */
    private void generarPaginaEvaluacionMultipleRubricas(
            Document document,
            Actividad actividad,
            CategoriaActividad categoria,
            Participante participante,
            List<Rubrica> rubricas,
            Evaluacion evaluacion) {

        // Encabezado
        agregarEncabezado(document, actividad);

        // Título
        Paragraph titulo = new Paragraph()
                .add(new Text("FORMULARIO DE EVALUACIÓN DE: ").setBold().setFontSize(11))
                .add(new Text(participante.getNombre().toUpperCase()).setFontSize(11))
                .setMarginBottom(15);
        document.add(titulo);

        // Tabla de criterios generales (estática según categoría)
        Table tablaCriterios = crearTablaCriteriosEstatica(categoria.getNombre());
        document.add(tablaCriterios);

        // Categoría
        Paragraph categoriaP = new Paragraph()
                .add(new Text("CATEGORÍA: ").setBold().setFontSize(10))
                .add(new Text(categoria.getNombre().toUpperCase()).setFontSize(10))
                .setMarginTop(12)
                .setMarginBottom(8);
        document.add(categoriaP);

        // ═══════════════════════════════════════════════════════════
        // Tabla de evaluación para MÚLTIPLES RÚBRICAS
        // ═══════════════════════════════════════════════════════════
        Table tablaEvaluacion = crearTablaEvaluacionMultipleRubricas(participante, rubricas, evaluacion);
        document.add(tablaEvaluacion);

        // Nota
        document.add(new Paragraph("Nota: Formulario de evaluación con múltiples rúbricas")
                .setFontSize(10)
                .setItalic()
                .setMarginTop(10));

        // Firmas
        agregarSeccionFirmas(document, evaluacion.getJurado());
    }

    /**
     * Crea la tabla de evaluación dinámica con MÚLTIPLES rúbricas
     */
    private Table crearTablaEvaluacionMultipleRubricas(
            Participante participante,
            List<Rubrica> rubricas,
            Evaluacion evaluacion) {

        // Recopilar todos los criterios de todas las rúbricas
        List<RubricaCriterio> todosCriterios = new ArrayList<>();
        Map<Long, String> criterioARubricaNombre = new HashMap<>();

        for (Rubrica rubrica : rubricas) {
            List<RubricaCriterio> criteriosDeRubrica = rubrica.getCriterios().stream()
                    .filter(c -> "A".equals(c.getEstado()))
                    .collect(Collectors.toList());

            todosCriterios.addAll(criteriosDeRubrica);

            for (RubricaCriterio criterio : criteriosDeRubrica) {
                criterioARubricaNombre.put(criterio.getIdRubricaCriterio(), rubrica.getNombre());
            }
        }

        // Crear tabla con columnas dinámicas
        float[] columnWidths = new float[todosCriterios.size() + 2]; // Nombre + criterios + Total
        columnWidths[0] = 3; // Nombre de la danza
        for (int i = 1; i <= todosCriterios.size(); i++) {
            columnWidths[i] = 1.5f; // Criterios
        }
        columnWidths[columnWidths.length - 1] = 1.5f; // Total

        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth();

        DeviceRgb colorEncabezado = new DeviceRgb(41, 128, 185);

        // Encabezado
        table.addCell(crearCeldaEncabezado("Nombre de la danza", colorEncabezado));

        for (RubricaCriterio criterio : todosCriterios) {
            String titulo;
            if (rubricas.size() > 1) {
                // Indicar la rúbrica si hay múltiples
                String nombreRubrica = criterioARubricaNombre.get(criterio.getIdRubricaCriterio());
                titulo = nombreRubrica + "\n" + criterio.getNombre() + "\n(" + criterio.getPorcentaje() + " pts)";
            } else {
                titulo = criterio.getNombre() + "\n(" + criterio.getPorcentaje() + " pts)";
            }

            table.addCell(crearCeldaEncabezado(titulo, colorEncabezado)
                    .setFontSize(7));
        }

        table.addCell(crearCeldaEncabezado("TOTAL", colorEncabezado));

        // Datos del participante
        table.addCell(crearCeldaNormal(participante.getNombre()));

        double total = 0;

        for (RubricaCriterio criterio : todosCriterios) {
            double puntaje = 0;

            // Buscar el puntaje en los detalles de la evaluación
            if (evaluacion.getDetalles() != null) {
                for (EvaluacionDetalle detalle : evaluacion.getDetalles()) {
                    if (detalle.getRubricaCriterio() != null &&
                            detalle.getRubricaCriterio().getIdRubricaCriterio()
                                    .equals(criterio.getIdRubricaCriterio())) {
                        puntaje = detalle.getPuntaje();
                        break;
                    }
                }
            }

            total += puntaje;
            table.addCell(crearCeldaNormal(String.format("%.2f", puntaje), TextAlignment.CENTER));
        }

        table.addCell(crearCeldaNormal(String.format("%.2f", total), TextAlignment.CENTER, true)
                .setBackgroundColor(new DeviceRgb(241, 196, 15)));

        return table;
    }

    /**
     * MÉTODO PRINCIPAL - Genera reporte por participante y categoría
     * Crea UNA PÁGINA por cada jurado que evaluó al participante
     * CORREGIDO: Usa la estructura correcta de Evaluacion + EvaluacionDetalle
     */
    public byte[] generarReportePorParticipanteCategoria(
        Long idParticipante, 
        Long idCategoria) {

    try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4);
        
        Document document = new Document(pdfDoc);
        document.setMargins(25, 25, 25, 25);

        // Obtener datos
        Participante participante = participanteService.findById(idParticipante);
        CategoriaActividad categoria = categoriaService.findById(idCategoria);
        
        if (participante == null || categoria == null) {
            throw new RuntimeException("Participante o categoría no encontrados");
        }

        Actividad actividad = categoria.getActividad();
        
        // Obtener TODAS las evaluaciones de este participante en esta categoría
        List<Evaluacion> evaluaciones = evaluacionService
                .findByParticipanteAndCategoria(idParticipante, idCategoria);

        if (evaluaciones.isEmpty()) {
            agregarEncabezado(document, actividad);
            document.add(new Paragraph("No hay evaluaciones registradas para este participante en esta categoría.")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(50)
                    .setFontSize(11)
                    .setItalic());
        } else {
            // Agrupar evaluaciones por jurado
            Map<Long, List<Evaluacion>> evaluacionesPorJurado = evaluaciones.stream()
                    .collect(Collectors.groupingBy(e -> e.getJurado().getIdJurado()));

            // Generar una página por cada jurado
            int contador = 0;
            for (Map.Entry<Long, List<Evaluacion>> entry : evaluacionesPorJurado.entrySet()) {
                if (contador > 0) {
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
                
                Jurado jurado = entry.getValue().get(0).getJurado();
                List<Evaluacion> evaluacionesJurado = entry.getValue();
                
                generarPaginaJurado(document, actividad, categoria, participante, jurado, evaluacionesJurado);
                
                contador++;
            }
        }

        document.close();
        return baos.toByteArray();

    } catch (Exception e) {
        log.error("Error generando reporte por participante-categoría", e);
        throw new RuntimeException("Error generando reporte: " + e.getMessage());
    }
}

    /**
     * Genera una página completa de evaluación para UN jurado específico
     * El jurado puede haber evaluado múltiples rúbricas
     */
    private void generarPaginaJurado(
        Document document,
        Actividad actividad,
        CategoriaActividad categoria,
        Participante participante,
        Jurado jurado,
        List<Evaluacion> evaluacionesJurado) {

    // ENCABEZADO
    agregarEncabezado(document, actividad);

    // INFORMACIÓN PRINCIPAL (Compacta)
    Table tablaInfo = new Table(UnitValue.createPercentArray(new float[]{0.8f, 2, 0.8f, 2}))
            .useAllAvailableWidth()
            .setMarginBottom(6);

    tablaInfo.addCell(crearCeldaLabel("Participante:"));
    tablaInfo.addCell(crearCeldaValor(participante.getNombre().toUpperCase()));
    tablaInfo.addCell(crearCeldaLabel("Categoría:"));
    tablaInfo.addCell(crearCeldaValor(categoria.getNombre().toUpperCase()));

    document.add(tablaInfo);

    // INFORMACIÓN DEL JURADO (línea compacta)
    Table tablaJuradoCompacta = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
            .useAllAvailableWidth()
            .setMarginBottom(8);

    tablaJuradoCompacta.addCell(crearCeldaLabel("Jurado:"));
    tablaJuradoCompacta.addCell(crearCeldaValor(jurado.getPersona().getNombreCompleto()));

    document.add(tablaJuradoCompacta);

    // Colores más suaves y transparentes para cada rúbrica
    DeviceRgb[] coloresRubricas = new DeviceRgb[]{
            new DeviceRgb(173, 216, 230),   // Azul claro
            new DeviceRgb(144, 238, 144),  // Verde claro
            new DeviceRgb(221, 160, 221),  // Púrpura claro
            new DeviceRgb(255, 218, 185)   // Naranja claro
    };

    // Iterar por cada evaluación del jurado (cada rúbrica)
    double totalGeneral = 0;
    int numeroRubrica = 0;

    for (Evaluacion evaluacion : evaluacionesJurado) {
        Rubrica rubrica = evaluacion.getRubrica();
        DeviceRgb colorRubrica = coloresRubricas[numeroRubrica % coloresRubricas.length];
        
        if (numeroRubrica > 0) {
            document.add(new Paragraph()
                    .setMarginTop(2)
                    .setMarginBottom(2));
        }

        // TÍTULO DE LA RÚBRICA (Solo con color de fondo suave en el título)
        document.add(new Paragraph(rubrica.getNombre().toUpperCase())
                .setFontSize(8)
                .setBold()
                .setBackgroundColor(colorRubrica)
                .setFontColor(ColorConstants.BLACK)
                .setPadding(2)
                .setMarginBottom(3)
                .setMarginTop(0)
                .setMarginLeft(0)
                .setMarginRight(0));

        // TABLA DE CRITERIOS EVALUADOS POR ESTA RÚBRICA (Sin colores, solo bordes simples)
        List<EvaluacionDetalle> detalles = evaluacion.getDetalles();

        if (detalles != null && !detalles.isEmpty()) {
            Table tablaCriterios = new Table(UnitValue.createPercentArray(new float[]{2.5f, 0.7f, 0.8f}))
                    .useAllAvailableWidth()
                    .setMarginLeft(5)
                    .setMarginRight(5);

            // ENCABEZADOS (Sin color en tabla, solo texto)
            tablaCriterios.addCell(crearCeldaEncabezadoSimple("Criterio"));
            tablaCriterios.addCell(crearCeldaEncabezadoSimple("Puntaje"));
            tablaCriterios.addCell(crearCeldaEncabezadoSimple("Pond."));

            // FILAS DE CRITERIOS
            double subTotal = 0;
            
            for (EvaluacionDetalle detalle : detalles) {
                RubricaCriterio criterio = detalle.getRubricaCriterio();

                // Nombre del criterio
                tablaCriterios.addCell(crearCeldaNormalSimple(criterio.getNombre()));

                // Puntaje
                tablaCriterios.addCell(crearCeldaNormalSimple(
                        String.format("%.2f", detalle.getPuntaje()),
                        TextAlignment.CENTER));

                // Ponderado (resaltado en amarillo muy claro)
                tablaCriterios.addCell(crearCeldaNormalSimple(
                        String.format("%.2f", detalle.getPonderado()),
                        TextAlignment.CENTER)
                        .setBackgroundColor(new DeviceRgb(255, 250, 205)));

                subTotal += detalle.getPuntaje();
            }

            // FILA DE SUBTOTAL POR RÚBRICA (Sin color, solo bordes)
            Cell cellSubTotalLabel = new Cell(1, 2)
                    .add(new Paragraph("SUBTOTAL")
                            .setBold()
                            .setFontSize(7))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setPadding(2)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            tablaCriterios.addCell(cellSubTotalLabel);

            tablaCriterios.addCell(crearCeldaNormalSimple(String.format("%.2f", subTotal),
                    TextAlignment.CENTER)
                    .setBold());

            document.add(tablaCriterios);
            
            totalGeneral += subTotal;
        }

        numeroRubrica++;
    }

    // TOTAL GENERAL (Sin color, solo bordes simples)
    document.add(new Paragraph()
            .setMarginTop(4)
            .setMarginBottom(2));

    Table tablaTotal = new Table(UnitValue.createPercentArray(new float[]{2.5f, 0.7f, 0.8f}))
            .useAllAvailableWidth()
            .setMarginLeft(5)
            .setMarginRight(5);

    Cell cellTotalLabel = new Cell(1, 2)
            .add(new Paragraph("TOTAL GENERAL")
                    .setBold()
                    .setFontSize(8))
            .setTextAlignment(TextAlignment.RIGHT)
            .setPadding(2)
            .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
            .setVerticalAlignment(VerticalAlignment.MIDDLE);
    tablaTotal.addCell(cellTotalLabel);

    tablaTotal.addCell(crearCeldaNormalSimple(String.format("%.2f", totalGeneral),
            TextAlignment.CENTER)
            .setBold()
            .setFontSize(8));

    document.add(tablaTotal);

    // SECCIÓN DE FIRMAS (Compacta)
    agregarSeccionFirmas(document, jurado);
}

/**
     * Encabezado oficial del documento
     */
    private void agregarEncabezado(Document document, Actividad actividad) {
    try {
        Table tableHeader = new Table(UnitValue.createPercentArray(new float[]{0.7f, 3, 0.7f}))
                .useAllAvailableWidth()
                .setMarginBottom(6);

        // Logo izquierdo
        Cell cellLogoIzq = new Cell();
        if (logoIzquierdoPath != null && !logoIzquierdoPath.isEmpty()) {
            try {
                ImageData imageDataIzq = ImageDataFactory.create(logoIzquierdoPath);
                Image logoIzq = new Image(imageDataIzq).setWidth(40).setHeight(40);
                cellLogoIzq.add(logoIzq).setTextAlignment(TextAlignment.CENTER);
            } catch (Exception e) {
                log.warn("Logo izquierdo no encontrado");
            }
        }
        cellLogoIzq.setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0);
        tableHeader.addCell(cellLogoIzq);

        // Información central
        Cell cellInfo = new Cell()
                .add(new Paragraph("FEXCOIN V.4")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(10)
                        .setBold())
                .add(new Paragraph("Feria Exposición de Conocimiento e Investigación")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(7))
                .add(new Paragraph(actividad.getNombre())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(6)
                        .setMarginTop(1))
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(2);
        tableHeader.addCell(cellInfo);

        // Logo derecho
        Cell cellLogoDer = new Cell();
        if (logoDerechoPath != null && !logoDerechoPath.isEmpty()) {
            try {
                ImageData imageDataDer = ImageDataFactory.create(logoDerechoPath);
                Image logoDer = new Image(imageDataDer).setWidth(40).setHeight(40);
                cellLogoDer.add(logoDer).setTextAlignment(TextAlignment.CENTER);
            } catch (Exception e) {
                log.warn("Logo derecho no encontrado");
            }
        }
        cellLogoDer.setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE).setPadding(0);
        tableHeader.addCell(cellLogoDer);

        document.add(tableHeader);
        
        // Línea separadora
        document.add(new Paragraph()
                .setBorderBottom(new SolidBorder(1.2f))
                .setMarginBottom(6)
                .setMarginTop(0));

    } catch (Exception e) {
        log.warn("Error agregando encabezado: " + e.getMessage());
    }
}


/**
     * Sección de firmas
     */
    private void agregarSeccionFirmas(Document document, Jurado jurado) {
        document.add(new Paragraph()
                .setMarginTop(8)
                .setMarginBottom(0));

        Table tablaFirmas = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }))
                .useAllAvailableWidth();

        // Firma del jurado
        Cell cellJurado = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(0);

        cellJurado.add(new Paragraph("_____________________")
                .setMarginBottom(1)
                .setMarginTop(15)
                .setFontSize(7));
        cellJurado.add(new Paragraph("Firma del Jurado")
                .setBold()
                .setFontSize(7));
        cellJurado.add(new Paragraph(jurado.getPersona().getNombreCompleto())
                .setFontSize(6)
                .setMarginTop(0));

        tablaFirmas.addCell(cellJurado);

        // Firma del notario
        Cell cellNotario = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(0);

        cellNotario.add(new Paragraph("_____________________")
                .setMarginBottom(1)
                .setMarginTop(15)
                .setFontSize(7));
        cellNotario.add(new Paragraph("Firma y Sello del Notario")
                .setBold()
                .setFontSize(7));
        cellNotario.add(new Paragraph("Notario")
                .setFontSize(6)
                .setMarginTop(0));

        tablaFirmas.addCell(cellNotario);

        document.add(tablaFirmas);
    }

    @Data
    @AllArgsConstructor
    public static class CriterioEvaluado {
        private String nombreRubrica;
        private String nombreCriterio;
        private double puntaje;
    }

    /**
     * Genera reporte total de una categoría (todos los participantes)
     */
    public byte[] generarReportePorCategoria(Long idActividad, Long idCategoria) {
    try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4.rotate()); // Horizontal
        Document document = new Document(pdfDoc);
        document.setMargins(30, 40, 30, 40);

        // Obtener datos
        Actividad actividad = actividadService.findById(idActividad);
        CategoriaActividad categoria = categoriaService.findById(idCategoria);
        
        if (actividad == null || categoria == null) {
            throw new RuntimeException("Datos incompletos");
        }

        // Encabezado
        agregarEncabezado(document, actividad);
        
        // Título
        document.add(new Paragraph("REPORTE GENERAL - CATEGORÍA: " + categoria.getNombre().toUpperCase())
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(16)
            .setBold()
            .setMarginBottom(20));

        // Obtener inscripciones de esta categoría
        List<Inscripcion> inscripciones = inscripcionService
            .findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividadOrderByParticipante_PosicionAsc(
                idActividad, idCategoria);

        if (inscripciones.isEmpty()) {
            document.add(new Paragraph("No hay participantes inscritos en esta categoría.")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setItalic()
                .setMarginTop(50));
        } else {
            // Tabla de resultados con ranking
            Table table = crearTablaResultadosCategoriaConRanking(inscripciones, idActividad, idCategoria);
            document.add(table);
        }

        // Pie de página
        agregarPieDePagina(document);
        
        document.close();
        return baos.toByteArray();
        
    } catch (Exception e) {
        log.error("Error generando reporte por categoría", e);
        throw new RuntimeException("Error generando reporte: " + e.getMessage());
    }
}

/**
 * Crea tabla de resultados por categoría CON RANKING
 * Columnas: Puesto | Participante | Jurado1 | Jurado2 | ... | Promedio
 */
private Table crearTablaResultadosCategoriaConRanking(
        List<Inscripcion> inscripciones, 
        Long idActividad, 
        Long idCategoria) {
    
    // Obtener jurados asignados a esta categoría
    List<JuradoAsignacion> asignaciones = juradoAsignacionService
        .findByActividadAndCategoria(idActividad, idCategoria);
    
    int numJurados = asignaciones.size();
    
    // CALCULAR NÚMERO DE COLUMNAS
    // Puesto | Participante | Jurado1 | Jurado2 | ... | Promedio
    int numColumnas = 1 + 1 + numJurados + 1;
    
    float[] columnWidths = new float[numColumnas];
    columnWidths[0] = 0.6f;  // Puesto
    columnWidths[1] = 3f;    // Participante
    
    // Columnas de jurados
    for (int i = 2; i < 2 + numJurados; i++) {
        columnWidths[i] = 1.3f;
    }
    
    columnWidths[numColumnas - 1] = 1.3f; // Promedio
    
    Table table = new Table(UnitValue.createPercentArray(columnWidths))
        .useAllAvailableWidth();
    
    DeviceRgb colorEncabezado = new DeviceRgb(41, 128, 185);
    
    // === ENCABEZADOS ===
    table.addCell(crearCeldaEncabezado("", colorEncabezado));
    table.addCell(crearCeldaEncabezado("Participante", colorEncabezado));
    
    // Nombres de jurados
    for (JuradoAsignacion asignacion : asignaciones) {
        String nombre = asignacion.getJurado().getPersona().getNombres();
        table.addCell(crearCeldaEncabezado(nombre, colorEncabezado)
            .setFontSize(9));
    }
    
    table.addCell(crearCeldaEncabezado("Promedio", colorEncabezado));
    
    // === CALCULAR PUNTAJES Y ORDENAR ===
    
    // Estructura para almacenar datos de cada participante
    class ParticipanteData {
        Participante participante;
        Map<Long, Double> puntajesPorJurado = new HashMap<>();
        double promedio = 0;
        int juradosCompletados = 0;
    }
    
    List<ParticipanteData> datosParticipantes = new ArrayList<>();
    
    for (Inscripcion inscripcion : inscripciones) {
        Participante p = inscripcion.getParticipante();
        ParticipanteData data = new ParticipanteData();
        data.participante = p;
        
        double suma = 0;
        int count = 0;
        
        // Calcular puntaje por cada jurado
        for (JuradoAsignacion asignacion : asignaciones) {
            Long juradoId = asignacion.getJurado().getIdJurado();
            
            // Obtener TODAS las evaluaciones de este jurado para este participante en esta categoría
            List<Evaluacion> evaluaciones = evaluacionService
                .findByJuradoAndParticipanteAndCategoria2(juradoId, p.getIdParticipante(), idCategoria);
            
            if (!evaluaciones.isEmpty()) {
                // SUMAR todas las rúbricas evaluadas por este jurado
                double totalJurado = evaluaciones.stream()
                    .mapToDouble(Evaluacion::getTotalPonderacion)
                    .sum();
                
                data.puntajesPorJurado.put(juradoId, totalJurado);
                suma += totalJurado;
                count++;
            }
        }
        
        // Calcular promedio
        if (count > 0) {
            data.promedio = suma / count;
            data.juradosCompletados = count;
        }
        
        datosParticipantes.add(data);
    }
    
    // ORDENAR POR PROMEDIO (descendente)
    datosParticipantes.sort((a, b) -> Double.compare(b.promedio, a.promedio));
    
    // === GENERAR FILAS CON RANKING ===
    int puesto = 1;
    double puntajeAnterior = -1;
    int puestoMostrado = 1;
    
    for (int i = 0; i < datosParticipantes.size(); i++) {
        ParticipanteData data = datosParticipantes.get(i);
        
        // Si el puntaje es igual al anterior, mantener el mismo puesto
        if (i > 0 && Math.abs(data.promedio - puntajeAnterior) < 0.01) {
            // Mismo puesto
        } else {
            puestoMostrado = puesto;
        }
        
        puntajeAnterior = data.promedio;
        
        // Columna 1: Puesto con medalla
        String textoPuesto = String.valueOf(puestoMostrado);
        if (puestoMostrado == 1) textoPuesto = "🥇 1°";
        else if (puestoMostrado == 2) textoPuesto = "🥈 2°";
        else if (puestoMostrado == 3) textoPuesto = "🥉 3°";
        else textoPuesto = puestoMostrado + "°";
        
        table.addCell(crearCeldaNormal(textoPuesto, TextAlignment.CENTER, true));
        
        // Columna 2: Nombre del participante
        table.addCell(crearCeldaNormal(data.participante.getNombre()));
        
        // Columnas 3 a N: Puntajes de cada jurado
        for (JuradoAsignacion asignacion : asignaciones) {
            Long juradoId = asignacion.getJurado().getIdJurado();
            Double puntaje = data.puntajesPorJurado.get(juradoId);
            
            if (puntaje != null) {
                table.addCell(crearCeldaNormal(
                    String.format("%.2f", puntaje), 
                    TextAlignment.CENTER));
            } else {
                table.addCell(crearCeldaNormal("-", TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));
            }
        }
        
        // Última columna: Promedio
        if (data.promedio > 0) {
            table.addCell(crearCeldaNormal(
                String.format("%.2f", data.promedio), 
                TextAlignment.CENTER, 
                true)
                .setBackgroundColor(new DeviceRgb(241, 196, 15)));
        } else {
            table.addCell(crearCeldaNormal("-", TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));
        }
        
        puesto++;
    }
    
    return table;
}

    /**
     * Genera reporte TOTAL (todas las categorías sumadas)
     */
    public byte[] generarReporteTotal(Long idActividad) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

            Document document = new Document(pdfDoc);
            document.setMargins(30, 40, 30, 40);

            // Obtener datos
            Actividad actividad = actividadService.findById(idActividad);

            if (actividad == null) {
                throw new RuntimeException("Actividad no encontrada");
            }

            // Encabezado
            agregarEncabezado(document, actividad);

            document.add(new Paragraph("REPORTE TOTAL - RESULTADOS GENERALES")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16)
                    .setBold()
                    .setMarginBottom(20));

            // Obtener todas las inscripciones
            List<Inscripcion> todasInscripciones = inscripcionService.findByActividad(idActividad);

            // Agrupar por participante
            Map<Long, List<Inscripcion>> porParticipante = todasInscripciones.stream()
                    .collect(Collectors.groupingBy(i -> i.getParticipante().getIdParticipante()));

            // Crear tabla de resultados totales
            Table table = crearTablaResultadosTotal(porParticipante, idActividad);
            document.add(table);

            // Pie de página
            agregarPieDePagina(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando reporte total", e);
            throw new RuntimeException("Error generando reporte: " + e.getMessage());
        }
    }

    

    /**
     * Crea la tabla estática de criterios según categoría
     */
    private Table crearTablaCriteriosEstatica(String nombreCategoria) {

        Table table = new Table(UnitValue.createPercentArray(new float[] { 4, 1 }))
                .useAllAvailableWidth()
                .setMarginBottom(6);

        // Color de fondo para encabezado
        DeviceRgb colorEncabezado = new DeviceRgb(41, 128, 185);

        // Encabezado
        table.addCell(crearCeldaEncabezado("CRITERIOS DE CALIFICACIÓN DE DANZAS", colorEncabezado));
        table.addCell(crearCeldaEncabezado("PUNTAJE", colorEncabezado));

        // Contenido según categoría
        if ("PALCO".equalsIgnoreCase(nombreCategoria)) {
            table.addCell(crearCeldaNormal("Coreografía y demostración en el palco"));
            table.addCell(crearCeldaNormal("35 pts", TextAlignment.CENTER));

            table.addCell(crearCeldaNormal("Vestuario, alegría e interpretación/escenografía"));
            table.addCell(crearCeldaNormal("15 pts", TextAlignment.CENTER));

            // Fila TOTAL
            table.addCell(crearCeldaNormal("TOTAL", true));
            table.addCell(crearCeldaNormal("50 pts", TextAlignment.CENTER, true));

        } else if ("RECORRIDO".equalsIgnoreCase(nombreCategoria)) {

            table.addCell(crearCeldaNormal("Alegría baile en el recorrido"));
            table.addCell(crearCeldaNormal("30 pts", TextAlignment.CENTER));

            // Fila TOTAL
            table.addCell(crearCeldaNormal("TOTAL", true));
            table.addCell(crearCeldaNormal("30 pts", TextAlignment.CENTER, true));

        } else if ("INICIO".equalsIgnoreCase(nombreCategoria)) {
            table.addCell(crearCeldaNormal("Puntualidad de salida y organización de los participantes"));
            table.addCell(crearCeldaNormal("10 pts", TextAlignment.CENTER));

            table.addCell(crearCeldaNormal("Disciplina en el recorrido y cuidado del medio ambiente"));
            table.addCell(crearCeldaNormal("10 pts", TextAlignment.CENTER));

            // Fila TOTAL
            table.addCell(crearCeldaNormal("TOTAL", true));
            table.addCell(crearCeldaNormal("20 pts", TextAlignment.CENTER, true));
        }

        return table;
    }

    /**
     * Crea la tabla de evaluación dinámica con los criterios de la rúbrica
     */
    private Table crearTablaEvaluacion(
            Participante participante,
            Rubrica rubrica,
            Evaluacion evaluacion) {

        List<RubricaCriterio> criterios = rubrica.getCriterios().stream()
                .filter(c -> "A".equals(c.getEstado()))
                .collect(Collectors.toList());

        // Crear tabla con columnas dinámicas
        float[] columnWidths = new float[criterios.size() + 2]; // Nombre + criterios + Total
        columnWidths[0] = 3; // Nombre de la danza
        for (int i = 1; i <= criterios.size(); i++) {
            columnWidths[i] = 1.5f; // Criterios
        }
        columnWidths[columnWidths.length - 1] = 1.5f; // Total

        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth();

        DeviceRgb colorEncabezado = new DeviceRgb(52, 152, 219);

        // Encabezado
        table.addCell(crearCeldaEncabezado("Nombre de la danza", colorEncabezado));

        for (RubricaCriterio criterio : criterios) {
            String titulo = criterio.getNombre() + "\n(" + criterio.getPorcentaje() + " pts)";
            table.addCell(crearCeldaEncabezado(titulo, colorEncabezado)
                    .setFontSize(9));
        }

        table.addCell(crearCeldaEncabezado("TOTAL", colorEncabezado));

        // Datos del participante
        table.addCell(crearCeldaNormal(participante.getNombre()));

        double total = 0;

        for (RubricaCriterio criterio : criterios) {
            double puntaje = 0;

            // Buscar el puntaje en los detalles de la evaluación
            if (evaluacion.getDetalles() != null) {
                for (EvaluacionDetalle detalle : evaluacion.getDetalles()) {
                    if (detalle.getRubricaCriterio() != null &&
                            detalle.getRubricaCriterio().getIdRubricaCriterio()
                                    .equals(criterio.getIdRubricaCriterio())) {
                        puntaje = detalle.getPuntaje();
                        break;
                    }
                }
            }

            total += puntaje;
            table.addCell(crearCeldaNormal(String.format("%.2f", puntaje), TextAlignment.CENTER));
        }

        table.addCell(crearCeldaNormal(String.format("%.2f", total), TextAlignment.CENTER, true)
                .setBackgroundColor(new DeviceRgb(241, 196, 15)));

        return table;
    }

    

    /**
     * Clase auxiliar para estructurar criterios evaluados
     */
    @Data
    @AllArgsConstructor
    private static class CriterioInfo {
        private String rubrica;
        private String nombre;
        private double puntaje;
    }

    // ═══════════════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES PARA CELDAS
    // ═══════════════════════════════════════════════════════════════════

    private Cell crearCeldaLabel(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setBold().setFontSize(7))
                .setBackgroundColor(new DeviceRgb(240, 240, 240))
                .setPadding(2)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
    }

    private Cell crearCeldaValor(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setFontSize(7))
                .setPadding(2)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
    }

    // Encabezados de tabla SIN COLOR (solo texto)
    private Cell crearCeldaEncabezadoSimple(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setBold().setFontSize(7))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(2)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    // Celdas normales SIN COLOR
private Cell crearCeldaNormalSimple(String texto) {
    return crearCeldaNormalSimple(texto, TextAlignment.LEFT);
}

private Cell crearCeldaNormalSimple(String texto, TextAlignment alineacion) {
    Paragraph p = new Paragraph(texto).setFontSize(7);
    return new Cell()
            .add(p)
            .setTextAlignment(alineacion)
            .setPadding(2)
            .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
            .setVerticalAlignment(VerticalAlignment.MIDDLE);
}


    /**
     * Crea tabla de resultados por categoría
     */
    private Table crearTablaResultadosCategoria(List<Inscripcion> inscripciones, Long idCategoria) {

        // Obtener jurados de esta categoría
        List<JuradoAsignacion> asignaciones = juradoAsignacionService
                .findByCategoriaActividad(idCategoria);

        int numJurados = asignaciones.size();

        // ═══════════════════════════════════════════════════════════
        // CALCULAR NÚMERO CORRECTO DE COLUMNAS
        // ═══════════════════════════════════════════════════════════
        // Estructura: # | Participante | Jurado1 | Jurado2 | ... | Promedio
        int numColumnas = 1 + 1 + numJurados + 1; // Puesto + Nombre + Jurados + Promedio

        float[] columnWidths = new float[numColumnas];
        columnWidths[0] = 0.6f; // #
        columnWidths[1] = 3f; // Participante

        // Columnas de jurados
        for (int i = 2; i < 2 + numJurados; i++) {
            columnWidths[i] = 1.3f;
        }

        columnWidths[numColumnas - 1] = 1.3f; // Promedio

        Table table = new Table(UnitValue.createPercentArray(columnWidths))
                .useAllAvailableWidth();

        DeviceRgb colorEncabezado = new DeviceRgb(41, 128, 185);

        // ═══════════════════════════════════════════════════════════
        // FILA 1: ENCABEZADOS
        // ═══════════════════════════════════════════════════════════

        // Columna 1: "#"
        table.addCell(crearCeldaEncabezado("#", colorEncabezado));

        // Columna 2: "Participante"
        table.addCell(crearCeldaEncabezado("Participante", colorEncabezado));

        // Columnas 3 a N: Nombres de jurados
        for (JuradoAsignacion asignacion : asignaciones) {
            String nombre = asignacion.getJurado().getPersona().getNombres();
            table.addCell(crearCeldaEncabezado(nombre, colorEncabezado)
                    .setFontSize(9));
        }

        // Última columna: "Promedio"
        table.addCell(crearCeldaEncabezado("Promedio", colorEncabezado));

        // ═══════════════════════════════════════════════════════════
        // ORDENAR PARTICIPANTES POR PROMEDIO
        // ═══════════════════════════════════════════════════════════

        List<Inscripcion> ordenadas = inscripciones.stream()
                .sorted((a, b) -> {
                    double promedioA = calcularPromedioParticipante(
                            a.getParticipante().getIdParticipante(), idCategoria);
                    double promedioB = calcularPromedioParticipante(
                            b.getParticipante().getIdParticipante(), idCategoria);
                    return Double.compare(promedioB, promedioA);
                })
                .collect(Collectors.toList());

        // ═══════════════════════════════════════════════════════════
        // FILAS DE DATOS - UNA FILA POR PARTICIPANTE
        // ═══════════════════════════════════════════════════════════

        int puesto = 1;

        for (Inscripcion inscripcion : ordenadas) {
            Participante p = inscripcion.getParticipante();

            // Columna 1: Puesto
            table.addCell(crearCeldaNormal(String.valueOf(puesto), TextAlignment.CENTER));

            // Columna 2: Nombre del participante
            table.addCell(crearCeldaNormal(p.getNombre()));

            // Variables para calcular promedio
            double suma = 0;
            int count = 0;

            // Columnas 3 a N: Puntajes de cada jurado
            for (JuradoAsignacion asignacion : asignaciones) {
                Evaluacion eval = evaluacionService.findByJuradoAndParticipanteAndCategoria(
                        asignacion.getJurado().getIdJurado(),
                        p.getIdParticipante(),
                        idCategoria);

                if (eval != null) {
                    double puntaje = eval.getTotalPonderacion();
                    suma += puntaje;
                    count++;
                    table.addCell(crearCeldaNormal(
                            String.format("%.2f", puntaje),
                            TextAlignment.CENTER));
                } else {
                    table.addCell(crearCeldaNormal("—", TextAlignment.CENTER)
                            .setFontColor(ColorConstants.GRAY));
                }
            }

            // Última columna: Promedio
            double promedio = count > 0 ? suma / count : 0;
            table.addCell(crearCeldaNormal(
                    String.format("%.2f", promedio),
                    TextAlignment.CENTER,
                    true).setBackgroundColor(new DeviceRgb(241, 196, 15)));

            puesto++;
        }

        return table;
    }

    /**
     * Crea tabla de resultados totales
     */
    private Table crearTablaResultadosTotal(
            Map<Long, List<Inscripcion>> porParticipante,
            Long idActividad) {

        Table table = new Table(UnitValue.createPercentArray(new float[] { 0.8f, 3f, 1.5f, 1.5f, 1.5f, 1.5f }))
                .useAllAvailableWidth();

        DeviceRgb colorEncabezado = new DeviceRgb(39, 174, 96);

        // Encabezado
        table.addCell(crearCeldaEncabezado("#", colorEncabezado));
        table.addCell(crearCeldaEncabezado("Participante", colorEncabezado));
        table.addCell(crearCeldaEncabezado("Inicio", colorEncabezado));
        table.addCell(crearCeldaEncabezado("Recorrido", colorEncabezado));
        table.addCell(crearCeldaEncabezado("Palco", colorEncabezado));
        table.addCell(crearCeldaEncabezado("TOTAL", colorEncabezado));

        // Calcular totales y ordenar
        List<Map.Entry<Long, List<Inscripcion>>> ordenado = porParticipante.entrySet().stream()
                .sorted((a, b) -> {
                    double totalA = calcularPuntajeTotal(a.getValue());
                    double totalB = calcularPuntajeTotal(b.getValue());
                    return Double.compare(totalB, totalA);
                })
                .collect(Collectors.toList());

        // Datos
        int puesto = 1;
        for (Map.Entry<Long, List<Inscripcion>> entry : ordenado) {
            List<Inscripcion> inscripciones = entry.getValue();
            Participante p = inscripciones.get(0).getParticipante();

            table.addCell(crearCeldaNormal(String.valueOf(puesto), TextAlignment.CENTER));
            table.addCell(crearCeldaNormal(p.getNombre()));

            double puntajeInicio = 0;
            double puntajeRecorrido = 0;
            double puntajePalco = 0;

            for (Inscripcion insc : inscripciones) {
                CategoriaActividad cat = insc.getCategoriaActividad();
                double promedio = calcularPromedioParticipante(p.getIdParticipante(), cat.getIdCategoriaActividad());

                if ("INICIO".equalsIgnoreCase(cat.getNombre())) {
                    puntajeInicio = promedio;
                } else if ("RECORRIDO".equalsIgnoreCase(cat.getNombre())) {
                    puntajeRecorrido = promedio;
                } else if ("PALCO".equalsIgnoreCase(cat.getNombre())) {
                    puntajePalco = promedio;
                }
            }

            table.addCell(crearCeldaNormal(String.format("%.2f", puntajeInicio), TextAlignment.CENTER));
            table.addCell(crearCeldaNormal(String.format("%.2f", puntajeRecorrido), TextAlignment.CENTER));
            table.addCell(crearCeldaNormal(String.format("%.2f", puntajePalco), TextAlignment.CENTER));

            double total = puntajeInicio + puntajeRecorrido + puntajePalco;
            table.addCell(crearCeldaNormal(String.format("%.2f", total), TextAlignment.CENTER, true)
                    .setBackgroundColor(new DeviceRgb(241, 196, 15)));

            puesto++;
        }

        return table;
    }

    /**
     * Calcula el promedio de un participante en una categoría
     */
    private double calcularPromedioParticipante(Long idParticipante, Long idCategoria) {
        List<Evaluacion> evaluaciones = evaluacionService
                .findByParticipanteAndCategoria(idParticipante, idCategoria);

        if (evaluaciones.isEmpty()) {
            return 0;
        }

        double suma = evaluaciones.stream()
                .mapToDouble(Evaluacion::getTotalPonderacion)
                .sum();

        return suma / evaluaciones.size();
    }

    /**
     * Calcula el puntaje total de un participante (todas las categorías)
     */
    private double calcularPuntajeTotal(List<Inscripcion> inscripciones) {
        double total = 0;

        for (Inscripcion insc : inscripciones) {
            total += calcularPromedioParticipante(
                    insc.getParticipante().getIdParticipante(),
                    insc.getCategoriaActividad().getIdCategoriaActividad());
        }

        return total;
    }

    /**
     * Agrega pie de página
     */
    private void agregarPieDePagina(Document document) {
        document.add(new Paragraph()
                .setMarginTop(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(9)
                .setItalic()
                .add("Documento generado automáticamente - " +
                        java.time.LocalDateTime.now().format(
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
    }

    /**
     * ═══════════════════════════════════════════════════════════════════
     * MÉTODOS HELPER PARA CELDAS
     * ═══════════════════════════════════════════════════════════════════
     */

    private Cell crearCeldaEncabezado(String texto, DeviceRgb color) {
        return new Cell()
                .add(new Paragraph(texto).setBold().setFontSize(7).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(color)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(2)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    private Cell crearCeldaNormal(String texto) {
        return crearCeldaNormal(texto, TextAlignment.LEFT, false);
    }

    private Cell crearCeldaNormal(String texto, TextAlignment alineacion) {
        return crearCeldaNormal(texto, alineacion, false);
    }

    private Cell crearCeldaNormal(String texto, boolean negrita) {
        return crearCeldaNormal(texto, TextAlignment.LEFT, negrita);
    }

    private Cell crearCeldaNormal(String texto, TextAlignment alineacion, boolean negrita) {
        Paragraph p = new Paragraph(texto).setFontSize(7);
        if (negrita) {
            p.setBold();
        }
        return new Cell()
                .add(p)
                .setTextAlignment(alineacion)
                .setPadding(2)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    /**
     * Genera reporte COMPLETO de un participante (todas las categorías)
     */
    public byte[] generarReporteCompletoParticipante(Long idActividad, Long idParticipante) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

            Document document = new Document(pdfDoc);
            document.setMargins(30, 40, 30, 40);

            // Obtener datos
            Participante participante = participanteService.findById(idParticipante);
            Actividad actividad = actividadService.findById(idActividad);

            if (participante == null || actividad == null) {
                throw new RuntimeException("Datos incompletos");
            }

            // Encabezado
            agregarEncabezado(document, actividad);

            // Título
            document.add(new Paragraph("REPORTE COMPLETO DE EVALUACIONES")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(2));

            document.add(new Paragraph("PARTICIPANTE: " + participante.getNombre().toUpperCase())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setBold()
                    .setMarginBottom(5));

            // Obtener todas las categorías de la actividad
            List<CategoriaActividad> categorias = categoriaService.findByActividad(idActividad);

            double totalGeneral = 0;
            int categoriasEvaluadas = 0;

            // Por cada categoría, mostrar evaluaciones
            for (int i = 0; i < categorias.size(); i++) {
                CategoriaActividad categoria = categorias.get(i);

                if (i > 0) {
                    document.add(new Paragraph()
                            .setBorderTop(new SolidBorder(1))
                            .setMarginTop(5)
                            .setMarginBottom(5));
                }

                // Título de categoría
                document.add(new Paragraph("CATEGORÍA: " + categoria.getNombre().toUpperCase())
                        .setFontSize(10)
                        .setBold()
                        .setBackgroundColor(new DeviceRgb(52, 152, 219))
                        .setFontColor(ColorConstants.WHITE)
                        .setPadding(2)
                        .setMarginBottom(2));

                // Obtener evaluaciones en esta categoría
                List<Evaluacion> evaluaciones = evaluacionService
                        .findByParticipanteAndCategoria(idParticipante, categoria.getIdCategoriaActividad());

                if (evaluaciones.isEmpty()) {
                    document.add(new Paragraph("Sin evaluaciones registradas en esta categoría")
                            .setItalic()
                            .setFontColor(ColorConstants.GRAY)
                            .setMarginBottom(10));
                    continue;
                }

                // ═══════════════════════════════════════════════════════════
                // CAMBIO: Obtener TODAS las rúbricas de la categoría
                // ═══════════════════════════════════════════════════════════
                List<Rubrica> rubricas = rubricaService.findByCategoria(categoria.getIdCategoriaActividad());

                if (rubricas == null || rubricas.isEmpty()) {
                    document.add(new Paragraph("No hay rúbricas definidas para esta categoría")
                            .setItalic()
                            .setFontColor(ColorConstants.GRAY)
                            .setMarginBottom(10));
                    continue;
                }

                // ═══════════════════════════════════════════════════════════
                // RECOPILAR TODOS LOS CRITERIOS DE TODAS LAS RÚBRICAS
                // ═══════════════════════════════════════════════════════════
                List<RubricaCriterio> todosCriterios = new ArrayList<>();
                Map<Long, String> criterioARubricaNombre = new HashMap<>();

                for (Rubrica rubrica : rubricas) {
                    List<RubricaCriterio> criteriosDeRubrica = rubrica.getCriterios().stream()
                            .filter(c -> "A".equals(c.getEstado()))
                            .collect(Collectors.toList());

                    todosCriterios.addAll(criteriosDeRubrica);

                    for (RubricaCriterio criterio : criteriosDeRubrica) {
                        criterioARubricaNombre.put(criterio.getIdRubricaCriterio(), rubrica.getNombre());
                    }
                }

                if (todosCriterios.isEmpty()) {
                    document.add(new Paragraph("No hay criterios activos en las rúbricas")
                            .setItalic()
                            .setFontColor(ColorConstants.GRAY)
                            .setMarginBottom(10));
                    continue;
                }

                // ═══════════════════════════════════════════════════════════
                // CREAR TABLA
                // ═══════════════════════════════════════════════════════════

                int numColumnas = 1 + todosCriterios.size() + 1;
                float[] columnWidths = new float[numColumnas];
                columnWidths[0] = 2.5f;
                for (int j = 1; j <= todosCriterios.size(); j++) {
                    columnWidths[j] = 1.2f;
                }
                columnWidths[numColumnas - 1] = 1.3f;

                Table table = new Table(UnitValue.createPercentArray(columnWidths))
                        .useAllAvailableWidth();

                DeviceRgb colorEncabezado = new DeviceRgb(41, 128, 185);

                // Encabezados
                table.addCell(crearCeldaEncabezado("Jurado", colorEncabezado));

                for (RubricaCriterio criterio : todosCriterios) {
                    String titulo;
                    if (rubricas.size() > 1) {
                        String nombreRubrica = criterioARubricaNombre.get(criterio.getIdRubricaCriterio());
                        titulo = nombreRubrica + "\n" + criterio.getNombre() + "\n(" + criterio.getPorcentaje()
                                + " pts)";
                    } else {
                        titulo = criterio.getNombre() + "\n(" + criterio.getPorcentaje() + " pts)";
                    }

                    table.addCell(crearCeldaEncabezado(titulo, colorEncabezado)
                            .setFontSize(6));
                }

                table.addCell(crearCeldaEncabezado("TOTAL", colorEncabezado));

                // Datos de cada jurado
                double sumaPromedios = 0;

                for (Evaluacion evaluacion : evaluaciones) {
                    Jurado jurado = evaluacion.getJurado();

                    table.addCell(crearCeldaNormal(jurado.getPersona().getNombreCompleto()));

                    double totalJurado = 0;

                    for (RubricaCriterio criterio : todosCriterios) {
                        double puntaje = 0;

                        if (evaluacion.getDetalles() != null) {
                            for (EvaluacionDetalle detalle : evaluacion.getDetalles()) {
                                if (detalle.getRubricaCriterio() != null &&
                                        detalle.getRubricaCriterio().getIdRubricaCriterio()
                                                .equals(criterio.getIdRubricaCriterio())) {
                                    puntaje = detalle.getPuntaje();
                                    break;
                                }
                            }
                        }

                        totalJurado += puntaje;
                        table.addCell(crearCeldaNormal(String.format("%.2f", puntaje), TextAlignment.CENTER));
                    }

                    table.addCell(crearCeldaNormal(String.format("%.2f", totalJurado),
                            TextAlignment.CENTER, true)
                            .setBackgroundColor(new DeviceRgb(241, 196, 15)));

                    sumaPromedios += totalJurado;
                }

                // Fila de promedio
                double promedioCategoria = evaluaciones.size() > 0 ? sumaPromedios / evaluaciones.size() : 0;

                Cell cellPromedio = new Cell(1, numColumnas - 1)
                        .add(new Paragraph("PROMEDIO EN ESTA CATEGORÍA").setBold().setFontSize(8))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBackgroundColor(new DeviceRgb(46, 204, 113))
                        .setFontColor(ColorConstants.WHITE)
                        .setPadding(4)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE);
                table.addCell(cellPromedio);

                table.addCell(crearCeldaNormal(String.format("%.2f", promedioCategoria),
                        TextAlignment.CENTER, true)
                        .setBackgroundColor(new DeviceRgb(39, 174, 96))
                        .setFontColor(ColorConstants.WHITE)
                        .setFontSize(10)
                        .setPadding(2));

                document.add(table);

                totalGeneral += promedioCategoria;
                categoriasEvaluadas++;
            }

            // Total general final
            if (categoriasEvaluadas > 0) {
                document.add(new Paragraph()
                        .setBorderTop(new SolidBorder(2))
                        .setMarginTop(10)
                        .setMarginBottom(5));

                Table tablaTotalGeneral = new Table(UnitValue.createPercentArray(new float[] { 3, 1 }))
                        .useAllAvailableWidth();

                tablaTotalGeneral.addCell(new Cell()
                        .add(new Paragraph("PUNTAJE TOTAL FINAL").setBold().setFontSize(12))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBackgroundColor(new DeviceRgb(231, 76, 60))
                        .setFontColor(ColorConstants.WHITE)
                        .setPadding(5)
                        .setBorder(Border.NO_BORDER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));

                tablaTotalGeneral.addCell(new Cell()
                        .add(new Paragraph(String.format("%.2f", totalGeneral))
                                .setBold()
                                .setFontSize(14))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(new DeviceRgb(192, 57, 43))
                        .setFontColor(ColorConstants.WHITE)
                        .setPadding(5)
                        .setBorder(Border.NO_BORDER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));

                document.add(tablaTotalGeneral);
            }

            // Pie de página
            agregarPieDePagina(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generando reporte completo de participante", e);
            throw new RuntimeException("Error generando reporte: " + e.getMessage());
        }
    }
}