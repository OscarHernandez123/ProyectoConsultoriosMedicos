package unimagdalena.edu.rcmu.controllers;

import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.ReportDtos;
import unimagdalena.edu.rcmu.services.service.ReportService;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Validated
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/office-occupancy")
    public ResponseEntity<List<ReportDtos.OfficeOccupancyResponse>> officeOccupancy(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate) {
        return ResponseEntity.ok(reportService.officeOccupancy(startDate, endDate));
    }

    @GetMapping("/doctor-productivity")
    public ResponseEntity<List<ReportDtos.DoctorProductivityResponse>> doctorProductivity() {
        return ResponseEntity.ok(reportService.doctorProductivity());
    }

    @GetMapping("/no-show-patients")
    public ResponseEntity<List<ReportDtos.NoShowPatientResponse>> noShowPatient() {
        return ResponseEntity.ok(reportService.noShowPatient());
    }
}
