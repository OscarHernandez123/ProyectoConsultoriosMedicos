package unimagdalena.edu.rcmu.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.AppointmentTypeDtos.AppointmentTypeCreateRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentTypeDtos.AppointmentTypeResponse;
import unimagdalena.edu.rcmu.services.service.AppointmentTypeService;

@RestController
@RequestMapping("/api/v1/appointment-types")
@RequiredArgsConstructor
@Validated
public class AppointmentTypeController {

    private final AppointmentTypeService appointmentTypeService;

    @PostMapping
    public ResponseEntity<AppointmentTypeResponse> create(@Valid @RequestBody AppointmentTypeCreateRequest request,
                                                          UriComponentsBuilder uriBuilder){
        var appointmentTypeCreated = appointmentTypeService.create(request);
        var location = uriBuilder.path("/api/v1/appointment_types/{id}").buildAndExpand(appointmentTypeCreated.id()).toUri();
        return ResponseEntity.created(location).body(appointmentTypeCreated);                                                    
    }

    @GetMapping
    public ResponseEntity<List<AppointmentTypeResponse>> listAll(){
        return ResponseEntity.ok(appointmentTypeService.listAll());
    }
}
