package unimagdalena.edu.rcmu.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import unimagdalena.edu.rcmu.dtos.AvailabilityDtos;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.services.service.AvailabilityService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvailabilityController.class)
class AvailabilityControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AvailabilityService availabilityService;

    @Test
    void get_shouldReturn200() throws Exception {
        UUID doctorId = UUID.randomUUID();
        LocalDate day = LocalDate.of(2026, 4, 21);

        var response = List.of(
                new AvailabilityDtos.AvailabilitySlotResponse(
                        Instant.parse("2026-04-21T08:00:00Z"),
                        Instant.parse("2026-04-21T09:00:00Z")
                ),
                new AvailabilityDtos.AvailabilitySlotResponse(
                        Instant.parse("2026-04-21T10:00:00Z"),
                        Instant.parse("2026-04-21T11:00:00Z")
                )
        );

        when(availabilityService.getAvailableSlots(doctorId, day)).thenReturn(response);

        mvc.perform(get("/api/v1/availability/doctors/" + doctorId)
                        .param("day", "2026-04-21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startAt").value("2026-04-21T08:00:00Z"))
                .andExpect(jsonPath("$[0].endAt").value("2026-04-21T09:00:00Z"))
                .andExpect(jsonPath("$[1].startAt").value("2026-04-21T10:00:00Z"))
                .andExpect(jsonPath("$[1].endAt").value("2026-04-21T11:00:00Z"));

        verify(availabilityService).getAvailableSlots(doctorId, day);
    }

    @Test
    void get_shouldReturn404WhenScheduleNotFound() throws Exception {
        UUID doctorId = UUID.randomUUID();
        LocalDate day = LocalDate.of(2026, 4, 21);

        when(availabilityService.getAvailableSlots(doctorId, day))
                .thenThrow(new NotFoundException("Schedule not found"));

        mvc.perform(get("/api/v1/availability/doctors/" + doctorId)
                        .param("day", "2026-04-21"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Schedule not found"));
    }
}

