package unimagdalena.edu.rcmu.mappers;

import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleResponse;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.DoctorSchedule;

public class DoctorScheduleMapper {

    public static DoctorSchedule toEntity(DoctorScheduleCreateRequest request, Doctor doctor){
        return DoctorSchedule.builder()
                .dayOfWeek(request.dayOfWeek())
                .startAt(request.startAt())
                .endAt(request.endAt())
                .doctor(doctor)
                .build();
    }

    public static DoctorScheduleResponse toResponse(DoctorSchedule doctorSchedule){
        return new DoctorScheduleResponse(
            doctorSchedule.getId(),
            doctorSchedule.getDayOfWeek(), 
            doctorSchedule.getStartAt(),
            doctorSchedule.getEndAt(),
            DoctorMapper.toResponse(doctorSchedule.getDoctor())
        );
    }
}
