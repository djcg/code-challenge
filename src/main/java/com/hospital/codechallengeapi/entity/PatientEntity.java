package com.hospital.codechallengeapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "patient")
public class PatientEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @OneToOne(cascade = CascadeType.MERGE)
  @MapsId
  @JoinColumn(name = "id")
  private HospitalUserEntity hospitalUserEntity;

  @Column private String symptoms;
}
