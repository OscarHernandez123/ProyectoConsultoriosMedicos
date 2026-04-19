package unimagdalena.edu.rcmu.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCancelRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCompleteRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCreateRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentResponse;
import unimagdalena.edu.rcmu.services.service.AppointmentService;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentCreateRequest request,
                                                      UriComponentsBuilder uriBuilder){
        var appointmentCreated = appointmentService.create(request);
        var location = uriBuilder.path("api/v1/appointments/{id}").buildAndExpand(appointmentCreated.id()).toUri();
        return ResponseEntity.created(location).body(appointmentCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(appointmentService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size){
        var result = appointmentService.list(PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(result);                                                    
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirm(@PathVariable UUID id){
        return ResponseEntity.ok(appointmentService.confirm(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable UUID id, @Valid @RequestBody AppointmentCancelRequest request){
        return ResponseEntity.ok(appointmentService.cancel(id, request));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable UUID id, @Valid @RequestBody AppointmentCompleteRequest request){
        return ResponseEntity.ok(appointmentService.complete(id, request));
    }

    @PutMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponse> noShow(@PathVariable UUID id){
        return ResponseEntity.ok(appointmentService.markNoShow(id));
    }
}
