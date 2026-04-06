package unimagdalena.edu.rcmu.dtos;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class DoctorDtos {
    public record DoctorCreateRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @Valid DoctorProfileDtos.DoctorProfileCreateRequest profile,
        UUID specialtyId
    ){}

    public record DoctorUpdateRequest(
        String fullName,
        @Email String email,
        @Valid DoctorProfileDtos.DoctorProfileUpdateRequest profile,
        UUID specialtyId
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
