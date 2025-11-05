package com.example.proyecto.Models.Service.report.EntradaUniversitaria;

import java.io.File;
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
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

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

    /**
     * Genera reporte individual por participante y categoría
     * Un PDF por cada jurado que evaluó
     */
    public byte[] generarReportePorParticipanteCategoria(
            Long idParticipante, 
            Long idCategoria) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            
            // Configurar página horizontal
            pdfDoc.setDefaultPageSize(PageSize.LETTER.rotate());
            
            Document document = new Document(pdfDoc);
            document.setMargins(30, 40, 30, 40);

            // Obtener datos
            Participante participante = participanteService.findById(idParticipante);
            CategoriaActividad categoria = categoriaService.findById(idCategoria);
            Rubrica rubrica = rubricaService.findByCategoria(idCategoria);
            
            if (participante == null || categoria == null || rubrica == null) {
                throw new RuntimeException("Datos incompletos para generar reporte");
            }

            Actividad actividad = categoria.getActividad();
            
            // Obtener evaluaciones de este participante en esta categoría
            List<Evaluacion> evaluaciones = evaluacionService
                    .findByParticipanteAndCategoria(idParticipante, idCategoria);

            if (evaluaciones.isEmpty()) {
                // Página indicando que no hay evaluaciones
                agregarEncabezado(document, actividad);
                document.add(new Paragraph("No hay evaluaciones registradas para este participante en esta categoría.")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(100)
                        .setFontSize(14));
            } else {
                // Generar una página por cada jurado
                for (int i = 0; i < evaluaciones.size(); i++) {
                    Evaluacion evaluacion = evaluaciones.get(i);
                    
                    if (i > 0) {
                        document.add(new AreaBreak());
                    }
                    
                    generarPaginaEvaluacion(
                            document, 
                            actividad, 
                            categoria, 
                            participante, 
                            rubrica, 
                            evaluacion
                    );
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
     * Genera reporte total de una categoría (todos los participantes)
     */
    public byte[] generarReportePorCategoria(Long idActividad, Long idCategoria) {
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
            
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
            
            document.add(new Paragraph("REPORTE GENERAL - CATEGORÍA: " + categoria.getNombre().toUpperCase())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16)
                    .setBold()
                    .setMarginBottom(20));

            // Obtener inscripciones de esta categoría
            List<Inscripcion> inscripciones = inscripcionService
                    .findByActividad_IdActividadAndCategoriaActividad_IdCategoriaActividad(idActividad, idCategoria);

            // Tabla de resultados
            Table table = crearTablaResultadosCategoria(inscripciones, idCategoria);
            document.add(table);

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
            
            // Agrupar por participante y sumar puntajes
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
     * ═══════════════════════════════════════════════════════════════════
     * MÉTODOS AUXILIARES
     * ═══════════════════════════════════════════════════════════════════
     */

    /**
     * Genera una página de evaluación individual
     */
    private void generarPaginaEvaluacion(
            Document document,
            Actividad actividad,
            CategoriaActividad categoria,
            Participante participante,
            Rubrica rubrica,
            Evaluacion evaluacion) {

        // Encabezado
        agregarEncabezado(document, actividad);

        // Título "FORMULARIO DE EVALUACIÓN DE:"
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

        // Tabla de evaluación dinámica
        Table tablaEvaluacion = crearTablaEvaluacion(participante, rubrica, evaluacion);
        document.add(tablaEvaluacion);

        // Nota
        document.add(new Paragraph("Nota: Formulario de evaluación sobre 50 puntos")
                .setFontSize(10)
                .setItalic()
                .setMarginTop(10));

        // Firmas
        agregarSeccionFirmas(document, evaluacion.getJurado());
    }

    /**
     * Agrega el encabezado con logos y título
     */
    private void agregarEncabezado(Document document, Actividad actividad) {
        
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(10);

        // Logo izquierdo
        Cell cellLogoIzq = new Cell()
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.RIGHT);
        
        if (logoIzquierdoPath != null && !logoIzquierdoPath.isEmpty() && new File(logoIzquierdoPath).exists()) {
            try {
                ImageData imageData = ImageDataFactory.create(logoIzquierdoPath);
                Image logo = new Image(imageData).setWidth(80).setHeight(80);
                cellLogoIzq.add(logo);
            } catch (Exception e) {
                log.warn("No se pudo cargar logo izquierdo: {}", e.getMessage());
            }
        }
        headerTable.addCell(cellLogoIzq);

        // Título central
        Cell cellTitulo = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        
        cellTitulo.add(new Paragraph(actividad.getNombre().toUpperCase())
                .setBold()
                .setFontSize(12)
                .setMarginBottom(5));
        
        if (actividad.getDescripcion() != null && !actividad.getDescripcion().isEmpty()) {
            cellTitulo.add(new Paragraph(actividad.getDescripcion())
                    .setFontSize(9)
                    .setItalic());
        }
        
        headerTable.addCell(cellTitulo);

        // Logo derecho
        Cell cellLogoDer = new Cell()
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.RIGHT);
        
        if (logoDerechoPath != null && !logoDerechoPath.isEmpty() && new File(logoDerechoPath).exists()) {
            try {
                ImageData imageData = ImageDataFactory.create(logoDerechoPath);
                Image logo = new Image(imageData).setWidth(80).setHeight(80);
                cellLogoDer.add(logo);
            } catch (Exception e) {
                log.warn("No se pudo cargar logo derecho: {}", e.getMessage());
            }
        }
        headerTable.addCell(cellLogoDer);

        document.add(headerTable);
        
        // Línea separadora
        document.add(new Paragraph().setBorderBottom(new SolidBorder(1)).setMarginBottom(12));
    }

    /**
     * Crea la tabla estática de criterios según categoría
     */
    private Table crearTablaCriteriosEstatica(String nombreCategoria) {
        
        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 1}))
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
            String titulo = criterio.getNombre() + "\n(" + criterio.getMaxPuntos() + " pts)";
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
                        detalle.getRubricaCriterio().getIdRubricaCriterio().equals(criterio.getIdRubricaCriterio())) {
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
     * Agrega la sección de firmas
     */
    private void agregarSeccionFirmas(Document document, Jurado jurado) {
        
        Table firmasTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginTop(30);

        // Columna izquierda: Jurado
        Cell cellJurado = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);
        
        cellJurado.add(new Paragraph("_______________________________")
                .setMarginBottom(5));
        cellJurado.add(new Paragraph(jurado.getPersona().getNombreCompleto())
                .setBold()
                .setFontSize(11));
        cellJurado.add(new Paragraph("JURADO")
                .setFontSize(10));
        
        firmasTable.addCell(cellJurado);

        // Columna derecha: Notario
        Cell cellNotario = new Cell()
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);
        
        cellNotario.add(new Paragraph("_______________________________")
                .setMarginBottom(5));
        cellNotario.add(new Paragraph("Firma y Sello")
                .setFontSize(10));
        cellNotario.add(new Paragraph("NOTARIO")
                .setBold()
                .setFontSize(11));
        
        firmasTable.addCell(cellNotario);

        document.add(firmasTable);
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
        columnWidths[1] = 3f;   // Participante
        
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
                        idCategoria
                );
                
                if (eval != null) {
                    double puntaje = eval.getTotalPonderacion();
                    suma += puntaje;
                    count++;
                    table.addCell(crearCeldaNormal(
                        String.format("%.2f", puntaje), 
                        TextAlignment.CENTER
                    ));
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
                true
            ).setBackgroundColor(new DeviceRgb(241, 196, 15)));
            
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

        Table table = new Table(UnitValue.createPercentArray(new float[]{0.8f, 3f, 1.5f, 1.5f, 1.5f, 1.5f}))
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
                    insc.getCategoriaActividad().getIdCategoriaActividad()
            );
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
                              java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                      )));
    }

    /**
     * ═══════════════════════════════════════════════════════════════════
     * MÉTODOS HELPER PARA CELDAS
     * ═══════════════════════════════════════════════════════════════════
     */

    private Cell crearCeldaEncabezado(String texto, DeviceRgb color) {
        return new Cell()
                .add(new Paragraph(texto).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(color)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(
                    VerticalAlignment.MIDDLE)
                .setPadding(4)
                .setFontSize(10);
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
        Paragraph p = new Paragraph(texto)
                .setFontSize(10);
        
        if (negrita) {
            p.setBold();
        }
        
        return new Cell()
                .add(p)
                .setTextAlignment(alineacion)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(6);
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

                // Obtener rúbrica
                Rubrica rubrica = rubricaService.findByCategoria(categoria.getIdCategoriaActividad());
                
                if (rubrica == null) {
                    continue;
                }

                // Tabla de evaluaciones de jurados
                List<RubricaCriterio> criterios = rubrica.getCriterios().stream()
                        .filter(c -> "A".equals(c.getEstado()))
                        .collect(Collectors.toList());

                // ═══════════════════════════════════════════════════════════
                // CREAR TABLA CON ESTRUCTURA CORRECTA
                // ═══════════════════════════════════════════════════════════
                
                // Ancho de columnas: Jurado + Criterios + Total
                int numColumnas = 1 + criterios.size() + 1;
                float[] columnWidths = new float[numColumnas];
                columnWidths[0] = 2.5f; // Columna Jurado (más ancha)
                for (int j = 1; j <= criterios.size(); j++) {
                    columnWidths[j] = 1.2f; // Columnas de criterios
                }
                columnWidths[numColumnas - 1] = 1.3f; // Columna Total

                Table table = new Table(UnitValue.createPercentArray(columnWidths))
                        .useAllAvailableWidth();

                DeviceRgb colorEncabezado = new DeviceRgb(41, 128, 185);

                // ═══════════════════════════════════════════════════════════
                // FILA 1: ENCABEZADOS
                // ═══════════════════════════════════════════════════════════
                
                // Columna 1: "Jurado"
                table.addCell(crearCeldaEncabezado("Jurado", colorEncabezado));
                
                // Columnas 2 a N: Criterios
                for (RubricaCriterio criterio : criterios) {
                    String titulo = criterio.getNombre() + "\n(" + criterio.getMaxPuntos() + " pts)";
                    table.addCell(crearCeldaEncabezado(titulo, colorEncabezado)
                            .setFontSize(7));
                }
                
                // Última columna: "TOTAL"
                table.addCell(crearCeldaEncabezado("TOTAL", colorEncabezado));

                // ═══════════════════════════════════════════════════════════
                // FILAS 2 a N: DATOS DE CADA JURADO
                // ═══════════════════════════════════════════════════════════
                
                double sumaPromedios = 0;
                
                for (Evaluacion evaluacion : evaluaciones) {
                    Jurado jurado = evaluacion.getJurado();
                    
                    // Columna 1: Nombre del jurado
                    table.addCell(crearCeldaNormal(jurado.getPersona().getNombreCompleto()));

                    double totalJurado = 0;
                    
                    // Columnas 2 a N: Puntajes por criterio
                    for (RubricaCriterio criterio : criterios) {
                        double puntaje = 0;
                        
                        // Buscar el puntaje de este criterio en los detalles
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

                    // Última columna: Total del jurado
                    table.addCell(crearCeldaNormal(String.format("%.2f", totalJurado), 
                            TextAlignment.CENTER, true)
                            .setBackgroundColor(new DeviceRgb(241, 196, 15)));
                    
                    sumaPromedios += totalJurado;
                }

                // ═══════════════════════════════════════════════════════════
                // ÚLTIMA FILA: PROMEDIO DE LA CATEGORÍA
                // ═══════════════════════════════════════════════════════════
                
                double promedioCategoria = evaluaciones.size() > 0 ? 
                        sumaPromedios / evaluaciones.size() : 0;
                
                // Celda que ocupa desde columna 1 hasta la penúltima (antes de Total)
                Cell cellPromedio = new Cell(1, numColumnas - 1)
                        .add(new Paragraph("PROMEDIO EN ESTA CATEGORÍA").setBold().setFontSize(8))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBackgroundColor(new DeviceRgb(46, 204, 113))
                        .setFontColor(ColorConstants.WHITE)
                        .setPadding(4)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE);
                table.addCell(cellPromedio);
                
                // Última celda: El valor del promedio
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

            // ═══════════════════════════════════════════════════════════
            // TOTAL GENERAL FINAL
            // ═══════════════════════════════════════════════════════════
            
            if (categoriasEvaluadas > 0) {
                document.add(new Paragraph()
                        .setBorderTop(new SolidBorder(2))
                        .setMarginTop(10)
                        .setMarginBottom(5));
                
                Table tablaTotalGeneral = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
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