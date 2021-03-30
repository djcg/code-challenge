package com.hospital.codechallengeapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AvailabilityResponse {

  @JsonProperty("available_time_slots")
  private List<TimeSlot> availableTimeSlots;
}
