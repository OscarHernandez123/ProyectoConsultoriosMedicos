package unimagdalena.edu.rcmu.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public class DoctorProfileDtos {
    public record DoctorProfileCreateRequest(
        @NotBlank String phone,
        @NotBlank String bio
    ){}

    public record DoctorProfileUpdateRequest(
        String phone,
        String bio
    ){}

    public record DoctorProfileResponse(
        UUID id,
        String phone,
        String bio
    ){}
}
