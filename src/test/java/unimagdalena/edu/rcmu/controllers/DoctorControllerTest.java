package unimagdalena.edu.rcmu.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorResponse;

import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.services.service.DoctorService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    DoctorService doctorService;

    @Test
    void create_shouldReturn201AndLocation() throws Exception {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-04-21T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-04-21T11:00:00Z");

        String body = """
                {
                  "fullName": "Oscar Turizo",
                  "email": "oscar@gmail.com"
                }
                """;

        var response = new DoctorResponse(
                id,
                "Oscar Turizo",
                "oscar@gmail.com",
                createdAt,
                updatedAt,
                null,
                null
        );

        when(doctorService.create(any())).thenReturn(response);

        mvc.perform(post("/api/v1/doctors")
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/doctors/" + id)))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.fullName").value("Oscar Turizo"))
                .andExpect(jsonPath("$.email").value("oscar@gmail.com"));

        verify(doctorService).create(any());
    }

    @Test
    void get_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-04-21T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-04-21T11:00:00Z");

        var response = new DoctorResponse(
                id,
                "Oscar Turizo",
                "oscar@gmail.com",
                createdAt,
                updatedAt,
                null,
                null
        );

        when(doctorService.get(id)).thenReturn(response);

        mvc.perform(get("/api/v1/doctors/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.fullName").value("Oscar Turizo"))
                .andExpect(jsonPath("$.email").value("oscar@gmail.com"));

        verify(doctorService).get(id);
    }

    @Test
    void get_shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(doctorService.get(id)).thenThrow(new NotFoundException("Doctor not found"));

        mvc.perform(get("/api/v1/doctors/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"));
    }

    @Test
    void list_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-04-21T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-04-21T11:00:00Z");

        var doctor = new DoctorResponse(
                id,
                "Oscar Turizo",
                "oscar@gmail.com",
                createdAt,
                updatedAt,
                null,
                null
        );

        Page<DoctorResponse> page = new PageImpl<>(
                List.of(doctor),
                PageRequest.of(0, 10),
                1
        );

        when(doctorService.list(any())).thenReturn(page);

        mvc.perform(get("/api/v1/doctors")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].fullName").value("Oscar Turizo"))
                .andExpect(jsonPath("$.content[0].email").value("oscar@gmail.com"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(doctorService).list(any());
    }

    @Test
    void update_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        UUID specialtyId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-04-21T10:00:00Z");
        Instant updatedAt = Instant.parse("2026-04-21T11:00:00Z");

        String body = """
                {
                  "fullName": "Oscar Actualizado",
                  "email": "oscar.actualizado@gmail.com",
                  "profile": {},
                  "specialtyId": "%s"
                }
                """.formatted(specialtyId);

        var response = new DoctorResponse(
                id,
                "Oscar Actualizado",
                "oscar.actualizado@gmail.com",
                createdAt,
                updatedAt,
                null,
                null
        );

        when(doctorService.update(any(), eq(id))).thenReturn(response);

        mvc.perform(put("/api/v1/doctors/" + id)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.fullName").value("Oscar Actualizado"))
                .andExpect(jsonPath("$.email").value("oscar.actualizado@gmail.com"));

        verify(doctorService).update(any(), eq(id));
    }

    @Test
    void update_shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        UUID specialtyId = UUID.randomUUID();

        String body = """
                {
                  "fullName": "Oscar Actualizado",
                  "email": "oscar.actualizado@gmail.com",
                  "profile": {},
                  "specialtyId": "%s"
                }
                """.formatted(specialtyId);

        when(doctorService.update(any(), eq(id)))
                .thenThrow(new NotFoundException("Doctor not found"));

        mvc.perform(put("/api/v1/doctors/" + id)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"));
    }
}