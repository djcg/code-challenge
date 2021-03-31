package com.hospital.codechallengeapi.model.request;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;


@Getter
@ToString
public class PatientRegisterRequest extends RegisterRequest {

  private final String symptoms;

  public PatientRegisterRequest(String symptoms) {
    this.symptoms = symptoms;
  }

  public PatientRegisterRequest(@NotBlank String name, @NotBlank String username, @NotBlank String password, String symptoms) {
    super(name, username, password);
    this.symptoms = symptoms;
  }
}
