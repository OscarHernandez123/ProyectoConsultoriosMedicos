package unimagdalena.edu.rcmu.services.servicesImpls;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.AppointmentTypeDtos.AppointmentTypeCreateRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentTypeDtos.AppointmentTypeResponse;
import unimagdalena.edu.rcmu.entities.AppointmentType;
import unimagdalena.edu.rcmu.mappers.AppointmentTypeMapper;
import unimagdalena.edu.rcmu.repositories.AppointmentTypeRepository;
import unimagdalena.edu.rcmu.services.service.AppointmentTypeService;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentTypeServiceImpl implements AppointmentTypeService {

    private final AppointmentTypeRepository appointmentTypeRepository;

    @Override
    public AppointmentTypeResponse create(AppointmentTypeCreateRequest request) {

        AppointmentType appointmentType = AppointmentType.builder()
                .title(request.title())
                .build();

        AppointmentType saved = appointmentTypeRepository.save(appointmentType);

        return AppointmentTypeMapper.toResponse(saved);
    }

    @Override
    public List<AppointmentTypeResponse> listAll() {

        return appointmentTypeRepository.findAll()
                .stream()
                .map(AppointmentTypeMapper::toResponse)
                .toList();
    }
}
