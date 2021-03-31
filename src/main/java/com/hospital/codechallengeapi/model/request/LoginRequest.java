package com.hospital.codechallengeapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@Getter
@ToString
public class LoginRequest {

  @NotBlank
  String username;
  @NotBlank
  @ToString.Exclude
  String password;
}
