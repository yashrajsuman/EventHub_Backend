package com.eventhub.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public record CreateEventRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String venue,
        @NotNull @Future LocalDateTime startsAt,
        @NotNull @Min(1) Integer capacity,
        @NotNull @Min(1) Integer numberOfDays,
        @NotNull @jakarta.validation.constraints.DecimalMin(value = "0.01") BigDecimal dailyPay) { }
