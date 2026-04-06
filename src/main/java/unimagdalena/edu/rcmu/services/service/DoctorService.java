package unimagdalena.edu.rcmu.services.service;

import java.util.List;
import java.util.UUID;

import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorResponse;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorUpdateRequest;

public interface DoctorService {
    DoctorResponse create(DoctorCreateRequest request);
    DoctorResponse patch(DoctorUpdateRequest request, UUID doctorId, UUID specialtyId);
    DoctorResponse get(UUID doctorId);
    List<DoctorResponse> listAll();
}
