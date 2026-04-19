package unimagdalena.edu.rcmu.services.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeCreateRequest;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficePatchRequest;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeResponse;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeUpdateRequest;

public interface OfficeService {
    OfficeResponse create(OfficeCreateRequest request);
    OfficeResponse patch(OfficePatchRequest request, UUID officeId);
    OfficeResponse update(OfficeUpdateRequest request, UUID officeId);
    OfficeResponse get(UUID officeId);
    Page<OfficeResponse> list(Pageable pageable);
}
