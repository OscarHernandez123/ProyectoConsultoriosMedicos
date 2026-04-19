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
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeCreateRequest;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeResponse;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeUpdateRequest;
import unimagdalena.edu.rcmu.services.service.OfficeService;

@RestController
@RequestMapping("api/v1/offices")
@RequiredArgsConstructor
@Validated
public class OfficeController {

    private final OfficeService officeService;

    @PostMapping
    public ResponseEntity<OfficeResponse> create(@Valid @RequestBody OfficeCreateRequest request, UriComponentsBuilder uriBuilder){
        var officeCreated = officeService.create(request);
        var location = uriBuilder.path("api/v1/offices/{id}").buildAndExpand(officeCreated.id()).toUri();
        return ResponseEntity.created(location).body(officeCreated);
    }

    @GetMapping
    public ResponseEntity<Page<OfficeResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        var result = officeService.list(PageRequest.of(page, size, Sort.by("id").ascending()));
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfficeResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody OfficeUpdateRequest request){
        return ResponseEntity.ok(officeService.update(request, id));
    }
}
