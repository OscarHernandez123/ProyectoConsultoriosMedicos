package unimagdalena.edu.rcmu.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientCreateRequest;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientResponse;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientUpdateRequest;
import unimagdalena.edu.rcmu.services.service.PatientService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Validated
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientCreateRequest request, UriComponentsBuilder uriBuilder){
        var patientCreated = patientService.create(request);
        var location = uriBuilder.path("/api/v1/patients/{id}").buildAndExpand(patientCreated.id()).toUri();
        return ResponseEntity.created(location).body(patientCreated);        
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> get(@PathVariable UUID id){
        return ResponseEntity.ok(patientService.get(id));
    }
   
    @GetMapping
    public ResponseEntity<Page<PatientResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size){
        var result = patientService.list(PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(result);                                                
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> update(@PathVariable UUID id,
                                                  @Valid @RequestBody PatientUpdateRequest request){
        return ResponseEntity.ok(patientService.update(request, id));
    }
}
