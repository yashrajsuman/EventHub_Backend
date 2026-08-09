package com.eventhub.event;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        @NotNull @Min(1) Integer age,
        @NotBlank String gender,
        @NotBlank String location,
        @NotNull @Min(1) Integer height,
        @NotNull @Min(1) Integer weight,
        @NotBlank String education,
        @NotBlank String experience,
        @NotBlank String picture) { }
