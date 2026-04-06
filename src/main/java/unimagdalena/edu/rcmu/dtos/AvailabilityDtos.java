package unimagdalena.edu.rcmu.dtos;

import java.time.Instant;

public class AvailabilityDtos {

    public record AvailabilitySlotResponse(
        Instant startAt,
        Instant endAt
    ){}
}
