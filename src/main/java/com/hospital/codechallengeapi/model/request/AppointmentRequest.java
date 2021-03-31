package com.hospital.codechallengeapi.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hospital.codechallengeapi.validator.TimeSlotValidation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@AllArgsConstructor
@Getter
@ToString
@Validated
public class AppointmentRequest {

  @NotNull
  @TimeSlotValidation
  @JsonProperty("appointment_date")
  Instant appointmentDate;
}
