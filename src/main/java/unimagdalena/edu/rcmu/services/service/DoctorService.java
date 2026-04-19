package unimagdalena.edu.rcmu.services.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorPatchRequest;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorResponse;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorUpdateRequest;

public interface DoctorService {
    DoctorResponse create(DoctorCreateRequest request);
    DoctorResponse patch(DoctorPatchRequest request, UUID doctorId);
    DoctorResponse update(DoctorUpdateRequest request, UUID doctorId);
    DoctorResponse get(UUID doctorId);
    Page<DoctorResponse> list(Pageable pageable);
}
