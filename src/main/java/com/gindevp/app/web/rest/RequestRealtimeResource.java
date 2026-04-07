package com.gindevp.app.web.rest;

import com.gindevp.app.service.RequestRealtimeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/realtime")
public class RequestRealtimeResource {

    private final RequestRealtimeService requestRealtimeService;

    public RequestRealtimeResource(RequestRealtimeService requestRealtimeService) {
        this.requestRealtimeService = requestRealtimeService;
    }

    @GetMapping(path = "/requests/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRequestEvents() {
        return requestRealtimeService.subscribe();
    }
}
