package unimagdalena.edu.rcmu.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class AppointmentTypeDtos {
    public record AppointmentTypeCreateRequest(
        @NotBlank String title
    ){}

    public record AppointmentTypeResponse(
        UUID id,
        String title,
        int durationMinutes
    ){}
}
