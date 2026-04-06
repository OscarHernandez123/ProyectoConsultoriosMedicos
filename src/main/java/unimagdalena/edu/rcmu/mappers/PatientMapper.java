package unimagdalena.edu.rcmu.mappers;

import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientCreateRequest;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientResponse;
import unimagdalena.edu.rcmu.dtos.PatientDtos.PatientUpdateRequest;
import unimagdalena.edu.rcmu.entities.Patient;
import unimagdalena.edu.rcmu.enums.PatientStatus;

public class PatientMapper {

    public static Patient toEntity(PatientCreateRequest request){
        return Patient.builder()
                .fullName(request.fullName())
                .phone(request.phone())
                .email(request.email())
                .status(PatientStatus.ACTIVE)
                .build();                                
    }

    public static void patch(Patient patient, PatientUpdateRequest request){

        if(request.fullName() != null){
            patient.setFullName(request.fullName());
        }

        if(request.phone() != null){
            patient.setPhone(request.phone());
        }

        if(request.email() != null){
            patient.setEmail(request.email());
        }

        if(request.status() != null){
            patient.setStatus(request.status());
        }
    }

    public static PatientResponse toResponse(Patient patient){
        return new PatientResponse(
            patient.getId(),
            patient.getFullName(), 
            patient.getPhone(),
            patient.getEmail(),
            patient.getStatus(),
            patient.getCreatedAt(),
            patient.getUpdatedAt()
        );
    }
}
