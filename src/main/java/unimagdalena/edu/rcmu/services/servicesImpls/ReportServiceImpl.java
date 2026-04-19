package unimagdalena.edu.rcmu.services.servicesImpls;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public List<OfficeOccupancyResponse> officeOccupancy(Instant startDate, Instant endDate) {
        return appointmentRepository.getOfficeOccupation(startDate, endDate);
    }

    @Override
    public List<DoctorProductivityResponse> doctorProductivity() {
        return appointmentRepository.getDoctorByAppointmentsCompleteDesc();
    }

    @Override
    public List<NoShowPatientResponse> noShowPatient() {
        return appointmentRepository.getPatientbyAppointmentsNoShowDesc();
    }
}
