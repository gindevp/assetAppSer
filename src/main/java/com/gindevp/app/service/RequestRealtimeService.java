package com.gindevp.app.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class RequestRealtimeService {

    private static final Logger LOG = LoggerFactory.getLogger(RequestRealtimeService.class);

    private static final Set<String> REQUEST_PATH_PREFIXES = Set.of(
        "/api/allocation-requests",
        "/api/allocation-request-lines",
        "/api/repair-requests",
        "/api/repair-request-lines",
        "/api/return-requests",
        "/api/return-request-lines",
        "/api/loss-report-requests",
        "/api/loss-report-entry-lines"
    );

    private final CopyOnWriteArraySet<SseEmitter> emitters = new CopyOnWriteArraySet<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(ex -> emitters.remove(emitter));
        try {
            emitter.send(SseEmitter.event().name("connected").data(Map.of("ok", true)));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    public void publishHttpMutation(String method, String uriPath, int status) {
        if (!isRequestMutation(method, uriPath, status)) {
            return;
        }
        var payload = Map.of("method", method, "path", uriPath, "status", status, "ts", Instant.now().toString());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("request-change").data(payload));
            } catch (Exception ex) {
                emitters.remove(emitter);
                LOG.debug("Remove broken realtime emitter: {}", ex.getMessage());
            }
        }
    }

    private static boolean isRequestMutation(String method, String uriPath, int status) {
        if (uriPath == null || status >= 400) {
            return false;
        }
        if (!("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) || "DELETE".equals(method))) {
            return false;
        }
        for (String prefix : REQUEST_PATH_PREFIXES) {
            if (uriPath.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
