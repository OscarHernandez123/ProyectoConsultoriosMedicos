package unimagdalena.edu.rcmu.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleResponse;
import unimagdalena.edu.rcmu.services.service.DoctorScheduleService;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}/schedules")
@RequiredArgsConstructor
@Validated
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    @PostMapping
    public ResponseEntity<DoctorScheduleResponse> create(@PathVariable UUID doctorId,
                                                         @Valid @RequestBody DoctorScheduleCreateRequest request,
                                                         UriComponentsBuilder uriBuilder){
        var doctorScheduleCreated = doctorScheduleService.create(request, doctorId);
        var location = uriBuilder.path("/api/v1/doctors/{doctorId}/schedules").buildAndExpand(doctorId).toUri();
        return ResponseEntity.created(location).body(doctorScheduleCreated);                                                        
    }

    @GetMapping
    public ResponseEntity<List<DoctorScheduleResponse>> get(@PathVariable UUID doctorId){
        return ResponseEntity.ok(doctorScheduleService.listByDoctor(doctorId));
    }
}
