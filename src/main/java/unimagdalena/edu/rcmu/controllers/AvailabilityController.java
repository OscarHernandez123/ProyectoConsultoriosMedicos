package unimagdalena.edu.rcmu.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.AvailabilityDtos.AvailabilitySlotResponse;
import unimagdalena.edu.rcmu.services.service.AvailabilityService;

@RestController
@RequestMapping("/api/v1/availability/doctors/{doctorId}")
@RequiredArgsConstructor
@Validated
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<AvailabilitySlotResponse>> get(@PathVariable UUID doctorId, LocalDate day){
        return ResponseEntity.ok(availabilityService.getAvailableSlots(doctorId, day));
    }
}
