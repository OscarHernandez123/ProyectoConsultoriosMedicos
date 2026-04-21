package unimagdalena.edu.rcmu.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos;
import unimagdalena.edu.rcmu.enums.AppointmentStatus;
import unimagdalena.edu.rcmu.exceptions.ConflictException;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.services.service.AppointmentService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    AppointmentService appointmentService;

    @Test
    void create_shouldReturn201AndLocation() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID officeId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        Instant startAt = Instant.parse("2026-04-21T10:00:00Z");
        Instant endAt = Instant.parse("2026-04-21T10:30:00Z");
        Instant createdAt = Instant.parse("2026-04-21T09:00:00Z");
        Instant updatedAt = Instant.parse("2026-04-21T09:05:00Z");

        var request = new AppointmentDtos.AppointmentCreateRequest(
                "Consulta general",
                startAt,
                doctorId,
                patientId,
                officeId,
                appointmentTypeId
        );

        var response = new AppointmentDtos.AppointmentResponse(
                appointmentId,
                "Consulta general",
                startAt,
                endAt,
                AppointmentStatus.SCHEDULED,
                doctorId,
                "Dr. House",
                patientId,
                "Oscar Turizo",
                officeId,
                "Consultorio 101",
                appointmentTypeId,
                "Consulta",
                createdAt,
                updatedAt
        );

        when(appointmentService.create(any(AppointmentDtos.AppointmentCreateRequest.class)))
                .thenReturn(response);

        mvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        org.hamcrest.Matchers.endsWith("/api/v1/appointments/" + appointmentId)
                ))
                .andExpect(jsonPath("$.id").value(appointmentId.toString()))
                .andExpect(jsonPath("$.title").value("Consulta general"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$.patientId").value(patientId.toString()))
                .andExpect(jsonPath("$.officeId").value(officeId.toString()))
                .andExpect(jsonPath("$.appointmentTypeId").value(appointmentTypeId.toString()));
    }

    @Test
    void create_shouldReturn400WhenValidationFails() throws Exception {
        String body = """
                {
                  "title": "",
                  "startAt": null,
                  "doctorId": null,
                  "patientId": null,
                  "officeId": null,
                  "appointmentTypeId": null
                }
                """;

        mvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(appointmentService, never()).create(any());
    }

    @Test
    void get_shouldReturn404WhenNotFound() throws Exception {
        UUID appointmentId = UUID.randomUUID();

        when(appointmentService.get(appointmentId))
                .thenThrow(new NotFoundException("Appointment not found"));

        mvc.perform(get("/api/v1/appointments/" + appointmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Appointment not found"));
    }

    @Test
    void create_shouldReturn409WhenScheduleConflict() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID officeId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        var request = new AppointmentDtos.AppointmentCreateRequest(
                "Consulta general",
                Instant.parse("2026-04-21T10:00:00Z"),
                doctorId,
                patientId,
                officeId,
                appointmentTypeId
        );

        when(appointmentService.create(any(AppointmentDtos.AppointmentCreateRequest.class)))
                .thenThrow(new ConflictException("Doctor already has an appointment in that time range"));

        mvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Doctor already has an appointment in that time range"));
    }

    @Test
    void list_shouldReturn200() throws Exception {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID officeId = UUID.randomUUID();
        UUID appointmentTypeId = UUID.randomUUID();

        var response = new AppointmentDtos.AppointmentResponse(
                appointmentId,
                "Consulta general",
                Instant.parse("2026-04-21T10:00:00Z"),
                Instant.parse("2026-04-21T10:30:00Z"),
                AppointmentStatus.SCHEDULED,
                doctorId,
                "Dr. House",
                patientId,
                "Oscar Turizo",
                officeId,
                "Consultorio 101",
                appointmentTypeId,
                "Consulta",
                Instant.parse("2026-04-21T09:00:00Z"),
                Instant.parse("2026-04-21T09:05:00Z")
        );

        Page<AppointmentDtos.AppointmentResponse> page =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        when(appointmentService.list(any(Pageable.class))).thenReturn(page);

        mvc.perform(get("/api/v1/appointments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(appointmentId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Consulta general"))
                .andExpect(jsonPath("$.content[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }
}
