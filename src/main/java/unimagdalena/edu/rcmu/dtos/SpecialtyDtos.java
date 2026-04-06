package unimagdalena.edu.rcmu.dtos;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

public class SpecialtyDtos {
    public record SpecialtyCreateRequest(
        @NotBlank String title       
    ){}

    public record SpecialtyResponse(
        UUID id,
        String title
    ){}
}
