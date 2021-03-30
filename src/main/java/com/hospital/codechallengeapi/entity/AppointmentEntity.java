package com.hospital.codechallengeapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "appointment")
public class AppointmentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "doctor_id")
  private DoctorEntity doctor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "patient_id")
  private PatientEntity patient;

  @Column(columnDefinition = "TIMESTAMP", name = "start_date")
  private Instant startDate;

  @Column(columnDefinition = "TIMESTAMP", name = "end_date")
  private Instant endDate;

  @Column private String reason;
}
