package com.eventhub.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final ObjectProvider<JavaMailSender> mail;

    private final boolean emailEnabled;
    private final String from;

    private final SecureRandom random = new SecureRandom();

    public AuthService(
            UserRepository users,
            PasswordEncoder passwords,
            ObjectProvider<JavaMailSender> mail,
            @Value("${app.email.enabled:false}") boolean emailEnabled,
            @Value("${app.email.from:no-reply@eventhub.local}") String from
    ) {
        this.users = users;
        this.passwords = passwords;
        this.mail = mail;
        this.emailEnabled = emailEnabled;
        this.from = from;
    }

    public AuthResponse signUp(SignUpRequest request) {

        if (users.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new AuthException(
                    "An account already exists for this email."
            );
        }

        AppUser user = new AppUser(
                request.email(),
                passwords.encode(request.password())
        );

        String otp = issueOtp(user);

        users.save(user);

        sendOtp(user.getEmail(), otp);

        return response(user, null);
    }

    public AuthResponse verify(VerifyOtpRequest request) {

        AppUser user = users.findByEmailIgnoreCase(request.email())
                .orElseThrow(() ->
                        new AuthException("Account not found.")
                );

        if (user.isEmailVerified()) {
            throw new AuthException(
                    "Email is already verified. Please sign in."
            );
        }

        if (
                user.getOtpExpiresAt() == null
                        || user.getOtpExpiresAt().isBefore(LocalDateTime.now())
                        || !passwords.matches(
                        request.otp(),
                        user.getOtpHash()
                )
        ) {
            throw new AuthException(
                    "That OTP is invalid or has expired."
            );
        }

        user.verifyEmail();

        user.startSession(
                UUID.randomUUID().toString()
        );

        return response(
                user,
                user.getSessionToken()
        );
    }

    public AuthResponse signIn(SignInRequest request) {

        AppUser user = users.findByEmailIgnoreCase(request.email())
                .orElseThrow(() ->
                        new AuthException("Account not found.")
                );

        if (!passwords.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new AuthException(
                    "Invalid email or password."
            );
        }

        if (!user.isEmailVerified()) {

            String otp = issueOtp(user);

            sendOtp(
                    user.getEmail(),
                    otp
            );

            return response(
                    user,
                    null
            );
        }

        user.startSession(
                UUID.randomUUID().toString()
        );

        return response(
                user,
                user.getSessionToken()
        );
    }

    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        users.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            String otp = issueOtp(user);
            sendPasswordResetOtp(user.getEmail(), otp);
        });
        return new MessageResponse("If an account exists for this email, a password reset code has been sent.");
    }

    public MessageResponse resetPassword(ResetPasswordRequest request) {
        AppUser user = users.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new AuthException("That reset code is invalid or has expired."));
        if (user.getOtpExpiresAt() == null || user.getOtpExpiresAt().isBefore(LocalDateTime.now()) || !passwords.matches(request.otp(), user.getOtpHash())) {
            throw new AuthException("That reset code is invalid or has expired.");
        }
        user.setPasswordHash(passwords.encode(request.password()));
        user.clearOtp();
        user.clearSession();
        users.saveAndFlush(user);
        return new MessageResponse("Password updated. You can now sign in.");
    }

    public AppUser requireUser(String header) {

        if (
                header == null
                        || !header.startsWith("Bearer ")
        ) {
            throw new AuthException(
                    "Please sign in to continue."
            );
        }

        return users.findBySessionToken(
                header.substring(7)
        ).orElseThrow(() ->
                new AuthException(
                        "Your session has expired. Please sign in again."
                )
        );
    }

    public UserProfileResponse profile(AppUser user) {
        return UserProfileResponse.from(user);
    }

    public UserProfileResponse saveProfile(
            AppUser user,
            ProfileRequest request
    ) {

        user.completeProfile(request);

        users.saveAndFlush(user);

        return UserProfileResponse.from(user);
    }

    private String issueOtp(AppUser user) {

        String otp = String.format(
                "%06d",
                random.nextInt(1_000_000)
        );

        user.setOtp(
                passwords.encode(otp),
                LocalDateTime.now().plusMinutes(10)
        );

        return otp;
    }

    private AuthResponse response(
            AppUser user,
            String token
    ) {

        return new AuthResponse(
                token,
                user.getEmail(),
                user.isEmailVerified(),
                user.isProfileComplete()
        );
    }

    private void sendOtp(
            String to,
            String otp
    ) {

        JavaMailSender mailSender = mail.getIfAvailable();

        if (!emailEnabled || mailSender == null) {
            return;
        }

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            false,
                            "UTF-8"
                    );

            // Display name + actual Gmail address
            helper.setFrom(
                    from,
                    "EventHub"
            );

            helper.setTo(to);

            helper.setSubject(
                    "Your EventHub verification code"
            );

            helper.setText(
                    "Your EventHub verification code is "
                            + otp
                            + ". It expires in 10 minutes."
            );

            mailSender.send(message);

        } catch (Exception e) {

            throw new AuthException(
                    "Unable to send verification email."
            );
        }
    }

    private void sendPasswordResetOtp(String to, String otp) {
        JavaMailSender mailSender = mail.getIfAvailable();
        if (!emailEnabled || mailSender == null) return;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from, "EventHub");
            helper.setTo(to);
            helper.setSubject("Reset your EventHub password");
            helper.setText("Your EventHub password reset code is " + otp + ". It expires in 10 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new AuthException("Unable to send password reset email.");
        }
    }
}

class AuthException extends RuntimeException {

    AuthException(String message) {
        super(message);
    }
}
