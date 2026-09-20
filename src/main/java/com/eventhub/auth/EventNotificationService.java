package com.eventhub.auth;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
public class EventNotificationService {
    private final UserRepository users;
    private final ObjectProvider<JavaMailSender> mail;
    private final boolean emailEnabled;
    private final String from, clientOrigin;
    private static final DateTimeFormatter EVENT_DATE = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy 'at' h:mm a");

    public EventNotificationService(UserRepository users, ObjectProvider<JavaMailSender> mail,
                                    @Value("${app.email.enabled:false}") boolean emailEnabled,
                                    @Value("${app.email.from:no-reply@eventhub.local}") String from,
                                    @Value("${app.client-origin}") String clientOrigin) {
        this.users = users; this.mail = mail; this.emailEnabled = emailEnabled; this.from = from; this.clientOrigin = clientOrigin;
    }

    @Async
    public void notifyEventPublished(String title, String venue, String startsAt, String dailyPay, String numberOfDays) {
        JavaMailSender sender = mail.getIfAvailable();
        if (!emailEnabled || sender == null) return;
        String body = "A new EventHub shift is now live.\n\n"
                + title + "\n"
                + "When: " + startsAt + "\n"
                + "Where: " + venue + "\n"
                + "Pay: ₹" + dailyPay + " per day for " + numberOfDays + " day(s)\n\n"
                + "View the event and register: " + clientOrigin + "/#gigs\n\n"
                + "You are receiving this because you have a completed EventHub profile.";
        for (AppUser user : users.findByEmailVerifiedTrueAndNameIsNotNull()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(from); message.setTo(user.getEmail()); message.setSubject("New EventHub shift: " + title); message.setText(body);
                sender.send(message);
            } catch (RuntimeException ignored) {
                // A single bad mailbox must not stop notifications to other users or event publishing.
            }
        }
    }
}
