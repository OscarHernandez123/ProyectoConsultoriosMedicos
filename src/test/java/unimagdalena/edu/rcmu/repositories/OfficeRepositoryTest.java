package unimagdalena.edu.rcmu.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import unimagdalena.edu.rcmu.entities.Office;
import unimagdalena.edu.rcmu.enums.OfficeStatus;

public class OfficeRepositoryTest extends AbstractRepositoryIT{
    
    @Autowired
    OfficeRepository officeRepository;

    @Test
    void shouldCreateOffice(){

        Office office = Office.builder()
                        .location("Sincelejo")
                        .status(OfficeStatus.ACTIVE)
                        .build();

        Office savedOffice = officeRepository.save(office);

        assertThat(savedOffice.getId()).isNotNull();
        assertThat(savedOffice.getLocation()).isEqualTo("Sincelejo");
        assertThat(savedOffice.getStatus()).isEqualTo(OfficeStatus.ACTIVE);
    }

    @Test
    void shouldReadOffice(){

        Office office = Office.builder()
                        .location("Sincelejo")
                        .status(OfficeStatus.ACTIVE)
                        .build();

        Office savedOffice = officeRepository.save(office);

        Optional<Office> foundOffice = officeRepository.findById(savedOffice.getId());
        
        assertThat(foundOffice).isPresent();
        assertThat(foundOffice.get().getLocation()).isEqualTo("Sincelejo");
        assertThat(foundOffice.get().getStatus()).isEqualTo(OfficeStatus.ACTIVE);
    }

    @Test
    void shouldUpdateOffice(){

        Office office = Office.builder()
                        .location("Sincelejo")
                        .status(OfficeStatus.ACTIVE)
                        .build();

        Office savedOffice = officeRepository.save(office);
        savedOffice.setLocation("Corozal");
        savedOffice.setStatus(OfficeStatus.INACTIVE);

        Optional<Office> updatedOffice = officeRepository.findById(savedOffice.getId());

        assertThat(updatedOffice).isPresent();
        assertThat(updatedOffice.get().getLocation()).isEqualTo("Corozal");
        assertThat(updatedOffice.get().getStatus()).isEqualTo(OfficeStatus.INACTIVE);
    }

    @Test
    void shouldDeleteOffice(){

        Office office = Office.builder()
                        .location("Sincelejo")
                        .status(OfficeStatus.ACTIVE)
                        .build();

        officeRepository.save(office);
        
        UUID deleteId = office.getId();
        officeRepository.deleteById(deleteId);

        assertThat(officeRepository.existsById(deleteId)).isFalse();
    }
}
