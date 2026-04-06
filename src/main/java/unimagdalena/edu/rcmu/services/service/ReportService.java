package unimagdalena.edu.rcmu.services.service;

import java.time.Instant;
import java.util.List;

import unimagdalena.edu.rcmu.dtos.ReportDtos;

public interface ReportService {
    List<ReportDtos.OfficeOccupancyResponse> OfficeOccupancy(Instant startDate, Instant endDate);
    List<ReportDtos.DoctorProductivityResponse> doctorProductivity();
    List<ReportDtos.NoShowPatientResponse> noShowPatient();
}
