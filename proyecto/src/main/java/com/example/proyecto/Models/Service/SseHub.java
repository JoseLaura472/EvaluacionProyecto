package com.example.proyecto.Models.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SseHub {
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> registry = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long actId) {
        SseEmitter emitter = new SseEmitter(0L); // 0 = sin timeout
        registry.computeIfAbsent(actId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        Runnable remover = () -> {
            CopyOnWriteArrayList<SseEmitter> lista = registry.get(actId);
            if (lista != null) {
                lista.remove(emitter);
                if (lista.isEmpty()) {
                    registry.remove(actId);
                }
            }
        };

        emitter.onCompletion(remover);
        emitter.onTimeout(remover);
        emitter.onError(e -> {
            log.warn("Error en SSE para actividad {}: {}", actId, e.getMessage());
            remover.run();
        });

        // Enviar ping inicial
        try {
            emitter.send(SseEmitter.event()
                    .name("ping")
                    .data("{\"tipo\":\"PING\"}"));
            log.info("Cliente SSE conectado a actividad {}", actId);
        } catch (IOException e) {
            log.error("Error enviando ping inicial", e);
            remover.run();
        }

        return emitter;
    }

    public void broadcast(Long actId, Object payload) {
        var list = registry.getOrDefault(actId, new CopyOnWriteArrayList<>());
        
        if (list.isEmpty()) {
            log.debug("No hay clientes SSE para actividad {}", actId);
            return;
        }

        int enviados = 0;
        int errores = 0;

        for (var emitter : list) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(payload, MediaType.APPLICATION_JSON));
                enviados++;
            } catch (IOException ex) {
                log.warn("Cliente SSE desconectado para actividad {}", actId);
                list.remove(emitter);
                errores++;
            } catch (IllegalStateException ex) {
                log.warn("SSE Emitter ya completado para actividad {}", actId);
                list.remove(emitter);
                errores++;
            }
        }

        log.debug("Broadcast a actividad {}: {} enviados, {} errores", actId, enviados, errores);
    }
}
