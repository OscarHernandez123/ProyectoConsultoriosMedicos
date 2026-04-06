package unimagdalena.edu.rcmu.repositories;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import unimagdalena.edu.rcmu.entities.Specialty;

public class SpecialtyRepositoryTest extends AbstractRepositoryIT{

    @Autowired
    SpecialtyRepository specialtyRepository;

    @Test
    void shouldCreateSpecialty(){

        Specialty specialty = Specialty.builder()
                                .title("Pediatric")
                                .build();

        Specialty savedSpecialty = specialtyRepository.save(specialty);

        assertThat(savedSpecialty.getId()).isNotNull();
        assertThat(savedSpecialty.getTitle()).isEqualTo("Pediatric");
    }

    @Test
    void shouldReadSpecialty(){

        Specialty specialty = Specialty.builder()
                                .title("Pediatric")
                                .build();

        Specialty savedSpecialty = specialtyRepository.save(specialty);

        Optional<Specialty> foundSpecialty = specialtyRepository.findById(savedSpecialty.getId());

        assertThat(foundSpecialty).isPresent();
        assertThat(foundSpecialty.get().getTitle()).isEqualTo("Pediatric");
    }    

    @Test
    void shouldUpdatedSpecialty(){

        Specialty specialty = Specialty.builder()
                                .title("Pediatric")
                                .build();

        specialtyRepository.save(specialty);
        specialty.setTitle("Cardiology");
        
        Optional<Specialty> updatedSpecialty = specialtyRepository.findById(specialty.getId());

        assertThat(updatedSpecialty).isPresent();
        assertThat(updatedSpecialty.get().getTitle()).isEqualTo("Cardiology");
    }

    @Test
    void shouldDeleteSpecialty(){

        Specialty specialty = Specialty.builder()
                                .title("Pediatric")
                                .build();

        specialtyRepository.save(specialty);

        UUID deletedId = specialty.getId();
        specialtyRepository.deleteById(specialty.getId());

        assertThat(specialtyRepository.existsById(deletedId)).isFalse();
        
    }
}
