package com.hospital.codechallengeapi.service;

import com.hospital.codechallengeapi.entity.AppointmentEntity;
import com.hospital.codechallengeapi.entity.DoctorEntity;
import com.hospital.codechallengeapi.entity.HospitalUserEntity;
import com.hospital.codechallengeapi.entity.PatientEntity;
import com.hospital.codechallengeapi.exception.AppointmentCreationException;
import com.hospital.codechallengeapi.model.request.LeaveRequest;
import com.hospital.codechallengeapi.model.response.AppointmentResponse;
import com.hospital.codechallengeapi.model.response.AvailabilityResponse;
import com.hospital.codechallengeapi.model.response.DoctorResponse;
import com.hospital.codechallengeapi.model.response.IdResponse;
import com.hospital.codechallengeapi.repository.AppointmentRepository;
import com.hospital.codechallengeapi.repository.DoctorRepository;
import com.hospital.codechallengeapi.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

  private final DoctorRepository doctorRepository = mock(DoctorRepository.class);

  private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);

  private final PatientRepository patientRepository = mock(PatientRepository.class);

  private final Clock clock = Clock.fixed(Instant.parse("2021-04-01T09:00:00Z"), ZoneId.of("UTC"));

  private final DoctorService doctorService =
      new DoctorService(doctorRepository, appointmentRepository, patientRepository, clock);

  @Test
  public void getDoctors_shouldReturnAPage() {

    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    HospitalUserEntity user1 =
        HospitalUserEntity.builder().id(id1).name("test1").username("test1").build();
    DoctorEntity doctor1 =
        DoctorEntity.builder().id(id1).hospitalUserEntity(user1).specialty("specialty1").build();
    HospitalUserEntity user2 =
        HospitalUserEntity.builder().id(id2).name("test2").username("test2").build();
    DoctorEntity doctor2 =
        DoctorEntity.builder().id(id2).hospitalUserEntity(user2).specialty("specialty2").build();

    Page<DoctorEntity> pagedResponse = new PageImpl(List.of(doctor1, doctor2));

    when(doctorRepository.findAll(any(Pageable.class))).thenReturn(pagedResponse);

    Page<DoctorResponse> response = doctorService.getDoctors(1, 10);

    assertNotNull(response);
    assertFalse(response.hasNext());
    assertFalse(response.hasPrevious());
    assertEquals(2, response.getTotalElements());
    assertEquals(1, response.getTotalPages());

    List<DoctorResponse> content = response.getContent();
    assertFalse(content.isEmpty());
    DoctorResponse doctorResponse = content.get(0);
    assertEquals(id1, doctorResponse.getId());
    assertEquals("test1", doctorResponse.getName());
  }

  @Test
  public void getDoctors_shouldReturnEmptyPage() {

    Page<DoctorEntity> pagedResponse = Page.empty();
    when(doctorRepository.findAll(any(Pageable.class))).thenReturn(pagedResponse);

    Page<DoctorResponse> response = doctorService.getDoctors(1, 10);

    assertNotNull(response);
    assertFalse(response.hasNext());
    assertFalse(response.hasPrevious());
    assertEquals(0, response.getTotalElements());
    assertEquals(1, response.getTotalPages());

    List<DoctorResponse> content = response.getContent();
    assertTrue(content.isEmpty());
  }

  @Test
  public void getAppointments_shouldReturnAPage() {

    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    UUID id3 = UUID.randomUUID();
    UUID id4 = UUID.randomUUID();

    HospitalUserEntity user1 =
        HospitalUserEntity.builder().id(id1).name("doctor1").username("doctor1").build();
    DoctorEntity doctor1 =
        DoctorEntity.builder().id(id1).hospitalUserEntity(user1).specialty("specialty1").build();
    HospitalUserEntity user2 =
        HospitalUserEntity.builder().id(id2).name("patient1").username("patient1").build();
    PatientEntity patient1 =
        PatientEntity.builder().id(id2).hospitalUserEntity(user2).symptoms("symptoms1").build();

    Instant now = clock.instant();
    Instant tomorrow = now.plus(1, ChronoUnit.DAYS);

    AppointmentEntity appointmentEntity1 =
        AppointmentEntity.builder()
            .id(id3)
            .doctor(doctor1)
            .patient(patient1)
            .startDate(now)
            .endDate(now.plus(1, ChronoUnit.HOURS))
            .build();
    AppointmentEntity appointmentEntity2 =
        AppointmentEntity.builder()
            .id(id4)
            .doctor(doctor1)
            .patient(patient1)
            .startDate(tomorrow)
            .endDate(tomorrow.plus(1, ChronoUnit.HOURS))
            .build();

    Page<AppointmentEntity> pagedResponse =
        new PageImpl(List.of(appointmentEntity1, appointmentEntity2));
    when(appointmentRepository
            .findAppointmentEntitiesByDoctorIdAndPatientIdIsNotNullOrderByStartDate(
                eq(id1), any(Pageable.class)))
        .thenReturn(pagedResponse);

    Page<AppointmentResponse> response = doctorService.getAppointments(id1, 1, 10);

    assertNotNull(response);
    assertFalse(response.hasNext());
    assertFalse(response.hasPrevious());
    assertEquals(2, response.getTotalElements());
    assertEquals(1, response.getTotalPages());

    List<AppointmentResponse> content = response.getContent();
    assertFalse(content.isEmpty());
    AppointmentResponse appointmentResponse = content.get(0);
    assertEquals("doctor1", appointmentResponse.getDoctorName());
    assertEquals("patient1", appointmentResponse.getPatientName());
    assertEquals("specialty1", appointmentResponse.getSpecialty());
    assertEquals(now, appointmentResponse.getStartDate());
    assertEquals(now.plus(1, ChronoUnit.HOURS), appointmentResponse.getEndDate());
  }

  @Test
  public void getAppointments_shouldReturnEmptyPage() {

    Page<AppointmentEntity> pagedResponse = Page.empty();
    when(appointmentRepository
            .findAppointmentEntitiesByDoctorIdAndPatientIdIsNotNullOrderByStartDate(
                any(UUID.class), any(Pageable.class)))
        .thenReturn(pagedResponse);

    Page<AppointmentResponse> response = doctorService.getAppointments(UUID.randomUUID(), 1, 10);

    assertNotNull(response);
    assertFalse(response.hasNext());
    assertFalse(response.hasPrevious());
    assertEquals(0, response.getTotalElements());
    assertEquals(1, response.getTotalPages());

    List<AppointmentResponse> content = response.getContent();
    assertTrue(content.isEmpty());
  }

  @Test
  public void createAppointment_shouldCreateAnAppointment() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    UUID id3 = UUID.randomUUID();

    DoctorEntity doctor1 = mock(DoctorEntity.class);
    PatientEntity patient1 = mock(PatientEntity.class);

    AppointmentEntity appointmentEntity1 = AppointmentEntity.builder().id(id3).build();

    Instant now = clock.instant();

    when(appointmentRepository
            .findAppointmentEntitiesByDoctorIdAndStartDateIsLessThanAndEndDateGreaterThan(
                eq(id1), eq(now.plus(1, ChronoUnit.HOURS)), eq(now)))
        .thenReturn(Collections.emptyList());
    when(doctorRepository.findById(eq(id1))).thenReturn(Optional.of(doctor1));
    when(patientRepository.getOne(eq(id2))).thenReturn(patient1);
    when(appointmentRepository.save(any(AppointmentEntity.class))).thenReturn(appointmentEntity1);

    IdResponse idResponse = doctorService.createAppointment(id1, id2, now);

    assertNotNull(idResponse);
    assertEquals(id3, idResponse.getId());
  }

  @Test
  public void createAppointment_shouldThrowAnException() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    AppointmentEntity appointmentEntity = mock(AppointmentEntity.class);

    Instant now = clock.instant();

    when(appointmentRepository
            .findAppointmentEntitiesByDoctorIdAndStartDateIsLessThanAndEndDateGreaterThan(
                eq(id1), eq(now.plus(1, ChronoUnit.HOURS)), eq(now)))
        .thenReturn(List.of(appointmentEntity));

    AppointmentCreationException thrown =
        assertThrows(
            AppointmentCreationException.class,
            () -> doctorService.createAppointment(id1, id2, now),
            "Expected AppointmentCreationException");

    assertTrue(thrown.getMessage().equals("The selected date is already booked"));
  }

  @Test
  public void scheduleLeave_shouldCreateAnAppointment() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    DoctorEntity doctor1 = mock(DoctorEntity.class);

    AppointmentEntity appointmentEntity1 = AppointmentEntity.builder().id(id2).build();

    Instant now = clock.instant();
    LeaveRequest request =
        LeaveRequest.builder()
            .startDate(now)
            .endDate(now.plus(7, ChronoUnit.DAYS))
            .leaveType("leaveType")
            .build();

    when(appointmentRepository
            .findAppointmentEntitiesByDoctorIdAndStartDateIsLessThanAndEndDateGreaterThan(
                eq(id1), eq(now), eq(request.getEndDate())))
        .thenReturn(Collections.emptyList());
    when(doctorRepository.getOne(eq(id1))).thenReturn(doctor1);
    when(appointmentRepository.save(any(AppointmentEntity.class))).thenReturn(appointmentEntity1);

    IdResponse idResponse = doctorService.scheduleLeave(id1, request);

    assertNotNull(idResponse);
    assertEquals(id2, idResponse.getId());
  }

  @Test
  public void scheduleLeave_shouldThrowAnException() {
    UUID id1 = UUID.randomUUID();

    AppointmentEntity appointmentEntity = mock(AppointmentEntity.class);

    Instant now = clock.instant();
    LeaveRequest request =
        LeaveRequest.builder()
            .startDate(now)
            .endDate(now.plus(7, ChronoUnit.DAYS))
            .leaveType("leaveType")
            .build();

    when(appointmentRepository
            .findAppointmentEntitiesByDoctorIdAndStartDateIsLessThanAndEndDateGreaterThan(
                eq(id1), eq(request.getEndDate()), eq(now)))
        .thenReturn(List.of(appointmentEntity));

    AppointmentCreationException thrown =
        assertThrows(
            AppointmentCreationException.class,
            () -> doctorService.scheduleLeave(id1, request),
            "Expected AppointmentCreationException");

    assertTrue(thrown.getMessage().equals("The selected date is already booked"));
  }

  @Test
  public void getAvailability_shouldReturnAllSlots() {

    UUID id1 = UUID.randomUUID();

    Instant start = clock.instant();
    Instant end = start.plus(7, ChronoUnit.DAYS);

    when(appointmentRepository.findAppointmentEntitiesByDoctorIdAndStartDateIsLessThanAndEndDateGreaterThan(
            eq(id1), eq(end), eq(start)))
        .thenReturn(Collections.emptyList());

    AvailabilityResponse response = doctorService.getAvailability(UUID.randomUUID());

    assertNotNull(response);
    assertEquals(77, response.getAvailableTimeSlots().size());
  }

  @Test
  public void getAvailability_shouldReturnAllSlotsExceptOne() {

    UUID id1 = UUID.randomUUID();

    Instant start = clock.instant();
    Instant end = start.plus(7, ChronoUnit.DAYS);

    AppointmentEntity appointmentEntity = AppointmentEntity.builder().startDate(start).endDate(start.plus(1, ChronoUnit.HOURS)).build();

    when(appointmentRepository.findAppointmentEntitiesByDoctorIdAndStartDateIsLessThanAndEndDateGreaterThan(
            eq(id1), eq(start.plus(1, ChronoUnit.HOURS)), eq(start)))
            .thenReturn(List.of(appointmentEntity));

    AvailabilityResponse response = doctorService.getAvailability(id1);

    assertNotNull(response);
    assertEquals(76, response.getAvailableTimeSlots().size());
    assertFalse(response.getAvailableTimeSlots().stream().anyMatch(timeSlot -> timeSlot.getStartDate().equals(start)));
  }

}
