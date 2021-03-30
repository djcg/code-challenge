package com.hospital.codechallengeapi.repository;

import com.hospital.codechallengeapi.entity.HospitalUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HospitalUserRepository extends JpaRepository<HospitalUserEntity, UUID> {

  Optional<HospitalUserEntity> findByUsername(String username);
}
