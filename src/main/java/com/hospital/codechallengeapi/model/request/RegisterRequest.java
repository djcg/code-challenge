package com.hospital.codechallengeapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterRequest {

  @NotBlank
  private String name;
  @NotBlank
  private String username;
  @NotBlank
  @ToString.Exclude
  private String password;
}
