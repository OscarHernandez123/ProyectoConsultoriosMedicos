package unimagdalena.edu.rcmu.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import unimagdalena.edu.rcmu.entities.Patient;
import unimagdalena.edu.rcmu.enums.PatientStatus;

public class PatientRepositoryTest extends AbstractRepositoryIT{

    @Autowired
    PatientRepository patientRepository;

    @Test
    void shouldCreatePatient(){

        Patient patient = Patient.builder()
                            .fullName("Oscar Turizo")
                            .phone("911")
                            .email("oscar@gmail.com")
                            .status(PatientStatus.ACTIVE)
                            .build();

        Patient savedPatient = patientRepository.save(patient);

        assertThat(savedPatient.getId()).isNotNull();
        assertThat(savedPatient.getFullName()).isEqualTo("Oscar Turizo");
        assertThat(savedPatient.getPhone()).isEqualTo("911");
        assertThat(savedPatient.getEmail()).isEqualTo("oscar@gmail.com");
        assertThat(savedPatient.getStatus()).isEqualTo(PatientStatus.ACTIVE);
    }

    @Test
    void shouldReadPatient(){

        Patient patient = Patient.builder()
                            .fullName("Oscar Turizo")
                            .phone("911")
                            .email("oscar@gmail.com")
                            .status(PatientStatus.ACTIVE)
                            .build();

        Patient savedPatient = patientRepository.save(patient);

        Optional<Patient> foundPatient = patientRepository.findById(savedPatient.getId());
        
        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get().getFullName()).isEqualTo("Oscar Turizo");
        assertThat(foundPatient.get().getPhone()).isEqualTo("911");
        assertThat(foundPatient.get().getEmail()).isEqualTo("oscar@gmail.com");
        assertThat(foundPatient.get().getStatus()).isEqualTo(PatientStatus.ACTIVE);
    }

    @Test
    void shouldUpdatePatient(){

        Patient patient = Patient.builder()
                            .fullName("Oscar Turizo")
                            .phone("911")
                            .email("oscar@gmail.com")
                            .status(PatientStatus.ACTIVE)
                            .build();

        patientRepository.save(patient);
        patient.setFullName("Manuel Hernandez");
        patient.setPhone("333");
        patient.setEmail("manuel@gmail.com");
        patient.setStatus(PatientStatus.INACTIVE);

        Optional<Patient> updatedPatient = patientRepository.findById(patient.getId());

        assertThat(updatedPatient).isPresent();
        assertThat(updatedPatient.get().getFullName()).isEqualTo("Manuel Hernandez");
        assertThat(updatedPatient.get().getPhone()).isEqualTo("333");
        assertThat(updatedPatient.get().getEmail()).isEqualTo("manuel@gmail.com");
        assertThat(updatedPatient.get().getStatus()).isEqualTo(PatientStatus.INACTIVE);
    }

    @Test 
    void shouldDeletePatient(){

        Patient patient = Patient.builder()
                            .fullName("Oscar Turizo")
                            .phone("911")
                            .email("oscar@gmail.com")
                            .status(PatientStatus.ACTIVE)
                            .build();

        patientRepository.save(patient);

        UUID deleteId = patient.getId();
        patientRepository.deleteById(patient.getId());

        assertThat(patientRepository.existsById(deleteId)).isFalse();
    }
}
