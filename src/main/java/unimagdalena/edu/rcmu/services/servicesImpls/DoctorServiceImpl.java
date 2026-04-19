package unimagdalena.edu.rcmu.services.servicesImpls;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorPatchRequest;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorResponse;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorUpdateRequest;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.Specialty;
import unimagdalena.edu.rcmu.mappers.DoctorMapper;
import unimagdalena.edu.rcmu.repositories.DoctorRepository;
import unimagdalena.edu.rcmu.repositories.SpecialtyRepository;
import unimagdalena.edu.rcmu.services.service.DoctorService;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    public DoctorResponse create(DoctorCreateRequest request) {

        Specialty specialty = specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new NotFoundException("Specialty not found"));

        Doctor doctor = Doctor.builder()
                .fullName(request.fullName())
                .email(request.email())
                .specialty(specialty)
                .createdAt(Instant.now())
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        return DoctorMapper.toResponse(savedDoctor);
    }

    @Override
    public DoctorResponse patch(DoctorPatchRequest request, UUID doctorId) {

        Specialty specialty = specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new NotFoundException("Specialty not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        DoctorMapper.patch(doctor, request, specialty);

        doctor.setUpdatedAt(Instant.now());
        Doctor savedDoctor = doctorRepository.save(doctor);

        return DoctorMapper.toResponse(savedDoctor);
    }

    @Override
    public DoctorResponse update(DoctorUpdateRequest request, UUID doctorId){

        Specialty specialty = specialtyRepository.findById(request.specialtyId())
                .orElseThrow(() -> new NotFoundException("Specialty not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        DoctorMapper.update(doctor, request, specialty);

        doctor.setUpdatedAt(Instant.now());
        Doctor savedDoctor = doctorRepository.save(doctor);

        return DoctorMapper.toResponse(savedDoctor);
    }

    @Override
    public DoctorResponse get(UUID doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        return DoctorMapper.toResponse(doctor);
    }

    @Override
    public Page<DoctorResponse> list(Pageable pageable) {
        return doctorRepository.findAll(pageable).map(DoctorMapper::toResponse);
    }
}
