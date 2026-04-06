package unimagdalena.edu.rcmu.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import unimagdalena.edu.rcmu.entities.Appointment;
import unimagdalena.edu.rcmu.entities.AppointmentType;
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
    void getOfficeOccupation(){

        Office office = Office.builder()
                        .location("Sincelejo")
                        .status(OfficeStatus.ACTIVE)
                        .build();

        Office savedOffice = officeRepository.save(office);

        AppointmentType type = AppointmentType.builder()
                                .title("Consultation")
                                .durationMinutes(30)
                                .build();

        AppointmentType savedType = appointmentTypeRepository.save(type);

        Instant start = Instant.now();
        Instant end = start.plusSeconds(1800);

        Appointment appointment = Appointment.builder()
                .title("Consultation")
                .startAt(start)
                .endAt(end)
                .status(AppointmentStatus.SCHEDULED)
                .office(savedOffice)
                .appointmentType(savedType)
                .build();

        appointmentRepository.save(appointment);

        List<Object[]> officeOccupation = appointmentRepository.getOfficeOccupation(
                start.minusSeconds(3600),
                start.plusSeconds(3600)
        );

        assertThat(officeOccupation).hasSize(1);
        assertThat(officeOccupation.get(0)[0]).isEqualTo(savedOffice.getId());
        assertThat(officeOccupation.get(0)[1]).isEqualTo(30L);
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

    @Test
    void getDoctorByAppointmentsCompleteDesc(){

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
                .status(AppointmentStatus.COMPLETED)
                .doctor(savedDoctor)
                .build();

        appointmentRepository.save(appointment);

        List<Object[]> listOfDoctorComplete = appointmentRepository.getDoctorByAppointmentsCompleteDesc();

        assertThat(listOfDoctorComplete).hasSize(1);

        Object[] row = listOfDoctorComplete.get(0);

        UUID doctorId = (UUID) row[0];
        String fullName = (String) row[1];
        Long count = (Long) row[2];

        assertThat(doctorId).isEqualTo(savedDoctor.getId());
        assertThat(fullName).isEqualTo("Oscar Turizo");
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void getPatientbyAppointmentsNoShowDesc(){

        Patient patient = Patient.builder()
                            .fullName("Oscar Turizo")
                            .phone("911")
                            .email("oscar@gmail.com")
                            .status(PatientStatus.ACTIVE)
                            .build();

        Patient savedPatient = patientRepository.save(patient);

        Instant start = Instant.now();
        Instant end = start.plusSeconds(1800);

        Appointment appointment1 = Appointment.builder()
                .title("Consultation")
                .startAt(start)
                .endAt(end)
                .status(AppointmentStatus.NO_SHOW)
                .patient(savedPatient)
                .build();

        appointmentRepository.save(appointment1);

        Appointment appointment2 = Appointment.builder()
                .title("Consultation")
                .startAt(start)
                .endAt(end)
                .status(AppointmentStatus.CANCELLED)
                .patient(savedPatient)
                .build();

        appointmentRepository.save(appointment2);

        List<Object[]> result =
                appointmentRepository.getPatientbyAppointmentsNoShowDesc();

        assertThat(result).hasSize(1);

        Object[] row = result.get(0);

        UUID patientId = (UUID) row[0];
        String fullName = (String) row[1];
        Long count = (Long) row[2];

        assertThat(patientId).isEqualTo(savedPatient.getId());
        assertThat(fullName).isEqualTo("Oscar Turizo");
        assertThat(count).isEqualTo(1L);
    }
}
