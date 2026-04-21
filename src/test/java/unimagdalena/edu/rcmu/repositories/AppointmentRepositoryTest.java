package unimagdalena.edu.rcmu.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import unimagdalena.edu.rcmu.entities.Appointment;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.Office;
import unimagdalena.edu.rcmu.entities.Patient;
import unimagdalena.edu.rcmu.entities.Specialty;
import unimagdalena.edu.rcmu.enums.AppointmentStatus;
import unimagdalena.edu.rcmu.enums.OfficeStatus;
import unimagdalena.edu.rcmu.enums.PatientStatus;

public class AppointmentRepositoryTest extends AbstractRepositoryIT{

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    OfficeRepository officeRepository;

    @Autowired
    AppointmentTypeRepository appointmentTypeRepository;

    @Autowired
    SpecialtyRepository specialtyRepository;

    @Test
    void shouldCreateAppointment(){

        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(Instant.now())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Consultation");
    }

    @Test
    void shouldReadAppointment(){

        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(Instant.now())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        Optional<Appointment> found = appointmentRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Consultation");
    }

    @Test
    void shouldUpdateAppointment(){

        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(Instant.now())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        appointmentRepository.save(appointment);

        appointment.setTitle("Surgery");
        appointmentRepository.save(appointment);

        Optional<Appointment> updated = appointmentRepository.findById(appointment.getId());

        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("Surgery");
    }

    @Test
    void shouldDeleteAppointment(){

        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(Instant.now())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        appointmentRepository.save(appointment);

        UUID id = appointment.getId();
        appointmentRepository.deleteById(id);

        assertThat(appointmentRepository.existsById(id)).isFalse();
    }

    @Test
    void findByPatientIdAndStatus(){

        Patient patient = Patient.builder()
                            .fullName("Oscar Turizo")
                            .phone("911")
                            .email("oscar@gmail.com")
                            .status(PatientStatus.ACTIVE)
                            .build();
        
        Patient savedPatient = patientRepository.save(patient);

        Instant startTime = Instant.now();
        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(startTime)
                .status(AppointmentStatus.SCHEDULED)
                .patient(patient)
                .build();

        appointmentRepository.save(appointment);

        List<Appointment> appointments = appointmentRepository.findByPatientIdAndStatus(savedPatient.getId(), AppointmentStatus.SCHEDULED);

        assertThat(appointments).hasSize(1);
        assertThat(appointments.get(0).getTitle()).isEqualTo("Consultation");
        assertThat(appointments.get(0).getStartAt()).isEqualTo(startTime);
        
    }

    @Test
    void findByStartAtBetween(){

        Instant start = Instant.now();

        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(start)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        List<Appointment> appointments = appointmentRepository.findByStartAtBetween(
                start.minusSeconds(3600),
                start.plusSeconds(3600)
        );

        assertThat(appointments).hasSize(1);
        assertThat(appointments.get(0).getTitle()).isEqualTo("Consultation");
        assertThat(appointments.get(0).getStartAt()).isEqualTo(savedAppointment.getStartAt());
    }

    @Test
    void countTraslapeDoctorAppointments(){

        Doctor doctor = Doctor.builder()
                        .fullName("Oscar Turizo")
                        .email("oscar@gmail.com")
                        .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        Instant start = Instant.now();
        Instant end = start.plusSeconds(1800);

        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(start)
                .endAt(end)
                .status(AppointmentStatus.SCHEDULED)
                .doctor(savedDoctor)
                .build();

        appointmentRepository.save(appointment);

        Long countTraslapes = appointmentRepository.countTraslapeDoctorAppointments(
                savedDoctor.getId(),
                start.minusSeconds(3600),
                end.plusSeconds(3600)
        );

        assertThat(countTraslapes).isEqualTo(1);
    }

    @Test
    void countTraslapeOfficeAppointments(){

        Office office = Office.builder()
                        .location("Sincelejo")
                        .status(OfficeStatus.ACTIVE)
                        .build();

        Office savedOffice = officeRepository.save(office);

        Instant start = Instant.now();
        Instant end = start.plusSeconds(1800);

        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(start)
                .endAt(end)
                .status(AppointmentStatus.SCHEDULED)
                .office(savedOffice)
                .build();

        appointmentRepository.save(appointment);

        Long countTraslapes = appointmentRepository.countTraslapeOfficeAppointments(
                savedOffice.getId(),
                start.minusSeconds(3600),
                end.plusSeconds(3600)
        );

        assertThat(countTraslapes).isEqualTo(1);
    }


    @Test
    void countCancelledOrNoShowAppointmentBySpecialty(){

        Specialty specialty = Specialty.builder()
                                .title("Pediatric")
                                .build();

        Specialty savedSpecialty = specialtyRepository.save(specialty);

        Doctor doctor = Doctor.builder()
                        .fullName("Oscar Turizo")
                        .email("oscar@gmail.com")
                        .specialty(savedSpecialty)
                        .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        Instant start = Instant.now();
        Instant end = start.plusSeconds(1800);

        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(start)
                .endAt(end)
                .status(AppointmentStatus.CANCELLED)
                .doctor(savedDoctor)
                .build();

        appointmentRepository.save(appointment);

        Long countCancelledOrNoShow = appointmentRepository.countCancelledOrNoShowAppointmentBySpecialty(savedSpecialty.getId());

        assertThat(countCancelledOrNoShow).isEqualTo(1);
    }

}
