package com.eventhub.event;

import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository events;

    public EventService(EventRepository events) { this.events = events; }

    @Transactional
    public EventResponse create(CreateEventRequest request) {
        Event event = events.save(new Event(request.title(), request.description(), request.venue(), request.startsAt(), request.capacity(), request.numberOfDays(), request.dailyPay()));
        return EventResponse.from(event);
    }
    public List<EventResponse> list() {
        return events.findAllByOrderByStartsAtAsc().stream().map(EventResponse::from).toList();
    }
    public List<AdminEventResponse> listForAdmin() {
        return events.findAllByOrderByStartsAtAsc().stream().map(AdminEventResponse::from).toList();
    }
    @Transactional
    public EventResponse update(Long eventId, CreateEventRequest request) {
        Event event = eventWithRegistrations(eventId);
        if (request.capacity() < event.getRegistrations().size()) throw new EventCapacityException();
        event.update(request.title(), request.description(), request.venue(), request.startsAt(), request.capacity(), request.numberOfDays(), request.dailyPay());
        return EventResponse.from(event);
    }
    @Transactional
    public void delete(Long eventId) {
        if (!events.existsById(eventId)) throw new EventNotFoundException();
        events.deleteById(eventId);
    }
    @Transactional
    public EventResponse register(Long eventId, RegisterRequest request) {
        Event event = eventWithRegistrations(eventId);
        if (event.getRegistrations().size() >= event.getCapacity()) throw new EventFullException();
        boolean alreadyRegistered = event.getRegistrations().stream().anyMatch(r -> r.getEmail().equalsIgnoreCase(request.email()));
        if (alreadyRegistered) throw new DuplicateRegistrationException();
        event.addRegistration(new Registration(request.name(), request.email(), request.phoneNumber(), request.age(),
                request.gender(), request.location(), request.height(), request.weight(), request.education(),
                request.experience(), request.picture()));
        return EventResponse.from(event);
    }

    private Event eventWithRegistrations(Long eventId) {
        return events.findWithRegistrationsById(eventId).orElseThrow(EventNotFoundException::new);
    }
}
