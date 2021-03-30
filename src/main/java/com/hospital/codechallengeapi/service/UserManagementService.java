package com.hospital.codechallengeapi.service;

import com.hospital.codechallengeapi.entity.*;
import com.hospital.codechallengeapi.exception.UserAlreadyExistsException;
import com.hospital.codechallengeapi.model.request.DoctorRegisterRequest;
import com.hospital.codechallengeapi.model.request.LoginRequest;
import com.hospital.codechallengeapi.model.request.PatientRegisterRequest;
import com.hospital.codechallengeapi.model.request.RegisterRequest;
import com.hospital.codechallengeapi.model.response.IdResponse;
import com.hospital.codechallengeapi.model.response.JwtResponse;
import com.hospital.codechallengeapi.repository.DoctorRepository;
import com.hospital.codechallengeapi.repository.HospitalUserRepository;
import com.hospital.codechallengeapi.repository.PatientRepository;
import com.hospital.codechallengeapi.repository.RoleRepository;
import com.hospital.codechallengeapi.security.jwt.JwtProvider;
import com.hospital.codechallengeapi.security.services.UserPrinciple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserManagementService {

  private final HospitalUserRepository hospitalUserRepository;

  private final DoctorRepository doctorRepository;

  private final PatientRepository patientRepository;

  private final RoleRepository roleRepository;

  private final PasswordEncoder passwordEncoder;

  private final AuthenticationManager authenticationManager;

  private final JwtProvider jwtProvider;

  @Autowired
  public UserManagementService(
      HospitalUserRepository hospitalUserRepository,
      DoctorRepository doctorRepository,
      PatientRepository patientRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtProvider jwtProvider) {
    this.hospitalUserRepository = hospitalUserRepository;
    this.doctorRepository = doctorRepository;
    this.patientRepository = patientRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtProvider = jwtProvider;
  }

  public IdResponse createDoctor(DoctorRegisterRequest request) throws UserAlreadyExistsException {
    checkIfUsernameExists(request.getUsername());

    HospitalUserEntity user = getHospitalUserEntity(request, ERole.ROLE_DOCTOR);

    DoctorEntity doctorEntity =
        DoctorEntity.builder().hospitalUserEntity(user).specialty(request.getSpecialty()).build();

    return new IdResponse(doctorRepository.save(doctorEntity).getId());
  }

  public IdResponse createPatient(PatientRegisterRequest request)
      throws UserAlreadyExistsException {
    checkIfUsernameExists(request.getUsername());

    HospitalUserEntity user = getHospitalUserEntity(request, ERole.ROLE_PATIENT);
    PatientEntity patientEntity =
        PatientEntity.builder().hospitalUserEntity(user).symptoms(request.getSymptoms()).build();

    return new IdResponse(patientRepository.save(patientEntity).getId());
  }

  public JwtResponse login(LoginRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtProvider.generateJwtToken(authentication);

    UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
    List<String> roles =
        userPrinciple.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    return new JwtResponse(
        jwt, userPrinciple.getId(), userPrinciple.getName(), userPrinciple.getUsername(), roles);
  }

  private void checkIfUsernameExists(String username) throws UserAlreadyExistsException {
    if (hospitalUserRepository.findByUsername(username).isPresent()) {
      throw new UserAlreadyExistsException("User with specified username already exists");
    }
  }

  private HospitalUserEntity getHospitalUserEntity(RegisterRequest request, ERole role) {
    Set<RoleEntity> roleEntities = new HashSet<>();
    RoleEntity roleEntity = roleRepository.findByName(role).orElseThrow();
    roleEntities.add(roleEntity);

    // Create new user's account
    HospitalUserEntity user =
        HospitalUserEntity.builder()
            .username(request.getUsername())
            .name(request.getName())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(roleEntities)
            .build();
    return user;
  }
}
