package unimagdalena.edu.rcmu.dtos;

import java.time.Instant;
import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import unimagdalena.edu.rcmu.enums.PatientStatus;

public class PatientDtos {
    public record PatientCreateRequest(
        @NotBlank String fullName, 
        @NotBlank String phone,
        @NotBlank @Email String email
    ){}

    public record PatientPatchRequest(
        String fullName,
        String phone,
        @Email String email,
        PatientStatus status
    ){}

    public record PatientUpdateRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        @NotNull @Email String email,
        @NotNull PatientStatus status
    ){}

    public record PatientResponse(
        UUID id,
        String fullName,
        String phone,
        String email,
        PatientStatus status,
        Instant createdAt,
        Instant updatedAt
    ){}
}
