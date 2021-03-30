package com.hospital.codechallengeapi.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hospital.codechallengeapi.validator.TimeSlotValidation;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

@Value
@AllArgsConstructor
@ToString
@Validated
public class AppointmentRequest {

  @TimeSlotValidation
  @JsonProperty("appointment_date")
  Instant appointmentDate;
}
