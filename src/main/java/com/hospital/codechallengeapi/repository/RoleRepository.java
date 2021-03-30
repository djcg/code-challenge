package com.hospital.codechallengeapi.repository;

import com.hospital.codechallengeapi.entity.ERole;
import com.hospital.codechallengeapi.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

  Optional<RoleEntity> findByName(ERole eRole);
}
