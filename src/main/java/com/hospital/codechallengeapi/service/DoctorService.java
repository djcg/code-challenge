package com.hospital.codechallengeapi.service;

import com.hospital.codechallengeapi.entity.AppointmentEntity;
import com.hospital.codechallengeapi.entity.DoctorEntity;
import com.hospital.codechallengeapi.entity.PatientEntity;
import com.hospital.codechallengeapi.exception.AppointmentCreationException;
import com.hospital.codechallengeapi.model.request.LeaveRequest;
import com.hospital.codechallengeapi.model.response.*;
import com.hospital.codechallengeapi.repository.AppointmentRepository;
import com.hospital.codechallengeapi.repository.DoctorRepository;
import com.hospital.codechallengeapi.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DoctorService {

  private final DoctorRepository doctorRepository;
  private final AppointmentRepository appointmentRepository;
  private final PatientRepository patientRepository;
  private final Clock clock;

  @Autowired
  public DoctorService(
      DoctorRepository doctorRepository,
      AppointmentRepository appointmentRepository,
      PatientRepository patientRepository,
      Clock clock) {
    this.doctorRepository = doctorRepository;
    this.appointmentRepository = appointmentRepository;
    this.patientRepository = patientRepository;
    this.clock = clock;
  }

  public Page<DoctorResponse> getDoctors(int page, int pageSize) {
    PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
    Page<DoctorEntity> doctors = this.doctorRepository.findAll(pageRequest);

    return doctors.map(
        doctor ->
            new DoctorResponse(
                doctor.getHospitalUserEntity().getId(),
                doctor.getHospitalUserEntity().getName(),
                doctor.getSpecialty()));
  }

  public Page<AppointmentResponse> getAppointments(UUID doctorId, int page, int pageSize) {
    PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
    Page<AppointmentEntity> appointments =
        this.appointmentRepository
            .findAppointmentEntitiesByDoctorIdAndPatientIdIsNotNullOrderByStartDate(
                doctorId, pageRequest);

    return appointments.map(
        appointment ->
            AppointmentResponse.builder()
                .doctorName(appointment.getDoctor().getHospitalUserEntity().getName())
                .patientName(appointment.getPatient().getHospitalUserEntity().getName())
                .specialty(appointment.getDoctor().getSpecialty())
                .startDate(appointment.getStartDate())
                .endDate(appointment.getEndDate())
                .build());
  }

  public IdResponse createAppointment(UUID doctorId, UUID patientId, Instant appointmentDate) {

    Instant endDate = appointmentDate.plus(1, ChronoUnit.HOURS);

    validateAppointmentHour(doctorId, appointmentDate, endDate);

    DoctorEntity doctorEntity = doctorRepository.getOne(doctorId);
    PatientEntity patientEntity = patientRepository.getOne(patientId);

    return createAppointment(doctorEntity, patientEntity, appointmentDate, endDate, null);
  }

  public IdResponse scheduleLeave(UUID doctorId, LeaveRequest leaveRequest)
      throws AppointmentCreationException {

    validateAppointmentHour(doctorId, leaveRequest.getStartDate(), leaveRequest.getEndDate());

    DoctorEntity doctorEntity = doctorRepository.getOne(doctorId);

    return createAppointment(
        doctorEntity,
        null,
        leaveRequest.getStartDate(),
        leaveRequest.getEndDate(),
        leaveRequest.getLeaveType());
  }

  public AvailabilityResponse getAvailability(UUID doctorId) {
    Instant start = clock.instant().truncatedTo(ChronoUnit.HOURS);
    Instant end = start.plus(7, ChronoUnit.DAYS);

    List<AppointmentEntity> appointments =
        this.appointmentRepository.findAppointmentEntitiesByDoctorIdAndStartDateBetween(
            doctorId, start, end);

    List<TimeSlot> availability = new ArrayList<>();
    while (start.isBefore(end)) {
      ZonedDateTime startZoneDateTime = start.atZone(ZoneId.of("UTC"));

      if (startZoneDateTime.get(ChronoField.HOUR_OF_DAY) >= 9
          && startZoneDateTime.get(ChronoField.HOUR_OF_DAY) <= 19) {
        Instant finalStart = start;
        if (appointments.stream()
            .noneMatch(
                appointmentEntity ->
                    (finalStart.isAfter(appointmentEntity.getStartDate())
                            || finalStart.compareTo(appointmentEntity.getStartDate()) == 0)
                        && finalStart.isBefore(appointmentEntity.getEndDate()))) {
          availability.add(
              TimeSlot.builder().startDate(start).endDate(start.plus(1, ChronoUnit.HOURS)).build());
        }
      }
      start = start.plus(1, ChronoUnit.HOURS);
    }

    return new AvailabilityResponse(availability);
  }

  private IdResponse createAppointment(
      DoctorEntity doctor,
      PatientEntity patient,
      Instant startAppointmentDate,
      Instant endAppointmentDate,
      String reason)
      throws AppointmentCreationException {

    AppointmentEntity appointmentEntity =
        AppointmentEntity.builder()
            .id(UUID.randomUUID())
            .doctor(doctor)
            .patient(patient)
            .startDate(startAppointmentDate.truncatedTo(ChronoUnit.HOURS))
            .endDate(endAppointmentDate.truncatedTo(ChronoUnit.HOURS))
            .reason(reason)
            .build();
    return new IdResponse(this.appointmentRepository.save(appointmentEntity).getId());
  }

  private void validateAppointmentHour(
      UUID doctorId, Instant startAppointmentDate, Instant endAppointmentDate) {
    if (!this.appointmentRepository
        .findAppointmentEntitiesByDoctorIdAndStartDateIsLessThanEqualAndEndDateGreaterThanEqual(
            doctorId, startAppointmentDate.truncatedTo(ChronoUnit.HOURS), endAppointmentDate.truncatedTo(ChronoUnit.HOURS))
        .isEmpty()) {
      throw new AppointmentCreationException("The selected date is already booked");
    }
  }
}
