package com.example.proyecto.Controllers.Api;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.proyecto.Models.Service.ConcursoDashboardService;
import com.example.proyecto.Models.Service.SseHub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/concurso")
@RequiredArgsConstructor
@Slf4j
public class ConcursoDashboardApi {
    private final ConcursoDashboardService dashService;
    private final SseHub sseHub; // ver sección 3

    /**
     * Snapshot mejorado con soporte para vista TOTAL
     */
    @GetMapping("/{idActividad}/snapshot")
    public ResponseEntity<Map<String, Object>> getSnapshot(
            @PathVariable Long idActividad,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false, defaultValue = "false") boolean vistaTotal) {
        
        try {
            Map<String, Object> data;
            
            if (vistaTotal) {
                // Vista TOTAL: suma de todas las categorías
                data = dashService.getSnapshotTotal(idActividad);
            } else if (categoriaId != null) {
                // Vista filtrada por categoría específica
                data = dashService.getSnapshotPorCategoria(idActividad, categoriaId);
            } else {
                // Vista por defecto (todas las categorías sin filtrar)
                data = dashService.getSnapshotCompleto(idActividad);
            }
            
            return ResponseEntity.ok(data);
            
        } catch (Exception e) {
            log.error("Error en snapshot de actividad {}", idActividad, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Stream SSE para actualizaciones en tiempo real
     * ✅ CORREGIDO: Usando el método subscribe de tu SseHub
     */
    @GetMapping(value = "/{idActividad}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable Long idActividad) {
        log.info("Cliente conectado a stream de actividad {}", idActividad);
        return sseHub.subscribe(idActividad);  // ← USAR subscribe en lugar de register
    }
    
    /**
     * Generar reporte PDF por categoría
     */
    @GetMapping("/{idActividad}/reporte/categoria/{categoriaId}.pdf")
    public ResponseEntity<byte[]> reporteCategoria(
            @PathVariable Long idActividad,
            @PathVariable Long categoriaId) {
        
        try {
            byte[] pdf = dashService.generarReportePorCategoria(idActividad, categoriaId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte-categoria-" + categoriaId + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
                    
        } catch (Exception e) {
            log.error("Error generando reporte de categoría", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Generar reporte PDF TOTAL (todas las categorías sumadas)
     */
    @GetMapping("/{idActividad}/reporte/total.pdf")
    public ResponseEntity<byte[]> reporteTotal(@PathVariable Long idActividad) {
        
        try {
            byte[] pdf = dashService.generarReporteTotal(idActividad);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte-total-actividad-" + idActividad + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
                    
        } catch (Exception e) {
            log.error("Error generando reporte total", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generar reporte PDF por participante y categoría
     * Genera un PDF con todas las evaluaciones de los jurados para ese participante
     */
    @GetMapping("/{idActividad}/reporte/participante/{idParticipante}/categoria/{categoriaId}.pdf")
    public ResponseEntity<byte[]> reporteParticipanteCategoria(
            @PathVariable Long idActividad,
            @PathVariable Long idParticipante,
            @PathVariable Long categoriaId) {
        
        try {
            byte[] pdf = dashService.generarReportePorParticipanteCategoria(
                idParticipante, 
                categoriaId
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                "attachment", 
                "evaluacion-participante-" + idParticipante + "-cat-" + categoriaId + ".pdf"
            );
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
                    
        } catch (Exception e) {
            log.error("Error generando reporte de participante", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generar reporte COMPLETO de un participante (todas las categorías)
     * Muestra evaluaciones de ambas categorías + total
     */
    @GetMapping("/{idActividad}/reporte/participante/{idParticipante}/completo.pdf")
    public ResponseEntity<byte[]> reporteParticipanteCompleto(
            @PathVariable Long idActividad,
            @PathVariable Long idParticipante) {
        
        try {
            byte[] pdf = dashService.generarReporteCompletoParticipante(
                idActividad,
                idParticipante
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                "attachment", 
                "reporte-completo-participante-" + idParticipante + ".pdf"
            );
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
                    
        } catch (Exception e) {
            log.error("Error generando reporte completo de participante", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
