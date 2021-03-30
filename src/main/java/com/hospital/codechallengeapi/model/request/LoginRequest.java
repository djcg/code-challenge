package com.hospital.codechallengeapi.model.request;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@AllArgsConstructor
@ToString
public class LoginRequest {

  String username;
  @ToString.Exclude
  String password;
}
