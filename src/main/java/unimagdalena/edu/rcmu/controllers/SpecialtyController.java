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
import unimagdalena.edu.rcmu.dtos.SpecialtyDtos.SpecialtyCreateRequest;
import unimagdalena.edu.rcmu.dtos.SpecialtyDtos.SpecialtyResponse;
import unimagdalena.edu.rcmu.services.service.SpecialtyService;

@RestController
@RequestMapping("/api/v1/specialties")
@RequiredArgsConstructor
@Validated
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(@Valid @RequestBody SpecialtyCreateRequest request, UriComponentsBuilder uriBuilder){
        var specialtyCreated = specialtyService.create(request);
        var location = uriBuilder.path("/api/v1/specialties/{id}").buildAndExpand(specialtyCreated.id()).toUri();
        return ResponseEntity.created(location).body(specialtyCreated);
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> listAll(){
        return ResponseEntity.ok(specialtyService.listAll());
    }
}
