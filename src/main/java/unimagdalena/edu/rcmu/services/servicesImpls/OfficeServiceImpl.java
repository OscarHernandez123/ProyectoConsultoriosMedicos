package unimagdalena.edu.rcmu.services.servicesImpls;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeCreateRequest;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeResponse;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeUpdateRequest;
import unimagdalena.edu.rcmu.entities.Office;
import unimagdalena.edu.rcmu.enums.OfficeStatus;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.mappers.OfficeMapper;
import unimagdalena.edu.rcmu.repositories.OfficeRepository;
import unimagdalena.edu.rcmu.services.service.OfficeService;

@Service
@RequiredArgsConstructor
@Transactional
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;

    @Override
    public OfficeResponse create(OfficeCreateRequest request) {

        Office office = Office.builder()
                .location(request.location())
                .status(OfficeStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        Office savedOffice = officeRepository.save(office);

        return OfficeMapper.toResponse(savedOffice);
    }

    @Override
    public OfficeResponse patch(OfficeUpdateRequest request, UUID officeId) {

        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new NotFoundException("Office not found"));

        OfficeMapper.patch(office, request);

        office.setUpdatedAt(Instant.now());
        Office savedOffice = officeRepository.save(office);

        return OfficeMapper.toResponse(savedOffice);
    }

    @Override
    public OfficeResponse get(UUID officeId) {

        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new NotFoundException("Office not found"));

        return OfficeMapper.toResponse(office);
    }

    @Override
    public List<OfficeResponse> listAll() {

        return officeRepository.findAll()
                .stream()
                .map(OfficeMapper::toResponse)
                .toList();
    }
}
