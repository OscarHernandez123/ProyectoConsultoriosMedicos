package unimagdalena.edu.rcmu.mappers;

import unimagdalena.edu.rcmu.dtos.SpecialtyDtos.SpecialtyCreateRequest;
import unimagdalena.edu.rcmu.dtos.SpecialtyDtos.SpecialtyResponse;
import unimagdalena.edu.rcmu.entities.Specialty;

public class SpecialtyMapper {

    public static Specialty toEntity(SpecialtyCreateRequest request){        
        return Specialty.builder()
                .title(request.title())
                .build();
    }

    public static SpecialtyResponse toResponse(Specialty specialty){
        return new SpecialtyResponse(
            specialty.getId(),
            specialty.getTitle());
    }
}
