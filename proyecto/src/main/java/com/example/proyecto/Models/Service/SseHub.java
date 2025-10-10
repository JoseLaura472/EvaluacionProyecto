package com.example.proyecto.Models.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseHub {
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> registry = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long actId) {
        SseEmitter emitter = new SseEmitter(0L); // 0 = sin timeout
        registry.computeIfAbsent(actId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        Runnable remover = () -> registry.getOrDefault(actId, new CopyOnWriteArrayList<>()).remove(emitter);
        emitter.onCompletion(remover);
        emitter.onTimeout(remover);
        emitter.onError(e -> remover.run());

        // opcional: enviar un "ping" inicial
        try { emitter.send(SseEmitter.event().name("ping").data("{\"tipo\":\"PING\"}")); } catch (IOException ignore) {
            remover.run();
        }
        return emitter;
    }

    public void broadcast(Long actId, Object payload) {
        var list = registry.getOrDefault(actId, new CopyOnWriteArrayList<>());
        for (var emitter : list) {
            try {
                emitter.send(SseEmitter.event()
                    .name("message")
                    .data(payload, MediaType.APPLICATION_JSON));
            } catch (IOException ex) {
                // cliente desconectado: eliminar y continuar
                list.remove(emitter);
            } catch (IllegalStateException ex) {
                // emitter ya completado
                list.remove(emitter);
            }
        }
    }
}
