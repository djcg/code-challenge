package com.hospital.codechallengeapi.service;

import com.hospital.codechallengeapi.entity.*;
import com.hospital.codechallengeapi.exception.UserAlreadyExistsException;
import com.hospital.codechallengeapi.model.request.DoctorRegisterRequest;
import com.hospital.codechallengeapi.model.request.LoginRequest;
import com.hospital.codechallengeapi.model.request.PatientRegisterRequest;
import com.hospital.codechallengeapi.model.response.IdResponse;
import com.hospital.codechallengeapi.model.response.JwtResponse;
import com.hospital.codechallengeapi.repository.DoctorRepository;
import com.hospital.codechallengeapi.repository.HospitalUserRepository;
import com.hospital.codechallengeapi.repository.PatientRepository;
import com.hospital.codechallengeapi.repository.RoleRepository;
import com.hospital.codechallengeapi.security.jwt.JwtProvider;
import com.hospital.codechallengeapi.security.services.UserPrinciple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {

    private final HospitalUserRepository hospitalUserRepository = mock(HospitalUserRepository.class);

    private final DoctorRepository doctorRepository = mock(DoctorRepository.class);

    private final PatientRepository patientRepository = mock(PatientRepository.class);

    private final RoleRepository roleRepository = mock(RoleRepository.class);

    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

    private final JwtProvider jwtProvider = mock(JwtProvider.class);

    private final UserManagementService userManagementService = new UserManagementService(hospitalUserRepository, doctorRepository, patientRepository, roleRepository, passwordEncoder, authenticationManager, jwtProvider);

    @Test
    public void createDoctor_shouldCreateUser() throws UserAlreadyExistsException {
        UUID id1 = UUID.randomUUID();

        DoctorRegisterRequest request = new DoctorRegisterRequest("test1", "test1", "password1", "specialty1");
        DoctorEntity doctorEntity = mock(DoctorEntity.class);

        when(hospitalUserRepository.findByUsername(eq("test1"))).thenReturn(Optional.empty());
        when(roleRepository.findByName(eq(ERole.ROLE_DOCTOR))).thenReturn(Optional.of(new RoleEntity()));
        when(passwordEncoder.encode("password1")).thenReturn("encodedPassword1");
        when(doctorRepository.save(any(DoctorEntity.class))).thenReturn(doctorEntity);
        when(doctorEntity.getId()).thenReturn(id1);

        IdResponse idResponse = userManagementService.createDoctor(request);
        assertNotNull(idResponse);
        assertEquals(id1, idResponse.getId());
    }

    @Test
    public void createDoctor_shouldThrowUserAlreadyExistsException() {

        DoctorRegisterRequest request = new DoctorRegisterRequest("test1", "test1", "password1", "specialty1");
        HospitalUserEntity hospitalUserEntity = mock(HospitalUserEntity.class);

        when(hospitalUserRepository.findByUsername(eq("test1"))).thenReturn(Optional.of(hospitalUserEntity));

        UserAlreadyExistsException thrown =
                assertThrows(
                        UserAlreadyExistsException.class,
                        () -> userManagementService.createDoctor(request),
                        "Expected UserAlreadyExistsException");

        assertNotNull(thrown);
        assertEquals("User with specified username already exists", thrown.getMessage());
    }

    @Test
    public void createPatient_shouldCreateUser() throws UserAlreadyExistsException {
        UUID id1 = UUID.randomUUID();

        PatientRegisterRequest request = new PatientRegisterRequest("test1", "test1", "password1", "symptoms1");
        PatientEntity patientEntity = mock(PatientEntity.class);

        when(hospitalUserRepository.findByUsername(eq("test1"))).thenReturn(Optional.empty());
        when(roleRepository.findByName(eq(ERole.ROLE_PATIENT))).thenReturn(Optional.of(new RoleEntity()));
        when(passwordEncoder.encode("password1")).thenReturn("encodedPassword1");
        when(patientRepository.save(any(PatientEntity.class))).thenReturn(patientEntity);
        when(patientEntity.getId()).thenReturn(id1);

        IdResponse idResponse = userManagementService.createPatient(request);
        assertNotNull(idResponse);
        assertEquals(id1, idResponse.getId());
    }

    @Test
    public void login_shouldLogin() {
        UUID id1 = UUID.randomUUID();

        LoginRequest request = new LoginRequest("test1", "password1");

        String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkamNnUCIsImV4cCI6MTYxNzEzMDQzMH0.K67mLRf3PuTuqytTE3ghdyf6LWxH4wvEp7zKx2MK8rE";
        UserPrinciple userPrinciple = new UserPrinciple(id1, "name1", "test1", "encodedPassword1", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtProvider.generateJwtToken(authentication)).thenReturn(jwt);
        when(authentication.getPrincipal()).thenReturn(userPrinciple);

        JwtResponse jwtResponse = userManagementService.login(request);

        assertNotNull(jwtResponse);
        assertEquals(id1, jwtResponse.getId());
        assertEquals(jwt, jwtResponse.getToken());
        assertEquals("name1", jwtResponse.getName());
        assertEquals("test1", jwtResponse.getUsername());
        assertEquals(1, jwtResponse.getRoles().size());

    }
}
