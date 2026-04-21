package unimagdalena.edu.rcmu.controllers;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
import com.github.dockerjava.api.exception.NotFoundException;

import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientCreateRequest;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientResponse;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientUpdateRequest;
import unimagdalena.edu.rcmu.enums.PatientStatus;
import unimagdalena.edu.rcmu.services.service.PatientService;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    PatientService patientService;

    @Test
    void testCreateShouldReturn201AndLocation() throws Exception {

        var request = new PatientCreateRequest(
                "Oscar Turizo",
                "3135569055",
                "oscar@gmail.com"
        );

        UUID idResponse = UUID.randomUUID();
        Instant createAtResponse = Instant.now();
        Instant updateAtResponse = Instant.now().plusSeconds(3600);

        var response = new PatientResponse(
                idResponse,
                "Oscar Manuel",
                "3135569055",
                "oscar@gmail.com",
                PatientStatus.ACTIVE,
                createAtResponse,
                updateAtResponse
        );

        when(patientService.create(any(PatientCreateRequest.class))).thenReturn(response);

        mvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        org.hamcrest.Matchers.endsWith("/api/v1/patients/" + idResponse)
                ))
                .andExpect(jsonPath("$.id").value(idResponse.toString()))
                .andExpect(jsonPath("$.fullName").value("Oscar Manuel"))
                .andExpect(jsonPath("$.phone").value("3135569055"))
                .andExpect(jsonPath("$.email").value("oscar@gmail.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        var response = new PatientResponse(
                id,
                "Oscar Manuel",
                "3135569055",
                "oscar@gmail.com",
                PatientStatus.ACTIVE,
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        when(patientService.get(id)).thenReturn(response);

        mvc.perform(get("/api/v1/patients/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.fullName").value("Oscar Manuel"))
                .andExpect(jsonPath("$.phone").value("3135569055"))
                .andExpect(jsonPath("$.email").value("oscar@gmail.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testGetShouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(patientService.get(id)).thenThrow(new NotFoundException("Patient not found"));

        mvc.perform(get("/api/v1/patients/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    void testListShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        var response = new PatientResponse(
                id,
                "Oscar Manuel",
                "3135569055",
                "oscar@gmail.com",
                PatientStatus.ACTIVE,
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        Page<PatientResponse> pageResponse =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        when(patientService.list(any(Pageable.class))).thenReturn(pageResponse);

        mvc.perform(get("/api/v1/patients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].fullName").value("Oscar Manuel"))
                .andExpect(jsonPath("$.content[0].phone").value("3135569055"))
                .andExpect(jsonPath("$.content[0].email").value("oscar@gmail.com"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testUpdateShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        var request = new PatientUpdateRequest(
                "Oscar Actualizado",
                "3001112233",
                "oscaractualizado@gmail.com",
                PatientStatus.INACTIVE
        );

        var response = new PatientResponse(
                id,
                "Oscar Actualizado",
                "3001112233",
                "oscaractualizado@gmail.com",
                PatientStatus.INACTIVE,
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        when(patientService.update(any(PatientUpdateRequest.class), eq(id))).thenReturn(response);

        mvc.perform(put("/api/v1/patients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.fullName").value("Oscar Actualizado"))
                .andExpect(jsonPath("$.phone").value("3001112233"))
                .andExpect(jsonPath("$.email").value("oscaractualizado@gmail.com"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void testUpdateShouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        var request = new PatientUpdateRequest(
                "Oscar Actualizado",
                "3001112233",
                "oscaractualizado@gmail.com",
                PatientStatus.INACTIVE
        );

        when(patientService.update(any(PatientUpdateRequest.class), eq(id)))
                .thenThrow(new NotFoundException("Patient not found"));

        mvc.perform(put("/api/v1/patients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }
    
}
