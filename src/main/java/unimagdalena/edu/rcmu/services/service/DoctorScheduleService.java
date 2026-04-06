package unimagdalena.edu.rcmu.services.service;

import java.util.List;
import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleResponse;

public interface DoctorScheduleService {
    DoctorScheduleResponse create(DoctorScheduleCreateRequest request);
    List<DoctorScheduleResponse> listAll();
}
