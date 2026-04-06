package unimagdalena.edu.rcmu.services.servicesImpls;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientCreateRequest;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientResponse;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientUpdateRequest;
import unimagdalena.edu.rcmu.entities.Patient;
import unimagdalena.edu.rcmu.enums.PatientStatus;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.mappers.PatientMapper;
import unimagdalena.edu.rcmu.repositories.PatientRepository;
import unimagdalena.edu.rcmu.services.service.PatientService;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService{

    private final PatientRepository patientRepository;
    
    @Override
    public PatientResponse create(PatientCreateRequest request) {

        Patient patient = Patient.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .status(PatientStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        Patient savedPatient = patientRepository.save(patient);

        return PatientMapper.toResponse(savedPatient);
    }

    @Override
    public PatientResponse patch(PatientUpdateRequest request, UUID patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found"));

        PatientMapper.patch(patient, request);

        patient.setUpdatedAt(Instant.now());
        Patient savedPatient = patientRepository.save(patient);

        return PatientMapper.toResponse(savedPatient);
    }

    @Override
    public PatientResponse get(UUID patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found"));

        return PatientMapper.toResponse(patient);
    }

    @Override
    public List<PatientResponse> listAll() {

        return patientRepository.findAll()
                .stream()
                .map(PatientMapper::toResponse)
                .toList();
    }

}
