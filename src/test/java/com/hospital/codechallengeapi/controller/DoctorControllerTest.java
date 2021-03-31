package com.hospital.codechallengeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.codechallengeapi.TestContainerStarter;
import com.hospital.codechallengeapi.entity.DoctorEntity;
import com.hospital.codechallengeapi.entity.HospitalUserEntity;
import com.hospital.codechallengeapi.model.request.DoctorRegisterRequest;
import com.hospital.codechallengeapi.model.request.PatientRegisterRequest;
import com.hospital.codechallengeapi.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class DoctorControllerTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag("12.3"))
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        TestContainerStarter.populateRegistryFromContainers(registry, postgres);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Transactional
    void shouldCreateDoctor() throws Exception {

        DoctorRegisterRequest request = new DoctorRegisterRequest("test1", "test1", "password1", "Cardiology");

        mockMvc
                .perform(
                        post("/v1/doctors").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());

    }

    @Test
    @Transactional
    void shouldNotCreateDoctor() throws Exception {

        DoctorRegisterRequest request = new DoctorRegisterRequest("test1", "test1", "password1", "Cardiology");

        mockMvc
                .perform(
                        post("/v1/doctors").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    @Transactional
    void shouldCreatePatient() throws Exception {

        PatientRegisterRequest request = new PatientRegisterRequest("test1", "test1", "password1", "Cardiology");

        mockMvc
                .perform(
                        post("/auth/register").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = {"DOCTOR"})
    @Transactional
    void shouldListDoctors() throws Exception {

        createDoctor(10);

        mockMvc
                .perform(
                        get("/v1/doctors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("doctor0"))
                .andExpect(jsonPath("$.content[0].specialty").value("specialty0"));
    }


    private void createDoctor(int howMany) {

        for (int i = 0; i < howMany; i++) {
            doctorRepository.save(DoctorEntity.builder().specialty("specialty" + i).hospitalUserEntity(HospitalUserEntity.builder().username("doctor" + i).password("doctor" + i).name("doctor" + i).build()).build());
        }

    }

}
