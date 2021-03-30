package com.hospital.codechallengeapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterRequest {

  private String name;
  private String username;
  @ToString.Exclude
  private String password;
}
