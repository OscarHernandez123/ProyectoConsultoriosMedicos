package unimagdalena.edu.rcmu.services.servicesImpls;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorCreateRequest;
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
    public DoctorResponse patch(DoctorUpdateRequest request, UUID doctorId, UUID specialtyId) {

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
    public DoctorResponse get(UUID doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        return DoctorMapper.toResponse(doctor);
    }

    @Override
    public List<DoctorResponse> listAll() {

        return doctorRepository.findAll()
                .stream()
                .map(DoctorMapper::toResponse)
                .toList();
    }
}
