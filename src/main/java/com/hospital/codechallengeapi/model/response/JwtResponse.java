package com.hospital.codechallengeapi.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class JwtResponse {

  @NonNull private String token;

  private String type = "Bearer";

  @NonNull private UUID id;

  @NonNull private String name;

  @NonNull private String username;

  @NonNull private List<String> roles;
}
