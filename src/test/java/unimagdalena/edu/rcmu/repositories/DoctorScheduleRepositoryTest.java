package unimagdalena.edu.rcmu.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.DoctorSchedule;

public class DoctorScheduleRepositoryTest extends AbstractRepositoryIT{

    @Autowired
    DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Test
    void shouldCreateDoctorSchedule(){

        LocalTime startAt = LocalTime.of(8, 0);
        LocalTime endAt = LocalTime.of(16, 30);

        DoctorSchedule doctorSchedule = DoctorSchedule.builder()
                                        .dayOfWeek(DayOfWeek.MONDAY)
                                        .startAt(startAt)
                                        .endAt(endAt)
                                        .build();

        DoctorSchedule savedDoctorSchedule = doctorScheduleRepository.save(doctorSchedule);

        assertThat(savedDoctorSchedule.getId()).isNotNull();
        assertThat(savedDoctorSchedule.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(savedDoctorSchedule.getStartAt()).isEqualTo(LocalTime.of(8, 0));
        assertThat(savedDoctorSchedule.getEndAt()).isEqualTo(LocalTime.of(16, 30));
    }

    @Test
    void shouldReadDoctorSchedule(){

        LocalTime startAt = LocalTime.of(8, 0);
        LocalTime endAt = LocalTime.of(16, 30);

        DoctorSchedule doctorSchedule = DoctorSchedule.builder()
                                        .dayOfWeek(DayOfWeek.MONDAY)
                                        .startAt(startAt)
                                        .endAt(endAt)
                                        .build();

        DoctorSchedule savedDoctorSchedule = doctorScheduleRepository.save(doctorSchedule);

        Optional<DoctorSchedule> foundDoctorSchedule = doctorScheduleRepository.findById(savedDoctorSchedule.getId());

        assertThat(foundDoctorSchedule).isPresent();
        assertThat(foundDoctorSchedule.get().getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(foundDoctorSchedule.get().getStartAt()).isEqualTo(LocalTime.of(8, 0));
        assertThat(foundDoctorSchedule.get().getEndAt()).isEqualTo(LocalTime.of(16, 30));
    }

    @Test
    void shouldUpdateDoctorSchedule(){

        LocalTime startAt = LocalTime.of(8, 0);
        LocalTime endAt = LocalTime.of(16, 30);

        DoctorSchedule doctorSchedule = DoctorSchedule.builder()
                                        .dayOfWeek(DayOfWeek.MONDAY)
                                        .startAt(startAt)
                                        .endAt(endAt)
                                        .build();

        DoctorSchedule savedDoctorSchedule = doctorScheduleRepository.save(doctorSchedule);
        LocalTime newStartAt = LocalTime.of(9, 0);
        LocalTime newEndAt = LocalTime.of(16, 0);
        savedDoctorSchedule.setDayOfWeek(DayOfWeek.TUESDAY);
        savedDoctorSchedule.setStartAt(newStartAt);
        savedDoctorSchedule.setEndAt(newEndAt);

        Optional<DoctorSchedule> updatedDoctorSchedule = doctorScheduleRepository.findById(savedDoctorSchedule.getId());

        assertThat(updatedDoctorSchedule).isPresent();
        assertThat(updatedDoctorSchedule.get().getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
        assertThat(updatedDoctorSchedule.get().getStartAt()).isEqualTo(LocalTime.of(9, 0));
        assertThat(updatedDoctorSchedule.get().getEndAt()).isEqualTo(LocalTime.of(16, 0));
    }

    @Test
    void shouldDeleteDoctor(){

        LocalTime startAt = LocalTime.of(8, 0);
        LocalTime endAt = LocalTime.of(16, 30);

        DoctorSchedule doctorSchedule = DoctorSchedule.builder()
                                        .dayOfWeek(DayOfWeek.MONDAY)
                                        .startAt(startAt)
                                        .endAt(endAt)
                                        .build();

        doctorScheduleRepository.save(doctorSchedule);

        UUID deleteId = doctorSchedule.getId();
        doctorScheduleRepository.deleteById(doctorSchedule.getId());

        assertThat(doctorScheduleRepository.existsById(deleteId)).isFalse();
    }
    @Test
    void testFindByDoctorIdAndDayOfWeek() {
        
        Doctor doctor = Doctor.builder()
                        .fullName("Oscar Turizo")
                        .email("oscar@gmail.com")
                        .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        LocalTime startAt = LocalTime.of(8, 0);
        LocalTime endAt = LocalTime.of(16, 30);

        DoctorSchedule doctorSchedule = DoctorSchedule.builder()
                                        .dayOfWeek(DayOfWeek.MONDAY)
                                        .startAt(startAt)
                                        .endAt(endAt)
                                        .doctor(savedDoctor)
                                        .build();

        doctorScheduleRepository.save(doctorSchedule);

        Optional<DoctorSchedule> foundDoctorSchedule = doctorScheduleRepository.findByDoctorIdAndDayOfWeek(savedDoctor.getId(), DayOfWeek.MONDAY);

        assertThat(foundDoctorSchedule.get().getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);

    }
}
