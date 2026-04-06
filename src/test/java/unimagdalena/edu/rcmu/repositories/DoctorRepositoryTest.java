package unimagdalena.edu.rcmu.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.Specialty;

public class DoctorRepositoryTest extends AbstractRepositoryIT{

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    SpecialtyRepository specialtyRepository;

    @Test
    void shouldCreateDoctor(){
        
        Doctor doctor = Doctor.builder()
                        .fullName("Oscar Turizo")
                        .email("oscar@gmail.com")
                        .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        assertThat(savedDoctor.getId()).isNotNull();
        assertThat(savedDoctor.getFullName()).isEqualTo("Oscar Turizo");
        assertThat(savedDoctor.getEmail()).isEqualTo("oscar@gmail.com");
    }

    @Test
    void shouldReadDoctor(){

        Doctor doctor = Doctor.builder()
                        .fullName("Oscar Turizo")
                        .email("oscar@gmail.com")
                        .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        Optional<Doctor> foundDoctor = doctorRepository.findById(savedDoctor.getId());

        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get().getFullName()).isEqualTo("Oscar Turizo");
        assertThat(foundDoctor.get().getEmail()).isEqualTo("oscar@gmail.com");
    }

    @Test
    void shouldUpdateDoctor(){

        Doctor doctor = Doctor.builder()
                        .fullName("Oscar Turizo")
                        .email("oscar@gmail.com")
                        .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        savedDoctor.setFullName("Manuel Hernandez");
        savedDoctor.setEmail("manuel@gmail.com");
        doctorRepository.save(savedDoctor);

        Optional<Doctor> updatedDoctor = doctorRepository.findById(savedDoctor.getId());

        assertThat(updatedDoctor).isPresent();
        assertThat(updatedDoctor.get().getFullName()).isEqualTo("Manuel Hernandez");
        assertThat(updatedDoctor.get().getEmail()).isEqualTo("manuel@gmail.com");
    }

    @Test
    void shouldDeleteDoctor(){

        Doctor doctor = Doctor.builder()
                        .fullName("Oscar Turizo")
                        .email("oscar@gmail.com")
                        .build();

        doctorRepository.save(doctor);

        UUID deleteId = doctor.getId();
        doctorRepository.deleteById(doctor.getId());

        assertThat(doctorRepository.existsById(deleteId)).isFalse();
    }

    @Test
    void testFindBySpecialtyId() {

        Specialty specialty = Specialty.builder()
                        .title("Pediatric")
                        .build();
        
        Specialty savedSpecialty = specialtyRepository.save(specialty);

        Doctor doctor = Doctor.builder()
                        .fullName("Oscar Turizo")
                        .email("oscar@gmail.com")
                        .specialty(savedSpecialty)
                        .build();

        doctorRepository.save(doctor);

        List<Doctor> doctors = doctorRepository.findBySpecialtyId(savedSpecialty.getId());

        assertThat(doctors).hasSize(1);
        assertThat(doctors.get(0).getFullName()).isEqualTo("Oscar Turizo");
    }
}
