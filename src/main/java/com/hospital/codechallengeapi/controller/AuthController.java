package com.hospital.codechallengeapi.controller;

import com.hospital.codechallengeapi.exception.UserAlreadyExistsException;
import com.hospital.codechallengeapi.model.request.LoginRequest;
import com.hospital.codechallengeapi.model.request.PatientRegisterRequest;
import com.hospital.codechallengeapi.model.response.IdResponse;
import com.hospital.codechallengeapi.model.response.JwtResponse;
import com.hospital.codechallengeapi.service.UserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserManagementService userManagementService;

  @Autowired
  private AuthController(UserManagementService userManagementService) {
    this.userManagementService = userManagementService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public IdResponse register(@RequestBody @Valid PatientRegisterRequest request)
      throws UserAlreadyExistsException {
    log.debug("Going to create a new patient with payload {}", request);
    return userManagementService.createPatient(request);
  }

  @PostMapping("/login")
  public JwtResponse login(@RequestBody @Valid LoginRequest request) {
    log.debug("User {} is performing a login", request.getUsername());
    return userManagementService.login(request);
  }
}
