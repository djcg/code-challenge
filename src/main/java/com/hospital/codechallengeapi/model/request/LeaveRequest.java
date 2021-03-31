package com.hospital.codechallengeapi.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hospital.codechallengeapi.validator.TimeSlotValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
@ToString
public class LeaveRequest {

  @NotBlank
  @JsonProperty("leave_type")
  String leaveType;

  @NotNull
  @TimeSlotValidation
  @JsonProperty("start_date")
  Instant startDate;

  @TimeSlotValidation
  @JsonProperty("end_date")
  Instant endDate;
}
