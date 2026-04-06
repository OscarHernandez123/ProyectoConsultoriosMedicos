package unimagdalena.edu.rcmu.dtos;

import java.util.UUID;

public class ReportDtos {
    public record OfficeOccupancyResponse(
        UUID officeId,
        Long occupiedMinutes
    ){}

    public record DoctorProductivityResponse(
        UUID doctorId,
        String doctorFullName,
        Long completedAppointments
    ){}

    public record NoShowPatientResponse(
        UUID patientId,
        String patientFullName,
        Long noShowCount
    ){}
}
