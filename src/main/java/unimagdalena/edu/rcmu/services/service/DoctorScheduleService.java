package unimagdalena.edu.rcmu.services.service;

import java.util.List;
import java.util.UUID;

import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleResponse;

public interface DoctorScheduleService {
    DoctorScheduleResponse create(DoctorScheduleCreateRequest request, UUID id);
    List<DoctorScheduleResponse> listAll();
    List<DoctorScheduleResponse> listByDoctor(UUID doctorId);
}
