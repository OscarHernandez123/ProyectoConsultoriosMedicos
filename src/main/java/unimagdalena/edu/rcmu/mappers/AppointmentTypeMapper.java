package unimagdalena.edu.rcmu.mappers;

import unimagdalena.edu.rcmu.dtos.AppointmentTypeDtos.AppointmentTypeCreateRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentTypeDtos.AppointmentTypeResponse;
import unimagdalena.edu.rcmu.entities.AppointmentType;

public class AppointmentTypeMapper {

    public static AppointmentType toEntity(AppointmentTypeCreateRequest request){
        return AppointmentType.builder()
                .title(request.title())
                .build();
    }

    public static AppointmentTypeResponse toResponse(AppointmentType appointmentType){
        return new AppointmentTypeResponse(
            appointmentType.getId(),
            appointmentType.getTitle(),
            appointmentType.getDurationMinutes()
        );
    }
}
