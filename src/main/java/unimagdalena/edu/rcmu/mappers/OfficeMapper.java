package unimagdalena.edu.rcmu.mappers;

import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeCreateRequest;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeResponse;
import unimagdalena.edu.rcmu.dtos.OfficeDtos.OfficeUpdateRequest;
import unimagdalena.edu.rcmu.entities.Office;
import unimagdalena.edu.rcmu.enums.OfficeStatus;

public class OfficeMapper {

    public static Office toEntity(OfficeCreateRequest request){
        return Office.builder()
                .location(request.location())
                .status(OfficeStatus.ACTIVE)
                .build();
    }

    public static void patch(Office office, OfficeUpdateRequest request){

        if(request.location() != null){
            office.setLocation(request.location());
        }

        if(request.status() != null){
            office.setStatus(request.status());
        }
    }

    public static OfficeResponse toResponse(Office office){
        return new OfficeResponse(office.getId(),
            office.getLocation(),
            office.getStatus(),
            office.getCreatedAt(),
            office.getUpdatedAt()
        );
    }
}
