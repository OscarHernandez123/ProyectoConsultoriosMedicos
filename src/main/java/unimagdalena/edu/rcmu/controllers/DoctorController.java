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
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorResponse;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorUpdateRequest;
import unimagdalena.edu.rcmu.services.service.DoctorService;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Validated
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponse> create(@Valid @RequestBody DoctorCreateRequest request,
                                                 UriComponentsBuilder uriBuilder){
        var doctorCreated = doctorService.create(request);
        var location = uriBuilder.path("/api/v1/doctors/{id}").buildAndExpand(doctorCreated.id()).toUri();
        return ResponseEntity.created(location).body(doctorCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(doctorService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        var result = doctorService.list(PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(result);                                               
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> update(@PathVariable UUID doctorId,
                                                 @Valid @RequestBody DoctorUpdateRequest request){
        return ResponseEntity.ok(doctorService.update(request, doctorId));                                                
    }
}
