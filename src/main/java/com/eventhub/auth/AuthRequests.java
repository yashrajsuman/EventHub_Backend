package com.eventhub.auth;

import jakarta.validation.constraints.*;

record SignUpRequest(@NotBlank @Email String email, @NotBlank @Size(min = 8, max = 100) String password, @AssertTrue(message = "You must accept the terms and conditions.") boolean termsAccepted) { }
record VerifyOtpRequest(@NotBlank @Email String email, @NotBlank @Pattern(regexp = "\\d{6}") String otp) { }
record SignInRequest(@NotBlank @Email String email, @NotBlank String password) { }
record ForgotPasswordRequest(@NotBlank @Email String email) { }
record ResetPasswordRequest(@NotBlank @Email String email, @NotBlank @Pattern(regexp = "\\d{6}") String otp, @NotBlank @Size(min = 8, max = 100) String password) { }
record MessageResponse(String message) { }
record ProfileRequest(@NotBlank String name, @NotBlank @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "Phone number may contain only digits and phone symbols.") String phoneNumber, @NotNull @Min(18) Integer age, @NotBlank String gender, @NotBlank String location, @NotNull @Min(1) Integer height, @NotNull @Min(1) Integer weight, @NotBlank String education, @NotBlank String experience, @NotBlank String picture, String aadhaarDocument) { }
record AuthResponse(String token, String email, boolean emailVerified, boolean profileComplete) { }
record UserProfileResponse(String email, boolean emailVerified, boolean profileComplete, String name, String phoneNumber, Integer age, String gender, String location, Integer height, Integer weight, String education, String experience, String picture, String aadhaarDocument) {
    static UserProfileResponse from(AppUser u) { return new UserProfileResponse(u.getEmail(), u.isEmailVerified(), u.isProfileComplete(), u.getName(), u.getPhoneNumber(), u.getAge(), u.getGender(), u.getLocation(), u.getHeight(), u.getWeight(), u.getEducation(), u.getExperience(), u.getPicture(), u.getAadhaarDocument()); }
}
