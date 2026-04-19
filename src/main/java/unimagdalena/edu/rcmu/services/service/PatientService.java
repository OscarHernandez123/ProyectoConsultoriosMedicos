package unimagdalena.edu.rcmu.services.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientCreateRequest;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientPatchRequest;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientResponse;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientUpdateRequest;

public interface PatientService {
    PatientResponse create(PatientCreateRequest request);
    PatientResponse patch(PatientPatchRequest request, UUID patientId);
    PatientResponse update(PatientUpdateRequest request, UUID patientId);
    PatientResponse get(UUID patientId);
    Page<PatientResponse> list(Pageable pageable);
}
