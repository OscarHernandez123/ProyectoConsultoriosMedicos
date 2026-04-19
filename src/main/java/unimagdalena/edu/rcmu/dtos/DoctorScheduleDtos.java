package unimagdalena.edu.rcmu.dtos;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public class DoctorScheduleDtos {
    public record DoctorScheduleCreateRequest(
        @NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime startAt,
        @NotNull LocalTime endAt
    ){}

    public record DoctorScheduleResponse(
        UUID id,
        DayOfWeek dayOfWeek,
        LocalTime startAt,
        LocalTime endAt,
        DoctorDtos.DoctorResponse doctor
    ){}
}
