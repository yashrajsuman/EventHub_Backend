package com.eventhub.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth; public AuthController(AuthService auth) { this.auth = auth; }
    @PostMapping("/signup") @ResponseStatus(HttpStatus.CREATED) AuthResponse signup(@Valid @RequestBody SignUpRequest r) { return auth.signUp(r); }
    @PostMapping("/verify-otp") AuthResponse verify(@Valid @RequestBody VerifyOtpRequest r) { return auth.verify(r); }
    @PostMapping("/signin") AuthResponse signin(@Valid @RequestBody SignInRequest r) { return auth.signIn(r); }
    @PostMapping("/forgot-password") MessageResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest r) { return auth.forgotPassword(r); }
    @PostMapping("/reset-password") MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest r) { return auth.resetPassword(r); }
    @GetMapping("/profile") UserProfileResponse profile(@RequestHeader("Authorization") String token) { return auth.profile(auth.requireUser(token)); }
    @PutMapping("/profile") UserProfileResponse profile(@RequestHeader("Authorization") String token, @Valid @RequestBody ProfileRequest r) { return auth.saveProfile(auth.requireUser(token), r); }
    @ExceptionHandler(AuthException.class) @ResponseStatus(HttpStatus.UNAUTHORIZED) Map<String, String> authError(AuthException e) { return Map.of("message", e.getMessage()); }
}
