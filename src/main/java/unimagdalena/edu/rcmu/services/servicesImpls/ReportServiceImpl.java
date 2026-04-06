package unimagdalena.edu.rcmu.services.servicesImpls;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.ReportDtos;
import unimagdalena.edu.rcmu.dtos.ReportDtos.DoctorProductivityResponse;
import unimagdalena.edu.rcmu.dtos.ReportDtos.NoShowPatientResponse;
import unimagdalena.edu.rcmu.dtos.ReportDtos.OfficeOccupancyResponse;
import unimagdalena.edu.rcmu.repositories.AppointmentRepository;
import unimagdalena.edu.rcmu.services.service.ReportService;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService{
    
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<OfficeOccupancyResponse> OfficeOccupancy(Instant startDate, Instant endDate) {

        return appointmentRepository.getOfficeOccupation(startDate, endDate)
                .stream()
                .map(row -> new ReportDtos.OfficeOccupancyResponse(
                        (UUID) row[0],
                        (Long) row[1]
                ))
                .toList();
    }

    @Override
    public List<DoctorProductivityResponse> doctorProductivity() {

        return appointmentRepository.getDoctorByAppointmentsCompleteDesc()
                .stream()
                .map(row -> new ReportDtos.DoctorProductivityResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (Long) row[2]
                ))
                .toList();
    }

    @Override
    public List<NoShowPatientResponse> noShowPatient() {

        return appointmentRepository.getPatientbyAppointmentsNoShowDesc()
                .stream()
                .map(row -> new ReportDtos.NoShowPatientResponse(
                        (UUID) row[0],
                        (String) row[1],
                        (Long) row[2]
                ))
                .toList();
    }

}
