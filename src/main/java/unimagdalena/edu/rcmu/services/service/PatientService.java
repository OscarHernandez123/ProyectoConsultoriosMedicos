package unimagdalena.edu.rcmu.services.service;

import java.util.List;
import java.util.UUID;

import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientCreateRequest;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientResponse;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientUpdateRequest;

public interface PatientService {
    PatientResponse create(PatientCreateRequest request);
    PatientResponse patch(PatientUpdateRequest request, UUID patientId);
    PatientResponse get(UUID patientId);
    List<PatientResponse> listAll();
}
