package unimagdalena.edu.rcmu.dtos;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DoctorDtos {
    public record DoctorCreateRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @Valid DoctorProfileDtos.DoctorProfileCreateRequest profile,
        UUID specialtyId
    ){}

    public record DoctorPatchRequest(
        String fullName,
        @Email String email,
        @Valid DoctorProfileDtos.DoctorProfileUpdateRequest profile,
        UUID specialtyId
    ){}

    public record DoctorUpdateRequest(
        @NotBlank String fullName,
        @NotNull @Email String email,
        @NotNull @Valid DoctorProfileDtos.DoctorProfileUpdateRequest profile,
        @NotNull UUID specialtyId
    ){}

    public record DoctorResponse(
        UUID id,
        String fullName,
        String email,
        Instant createdAt,
        Instant updatedAt,
        DoctorProfileDtos.DoctorProfileResponse profile,
        SpecialtyDtos.SpecialtyResponse specialty
    ){}
}
