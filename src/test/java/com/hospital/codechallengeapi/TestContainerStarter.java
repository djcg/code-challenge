package com.hospital.codechallengeapi;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainerStarter {

  public static void populateRegistryFromContainers(
      DynamicPropertyRegistry registry, PostgreSQLContainer<?> postgreSQLContainer) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }
}
