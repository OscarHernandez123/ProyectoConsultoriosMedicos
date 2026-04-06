package unimagdalena.edu.rcmu.services.service;

import java.util.List;

import unimagdalena.edu.rcmu.dtos.SpecialtyDtos.SpecialtyCreateRequest;
import unimagdalena.edu.rcmu.dtos.SpecialtyDtos.SpecialtyResponse;

public interface SpecialtyService {
    SpecialtyResponse create(SpecialtyCreateRequest request);
    List<SpecialtyResponse> listAll();
}
