package unimagdalena.edu.rcmu.services.service;

import java.util.List;
import java.util.UUID;

import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeCreateRequest;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeResponse;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeUpdateRequest;

public interface OfficeService {
    OfficeResponse create(OfficeCreateRequest request);
    OfficeResponse patch(OfficeUpdateRequest request, UUID officeId);
    OfficeResponse get(UUID officeId);
    List<OfficeResponse> listAll();
}
