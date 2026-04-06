package unimagdalena.edu.rcmu.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import unimagdalena.edu.rcmu.entities.AppointmentType;

public class AppointmentTypeTest extends AbstractRepositoryIT{

    @Autowired
    AppointmentTypeRepository appointmentTypeRepository;

    @Test
    void shouldCreateAppointmentType(){

        AppointmentType appointmentType = AppointmentType.builder()
                                            .title("Consultation")
                                            .build();

        AppointmentType savedAppointmentType = appointmentTypeRepository.save(appointmentType);

        assertThat(savedAppointmentType.getId()).isNotNull();
        assertThat(savedAppointmentType.getTitle()).isEqualTo("Consultation");
    }

    @Test
    void shouldReadAppointmentType(){

        AppointmentType appointmentType = AppointmentType.builder()
                                            .title("Consultation")
                                            .build();

        AppointmentType savedAppointmentType = appointmentTypeRepository.save(appointmentType);

        Optional<AppointmentType> foundAppointmentType = appointmentTypeRepository.findById(savedAppointmentType.getId());

        assertThat(foundAppointmentType).isPresent();
        assertThat(foundAppointmentType.get().getTitle()).isEqualTo("Consultation");
    }

    @Test
    void shouldUpdateAppointmentType(){

        AppointmentType appointmentType = AppointmentType.builder()
                                            .title("Consultation")
                                            .build();

        appointmentTypeRepository.save(appointmentType);

        appointmentType.setTitle("Surgery");
        appointmentTypeRepository.save(appointmentType);

        Optional<AppointmentType> updatedAppointmentType = appointmentTypeRepository.findById(appointmentType.getId());

        assertThat(updatedAppointmentType).isPresent();
        assertThat(updatedAppointmentType.get().getTitle()).isEqualTo("Surgery");
    }

    @Test
    void shouldDeleteAppointmentType(){

        AppointmentType appointmentType = AppointmentType.builder()
                                            .title("Consultation")
                                            .build();

        appointmentTypeRepository.save(appointmentType);

        UUID deleteId = appointmentType.getId();
        appointmentTypeRepository.deleteById(deleteId);

        assertThat(appointmentTypeRepository.existsById(deleteId)).isFalse();
    }
}
