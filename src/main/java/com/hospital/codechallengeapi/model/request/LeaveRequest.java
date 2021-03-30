package com.hospital.codechallengeapi.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hospital.codechallengeapi.validator.TimeSlotValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.time.Instant;

@Value
@AllArgsConstructor
@Builder
@ToString
public class LeaveRequest {

  @JsonProperty("leave_type")
  String leaveType;

  @TimeSlotValidation
  @JsonProperty("start_date")
  Instant startDate;

  @TimeSlotValidation
  @JsonProperty("end_date")
  Instant endDate;
}
