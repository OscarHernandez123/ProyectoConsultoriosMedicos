package unimagdalena.edu.rcmu.mappers;

import unimagdalena.edu.rcmu.dtos.DoctorProfileDtos.DoctorProfileCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorProfileDtos.DoctorProfileResponse;
import unimagdalena.edu.rcmu.dtos.DoctorProfileDtos.DoctorProfileUpdateRequest;
import unimagdalena.edu.rcmu.entities.DoctorProfile;

public class DoctorProfileMapper {

    public static DoctorProfile toEntity(DoctorProfileCreateRequest request){
        return DoctorProfile.builder()
                .bio(request.bio())
                .phone(request.phone())
                .build();
    }

    public static void patch(DoctorProfile profile, DoctorProfileUpdateRequest request){

        if(request.bio() != null){
            profile.setBio(request.bio());
        }

        if(request.phone() != null){
            profile.setPhone(request.phone());
        }
    }

    public static DoctorProfileResponse toResponse(DoctorProfile profile){
        return new DoctorProfileResponse(
            profile.getId(), 
            profile.getPhone(),
            profile.getBio());
    }
}
