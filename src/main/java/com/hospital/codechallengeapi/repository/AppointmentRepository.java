package com.hospital.codechallengeapi.repository;

import com.hospital.codechallengeapi.entity.AppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

    Page<AppointmentEntity> findAppointmentEntitiesByDoctorIdAndPatientIdIsNotNullOrderByStartDate(
            UUID doctorId, Pageable pageable);

    List<AppointmentEntity>
    findAppointmentEntitiesByDoctorIdAndStartDateIsLessThanAndEndDateGreaterThan(
            UUID doctorId, Instant startDate, Instant endDate);

}
