package com.hospital.codechallengeapi.controller;

import com.hospital.codechallengeapi.exception.UserAlreadyExistsException;
import com.hospital.codechallengeapi.model.request.AppointmentRequest;
import com.hospital.codechallengeapi.model.request.DoctorRegisterRequest;
import com.hospital.codechallengeapi.model.request.LeaveRequest;
import com.hospital.codechallengeapi.model.response.AppointmentResponse;
import com.hospital.codechallengeapi.model.response.AvailabilityResponse;
import com.hospital.codechallengeapi.model.response.DoctorResponse;
import com.hospital.codechallengeapi.model.response.IdResponse;
import com.hospital.codechallengeapi.security.services.UserPrinciple;
import com.hospital.codechallengeapi.service.DoctorService;
import com.hospital.codechallengeapi.service.UserManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequestMapping("/v1/doctors")
@Validated
@Slf4j
public class DoctorController {

  private final DoctorService doctorService;

  private final UserManagementService userManagementService;

  @Autowired
  public DoctorController(
      DoctorService doctorService, UserManagementService userManagementService) {
    this.doctorService = doctorService;
    this.userManagementService = userManagementService;
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public IdResponse createDoctor(@RequestBody DoctorRegisterRequest doctorRegisterRequest)
      throws UserAlreadyExistsException {
    log.debug("Going to create a new doctor with payload {}", doctorRegisterRequest);
    return this.userManagementService.createDoctor(doctorRegisterRequest);
  }

  @GetMapping
  public Page<DoctorResponse> getDoctors(
      @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
      @RequestParam(required = false, defaultValue = "10", name = "page_size") @Min(5)
          int pageSize) {
    log.debug("Going to get page {} with size {} of doctors", page, pageSize);
    return this.doctorService.getDoctors(page, pageSize);
  }

  @GetMapping("/appointments")
  @PreAuthorize("hasRole('DOCTOR')")
  public Page<AppointmentResponse> getDoctorAppointments(
      @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
      @RequestParam(required = false, defaultValue = "10", name = "page_size") @Min(5)
          int pageSize) {
    UUID doctorId = getLoggedUserId();
    log.debug(
        "Going to get page {} with size {} of appointments for doctor {}",
        page,
        pageSize,
        doctorId);
    return this.doctorService.getAppointments(doctorId, page, pageSize);
  }

  @PostMapping("/schedule-leave")
  @PreAuthorize("hasRole('DOCTOR')")
  @ResponseStatus(HttpStatus.CREATED)
  public IdResponse scheduleLeave(@RequestBody LeaveRequest leaveRequest) {
    UUID doctorId = getLoggedUserId();
    log.debug(
        "Going to schedule a leave for doctor {} with the payload {}", doctorId, leaveRequest);
    return this.doctorService.scheduleLeave(doctorId, leaveRequest);
  }

  @GetMapping("/{doctorId}/appointments")
  @PreAuthorize("hasRole('PATIENT')")
  public AvailabilityResponse getDoctorAvailability(@PathVariable UUID doctorId) {
    log.debug("Going to get availability for doctor {}", doctorId);
    return this.doctorService.getAvailability(doctorId);
  }

  @PostMapping("/{doctorId}/appointments")
  @PreAuthorize("hasRole('PATIENT')")
  @ResponseStatus(HttpStatus.CREATED)
  public IdResponse createDoctorAppointments(
      @PathVariable UUID doctorId, @RequestBody @Valid AppointmentRequest appointmentRequest) {
    UUID patientId = getLoggedUserId();
    log.debug(
        "Going to create an appointment for patient {} with doctor {} with payload {}",
        patientId,
        doctorId,
        appointmentRequest);
    return doctorService.createAppointment(
        doctorId, patientId, appointmentRequest.getAppointmentDate());
  }

  private UUID getLoggedUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return ((UserPrinciple) authentication.getPrincipal()).getId();
  }
}
