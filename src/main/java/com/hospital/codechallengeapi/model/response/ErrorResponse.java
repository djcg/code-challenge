package com.hospital.codechallengeapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ErrorResponse {

  String message;
}
