package com.hospital.codechallengeapi.model.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@ToString
public class DoctorRegisterRequest extends RegisterRequest {

  private String specialty;

  public DoctorRegisterRequest(String specialty) {
    this.specialty = specialty;
  }

  public DoctorRegisterRequest(@NotBlank String name, @NotBlank String username, @NotBlank String password, String specialty) {
    super(name, username, password);
    this.specialty = specialty;
  }
}
