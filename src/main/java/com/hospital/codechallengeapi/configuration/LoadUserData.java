package com.hospital.codechallengeapi.configuration;

import com.hospital.codechallengeapi.entity.ERole;
import com.hospital.codechallengeapi.entity.HospitalUserEntity;
import com.hospital.codechallengeapi.repository.HospitalUserRepository;
import com.hospital.codechallengeapi.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.util.Set;
import java.util.UUID;

@Configuration
@Slf4j
public class LoadUserData implements InitializingBean {

  private final HospitalUserRepository hospitalUserRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public LoadUserData(
      HospitalUserRepository hospitalUserRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder) {
    this.hospitalUserRepository = hospitalUserRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void afterPropertiesSet() {
    if (hospitalUserRepository.findByUsername("admin").isEmpty()) {

      HospitalUserEntity admin =
          HospitalUserEntity.builder()
              .username("admin")
              .name("admin")
              .password(passwordEncoder.encode("admin"))
              .roles(Set.of(roleRepository.findByName(ERole.ROLE_ADMIN).get()))
              .build();

      UUID userId = hospitalUserRepository.save(admin).getId();
      log.info("Created an admin user with id {}", userId);
    }
  }

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }
}
