package unimagdalena.edu.rcmu.mappers;

import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCreateRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentResponse;
import unimagdalena.edu.rcmu.entities.Appointment;
import unimagdalena.edu.rcmu.entities.AppointmentType;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.Office;
import unimagdalena.edu.rcmu.entities.Patient;
import unimagdalena.edu.rcmu.enums.AppointmentStatus;

public class AppointmentMapper {

    public static Appointment toEntity(AppointmentCreateRequest request, Doctor doctor, Patient patient, Office office, AppointmentType type){
        return Appointment.builder()
                .title(request.title())
                .startAt(request.startAt())
                .doctor(doctor)
                .patient(patient)
                .office(office)
                .appointmentType(type)
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    public static AppointmentResponse toResponse(Appointment appointment){
        return new AppointmentResponse(
            appointment.getId(),
            appointment.getTitle(), 
            appointment.getStartAt(), 
            appointment.getEndAt(), 
            appointment.getStatus(),
            appointment.getDoctor() != null ? appointment.getDoctor().getId() : null, 
            appointment.getDoctor() != null ? appointment.getDoctor().getFullName(): null,
            appointment.getPatient() != null ? appointment.getPatient().getId() : null,
            appointment.getPatient() != null ? appointment.getPatient().getFullName() : null,
            appointment.getOffice() != null ? appointment.getOffice().getId() : null,
            appointment.getOffice() != null ? appointment.getOffice().getLocation() : null,
            appointment.getAppointmentType() != null ? appointment.getAppointmentType().getId() : null,
            appointment.getAppointmentType() != null ? appointment.getAppointmentType().getTitle() : null,
            appointment.getCreatedAt(),
            appointment.getUpdatedAt()
        );
    }
}
