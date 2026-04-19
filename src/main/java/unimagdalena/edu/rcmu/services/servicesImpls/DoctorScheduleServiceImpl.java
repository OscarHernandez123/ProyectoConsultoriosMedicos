package unimagdalena.edu.rcmu.services.servicesImpls;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleResponse;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.DoctorSchedule;
import unimagdalena.edu.rcmu.exceptions.BadRequestException;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.mappers.DoctorScheduleMapper;
import unimagdalena.edu.rcmu.repositories.DoctorRepository;
import unimagdalena.edu.rcmu.repositories.DoctorScheduleRepository;
import unimagdalena.edu.rcmu.services.service.DoctorScheduleService;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public DoctorScheduleResponse create(DoctorScheduleCreateRequest request, UUID doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        if(!request.startAt().isBefore(request.endAt())){
            throw new BadRequestException("Start time must be before end time");
        }

        boolean existsOverlap = doctorScheduleRepository.existsByDoctorIdAndDayOfWeekAndStartAtLessThanAndEndAtGreaterThan(doctorId, request.dayOfWeek(), request.startAt(), request.endAt());

        if(existsOverlap == true){
            throw new BadRequestException("Doctor schedule overlaps with an existing schedule");
    }

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(request.dayOfWeek())
                .startAt(request.startAt())
                .endAt(request.endAt())
                .build();

        DoctorSchedule saved = doctorScheduleRepository.save(schedule);

        return DoctorScheduleMapper.toResponse(saved);
    }

    @Override
    public List<DoctorScheduleResponse> listAll() {

        return doctorScheduleRepository.findAll()
                .stream()
                .map(DoctorScheduleMapper::toResponse)
                .toList();
    }

    @Override
    public List<DoctorScheduleResponse> listByDoctor(UUID doctorId) {

        if (!doctorRepository.existsById(doctorId)) {
            throw new NotFoundException("Doctor not found");
        }

        return doctorScheduleRepository.findByDoctorId(doctorId)
                .stream()
                .map(DoctorScheduleMapper::toResponse)
                .toList();
    }
}