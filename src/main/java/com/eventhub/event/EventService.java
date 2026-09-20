package com.eventhub.event;

import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.eventhub.auth.AppUser;
import com.eventhub.auth.EventNotificationService;
import java.time.format.DateTimeFormatter;

@Service
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository events;
    private final EventNotificationService notifications;

    public EventService(EventRepository events, EventNotificationService notifications) { this.events = events; this.notifications = notifications; }

    @Transactional
    public EventResponse create(CreateEventRequest request) {
        Event event = events.save(new Event(request.title(), request.description(), request.venue(), request.startsAt(), request.capacity(), request.numberOfDays(), request.dailyPay()));
        notifications.notifyEventPublished(event.getTitle(), event.getVenue(), event.getStartsAt().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy 'at' h:mm a")), event.getDailyPay().toPlainString(), event.getNumberOfDays().toString());
        return EventResponse.from(event);
    }
    public List<EventResponse> list() {
        return events.findAllByOrderByStartsAtAsc().stream().map(EventResponse::from).toList();
    }
    public List<AdminEventResponse> listForAdmin() {
        return events.findAllByOrderByStartsAtAsc().stream().map(AdminEventResponse::from).toList();
    }
    public List<Long> registeredEventIds(AppUser user) {
        return events.findAllByOrderByStartsAtAsc().stream()
                .filter(event -> event.getRegistrations().stream().anyMatch(registration -> registration.getEmail().equalsIgnoreCase(user.getEmail())))
                .map(Event::getId).toList();
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
    public EventResponse register(Long eventId, AppUser user) {
        if (!user.isProfileComplete()) throw new ProfileIncompleteException();
        Event event = eventWithRegistrations(eventId);
        if (event.getRegistrations().size() >= event.getCapacity()) throw new EventFullException();
        boolean alreadyRegistered = event.getRegistrations().stream().anyMatch(r -> r.getEmail().equalsIgnoreCase(user.getEmail()));
        if (alreadyRegistered) throw new DuplicateRegistrationException();
        event.addRegistration(new Registration(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAge(), user.getGender(), user.getLocation(), user.getHeight(), user.getWeight(), user.getEducation(), user.getExperience(), user.getPicture()));
        return EventResponse.from(event);
    }

    private Event eventWithRegistrations(Long eventId) {
        return events.findWithRegistrationsById(eventId).orElseThrow(EventNotFoundException::new);
    }
}
