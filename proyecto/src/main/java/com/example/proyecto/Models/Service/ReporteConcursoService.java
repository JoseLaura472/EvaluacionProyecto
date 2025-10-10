package com.example.proyecto.Models.Service;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyecto.Models.Entity.Evaluacion;
import com.example.proyecto.Models.Entity.EvaluacionDetalle;
import com.example.proyecto.Models.Entity.Persona;
import com.example.proyecto.Models.Entity.RubricaCriterio;
import com.example.proyecto.Models.IService.IEvaluacionService;
import com.example.proyecto.Models.IService.IInscripcionService;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteConcursoService {

    private final IInscripcionService inscripcionRepo;
    private final IEvaluacionService evaluacionRepo;

    @Transactional(readOnly = true)
    public byte[] generarReporteInscripcionPdf(Long actId, Long inscId) {

        var insc = inscripcionRepo.fetchFull(actId, inscId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));

        var evals = evaluacionRepo.findFullByActividadAndInscripcion(actId, inscId);

        // ---- iText: construir PDF en memoria ----
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        // Cabecera
        String titulo = "Reporte de Evaluación - " + (insc.getActividad().getNombre());
        doc.add(new Paragraph(titulo).setBold().setFontSize(14));
        doc.add(new Paragraph(
                "Categoría: " + insc.getCategoriaActividad().getNombre() + "\n" +
                        "Participante: " + insc.getParticipante().getNombre() + "\n" +
                        "Institución: " + (insc.getParticipante().getInstitucion() == null ? "—"
                                : insc.getParticipante().getInstitucion()))
                .setMarginBottom(10));

        // Resumen: promedio global del participante (con las evaluaciones presentes)
        double promedioGlobal = 0;
        if (!evals.isEmpty()) {
            promedioGlobal = evals.stream()
                    .mapToDouble(Evaluacion::getTotalPonderacion)
                    .average().orElse(0);
        }
        doc.add(new Paragraph(String.format("Promedio Global: %.2f", promedioGlobal))
                .setBold().setFontSize(11).setMarginBottom(8));

        // Para cada jurado, una tabla de criterios
        for (Evaluacion e : evals) {
            String nombreJurado = Optional.ofNullable(e.getJurado().getPersona())
                    .map(Persona::getNombres).orElse("Jurado #" + idJuradoSafe(e));
            doc.add(new Paragraph("Jurado: " + nombreJurado)
                    .setBold().setFontSize(11).setMarginTop(10).setMarginBottom(4));

            Table t = new Table(new float[] { 4, 2, 2, 2 }).useAllAvailableWidth();
            t.addHeaderCell(new Cell().add(new Paragraph("Criterio").setBold()));
            t.addHeaderCell(new Cell().add(new Paragraph("%").setBold()));
            t.addHeaderCell(new Cell().add(new Paragraph("Puntaje").setBold()));
            t.addHeaderCell(new Cell().add(new Paragraph("Ponderado").setBold()));

            // si tu Evaluacion tiene getDetalles() (List<EvaluacionDetalle>)
            var detalles = Optional.ofNullable(e.getDetalles()).orElse(List.of());
            detalles.sort(Comparator.comparing(d -> d.getRubricaCriterio().getIdRubricaCriterio()));

            for (EvaluacionDetalle d : detalles) {
                String crit = Optional.ofNullable(d.getRubricaCriterio()).map(RubricaCriterio::getNombre).orElse("—");
                int porc = d.getPorcentaje();
                double puntaje = Optional.ofNullable(d.getPuntaje()).orElse(0.0);
                Double pondObj = d.getPonderado();
                double pond = (pondObj != null)
                        ? pondObj.doubleValue()
                        : puntaje * (porc / 100.0);


                t.addCell(new Cell().add(new Paragraph(crit)));
                t.addCell(new Cell().add(new Paragraph(String.valueOf(porc))));
                t.addCell(new Cell().add(new Paragraph(String.format("%.2f", puntaje))));
                t.addCell(new Cell().add(new Paragraph(String.format("%.2f", pond))));
            }

            // Fila total del jurado
            t.addCell(new Cell(1, 3).add(new Paragraph("TOTAL JURADO").setBold())
                    .setTextAlignment(TextAlignment.RIGHT));
            t.addCell(new Cell().add(new Paragraph(String.format("%.2f", e.getTotalPonderacion()))
                    .setBold()));

            doc.add(t);
        }

        // Pie
        doc.add(new Paragraph("Generado: " + java.time.LocalDateTime.now())
                .setFontSize(9).setFontColor(new DeviceRgb(120, 120, 120)).setMarginTop(15));

        doc.close();
        return baos.toByteArray();
    }

    // Utilidad para id de jurado (si el getter es distinto)
    private Long idJuradoSafe(Evaluacion e) {
        try {
            // si tu entidad tiene getIdJurado():
            return e.getJurado().getIdJurado();
        } catch (Exception ex) {
            // si solo hay getId_jurado():
            try {
                var m = e.getJurado().getClass().getMethod("getId_jurado");
                return (Long) m.invoke(e.getJurado());
            } catch (Exception ignore) {
                return -1L;
            }
        }
    }
}
