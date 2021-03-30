package com.hospital.codechallengeapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    @JsonProperty("doctor_name")
    private String doctorName;

    @JsonProperty("patient_name")
    private String patientName;

    private String specialty;

    @JsonProperty("start_date")
    private Instant startDate;

    @JsonProperty("end_date")
    private Instant endDate;

}
