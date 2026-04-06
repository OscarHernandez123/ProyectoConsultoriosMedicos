package unimagdalena.edu.rcmu.services.servicesImpls;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.SpecialtyDtos.SpecialtyCreateRequest;
import unimagdalena.edu.rcmu.dtos.SpecialtyDtos.SpecialtyResponse;
import unimagdalena.edu.rcmu.entities.Specialty;
import unimagdalena.edu.rcmu.mappers.SpecialtyMapper;
import unimagdalena.edu.rcmu.repositories.SpecialtyRepository;
import unimagdalena.edu.rcmu.services.service.SpecialtyService;

@Service
@RequiredArgsConstructor
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    @Override
    public SpecialtyResponse create(SpecialtyCreateRequest request) {

        Specialty specialty = Specialty.builder()
                .title(request.title())
                .build();

        Specialty savedSpecialty = specialtyRepository.save(specialty);

        return SpecialtyMapper.toResponse(savedSpecialty);
    }

    @Override
    public List<SpecialtyResponse> listAll() {

        return specialtyRepository.findAll()
                .stream()
                .map(SpecialtyMapper::toResponse)
                .toList();
    }
}
