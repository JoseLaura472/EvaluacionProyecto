package com.example.proyecto.Controllers.Api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyecto.Models.Service.ReporteConcursoService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/concurso")
@RequiredArgsConstructor
public class ReporteConcursoApi {

    private final ReporteConcursoService reporteService;

    @GetMapping(value = "/{actId}/reporte/inscripcion/{inscId}.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> reporteInscripcion(@PathVariable Long actId,
            @PathVariable Long inscId,
            HttpSession session) {
        // seguridad simple: validar sesión admin (ajusta a tu lógica)
        if (session.getAttribute("usuario") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        byte[] pdf = reporteService.generarReporteInscripcionPdf(actId, inscId);
        String filename = String.format("Reporte_%d_%d.pdf", actId, inscId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}
