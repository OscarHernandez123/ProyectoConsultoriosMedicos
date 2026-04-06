package unimagdalena.edu.rcmu.dtos;

import java.time.Instant;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import unimagdalena.edu.rcmu.enums.OfficeStatus;

public class OfficeDtos {
    public record OfficeCreateRequest(
        @NotBlank String location
    ){}

    public record OfficeUpdateRequest(
        String location,
        OfficeStatus status
    ){}

    public record OfficeResponse(
        UUID id,
        String location,
        OfficeStatus status,
        Instant createdAt,
        Instant updatedAt
    ){}
}
