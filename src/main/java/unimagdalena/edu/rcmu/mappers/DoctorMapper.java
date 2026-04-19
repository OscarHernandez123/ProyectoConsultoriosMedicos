package unimagdalena.edu.rcmu.mappers;

import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorPatchRequest;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorResponse;
import unimagdalena.edu.rcmu.dtos.DoctorDtos.DoctorUpdateRequest;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.DoctorProfile;
import unimagdalena.edu.rcmu.entities.Specialty;

public class DoctorMapper {

    public static Doctor toEntity(DoctorCreateRequest request, Specialty specialty){
        DoctorProfile profile = null;

        if(request.profile() != null){
            profile = DoctorProfileMapper.toEntity(request.profile());
        }
        
        return Doctor.builder()
                .fullName(request.fullName())
                .email(request.email())
                .doctorProfile(profile)
                .specialty(specialty)
                .build();
    }
    
    public static void patch(Doctor doctor, DoctorPatchRequest request, Specialty specialty){

        if(request.fullName() != null){
            doctor.setFullName(request.fullName());
        }

        if(request.email() != null){
            doctor.setEmail(request.email());
        }

        if(specialty != null){
            doctor.setSpecialty(specialty);
        }

        if(request.profile() != null){
            if(doctor.getDoctorProfile() == null){
                DoctorProfile newProfile = new DoctorProfile();
                DoctorProfileMapper.patch(newProfile, request.profile());
                doctor.setDoctorProfile(newProfile);
            } else {
                DoctorProfileMapper.patch(doctor.getDoctorProfile(), request.profile());
            }
        }
    }

    public static void update(Doctor doctor, DoctorUpdateRequest request, Specialty specialty){
        doctor.setFullName(request.fullName());
        doctor.setEmail(request.email());
        doctor.setSpecialty(specialty);
        DoctorProfileMapper.patch(doctor.getDoctorProfile(), request.profile());
    }

    public static DoctorResponse toResponse(Doctor doctor){
        return new DoctorResponse(
            doctor.getId(),
            doctor.getFullName(),
            doctor.getEmail(),
            doctor.getCreatedAt(),
            doctor.getUpdatedAt(),
            DoctorProfileMapper.toResponse(doctor.getDoctorProfile()),
            SpecialtyMapper.toResponse(doctor.getSpecialty()));
    }
}
