package com.eventhub.event;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EventController {
    private final EventService events;
    public EventController(EventService events) { this.events = events; }

    @PostMapping("/admin/gigs")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequest request) { return events.create(request); }

    @GetMapping("/admin/gigs")
    public List<AdminEventResponse> getAdminEvents() { return events.listForAdmin(); }

    @PutMapping("/admin/gigs/{eventId}")
    public EventResponse updateEvent(@PathVariable Long eventId, @Valid @RequestBody CreateEventRequest request) { return events.update(eventId, request); }

    @DeleteMapping("/admin/gigs/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long eventId) { events.delete(eventId); }

    @GetMapping("/admin/session")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkAdminSession() { }

    @GetMapping("/gigs")
    public List<EventResponse> getEvents() { return events.list(); }

    @PostMapping("/gigs/{eventId}/registrations")
    public EventResponse register(@PathVariable Long eventId, @Valid @RequestBody RegisterRequest request) { return events.register(eventId, request); }

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, String> notFound() { return Map.of("message", "Event not found."); }
    @ExceptionHandler(EventFullException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, String> full() { return Map.of("message", "This event is full."); }
    @ExceptionHandler(DuplicateRegistrationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, String> duplicate() { return Map.of("message", "This email is already registered for the event."); }
    @ExceptionHandler(EventCapacityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, String> capacity() { return Map.of("message", "Capacity cannot be lower than the current registrations."); }
}
