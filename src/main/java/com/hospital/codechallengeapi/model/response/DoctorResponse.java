package com.hospital.codechallengeapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DoctorResponse {

  private UUID id;
  private String name;
  private String specialty;
}
