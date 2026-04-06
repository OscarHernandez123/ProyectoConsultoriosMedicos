package unimagdalena.edu.rcmu.services.servicesImpls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCancelRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCompleteRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCreateRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentResponse;
import unimagdalena.edu.rcmu.entities.Appointment;
import unimagdalena.edu.rcmu.entities.AppointmentType;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.DoctorSchedule;
import unimagdalena.edu.rcmu.entities.Office;
import unimagdalena.edu.rcmu.entities.Patient;
import unimagdalena.edu.rcmu.enums.AppointmentStatus;
import unimagdalena.edu.rcmu.enums.OfficeStatus;
import unimagdalena.edu.rcmu.enums.PatientStatus;
import unimagdalena.edu.rcmu.exceptions.BadRequestException;
import unimagdalena.edu.rcmu.repositories.AppointmentRepository;
import unimagdalena.edu.rcmu.repositories.AppointmentTypeRepository;
import unimagdalena.edu.rcmu.repositories.DoctorRepository;
import unimagdalena.edu.rcmu.repositories.DoctorScheduleRepository;
import unimagdalena.edu.rcmu.repositories.OfficeRepository;
import unimagdalena.edu.rcmu.repositories.PatientRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private UUID appointmentId;
    private UUID doctorId;
    private UUID patientId;
    private UUID officeId;
    private UUID appointmentTypeId;

    private Doctor doctor;
    private Patient patient;
    private Office office;
    private AppointmentType appointmentType;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        officeId = UUID.randomUUID();
        appointmentTypeId = UUID.randomUUID();

        doctor = Doctor.builder()
                .id(doctorId)
                .fullName("Dr. House")
                .email("house@test.com")
                .build();

        patient = Patient.builder()
                .id(patientId)
                .fullName("Juan Perez")
                .email("juan@test.com")
                .phone("3001234567")
                .status(PatientStatus.ACTIVE)
                .build();

        office = Office.builder()
                .id(officeId)
                .location("Consultorio 101")
                .status(OfficeStatus.ACTIVE)
                .build();

        appointmentType = AppointmentType.builder()
                .id(appointmentTypeId)
                .title("Consulta general")
                .durationMinutes(30)
                .build();
    }

    @Test
    void shouldNotAllowAppointmentInThePast() {
        Instant startAt = Instant.now().minus(1, ChronoUnit.HOURS);
        AppointmentCreateRequest request = buildCreateRequest(startAt);

        mockBasicEntities();

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> appointmentService.create(request)
        );

        assertEquals("Appointment can not be created in the past", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldNotAllowDoctorOverlap() {
        Instant startAt = futureStartAtAtHour(9, 0);
        AppointmentCreateRequest request = buildCreateRequest(startAt);

        mockBasicEntities();
        mockSchedule(startAt);

        when(appointmentRepository.countTraslapeDoctorAppointments(
                doctorId,
                startAt,
                startAt.plus(30, ChronoUnit.MINUTES)
        )).thenReturn(1L);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> appointmentService.create(request)
        );

        assertEquals("Doctor already has an appointment in that time range", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldNotAllowOfficeOverlap() {
        Instant startAt = futureStartAtAtHour(10, 0);
        AppointmentCreateRequest request = buildCreateRequest(startAt);

        mockBasicEntities();
        mockSchedule(startAt);

        when(appointmentRepository.countTraslapeDoctorAppointments(
                doctorId,
                startAt,
                startAt.plus(30, ChronoUnit.MINUTES)
        )).thenReturn(0L);

        when(appointmentRepository.countTraslapeOfficeAppointments(
                officeId,
                startAt,
                startAt.plus(30, ChronoUnit.MINUTES)
        )).thenReturn(1L);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> appointmentService.create(request)
        );

        assertEquals("Office already has an appointment in that time range", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldNotAllowAppointmentOutsideDoctorWorkingHours() {
        Instant startAt = futureStartAtAtHour(17, 45);
        AppointmentCreateRequest request = buildCreateRequest(startAt);

        mockBasicEntities();

        DayOfWeek dayOfWeek = startAt.atZone(ZoneId.systemDefault()).getDayOfWeek();

        DoctorSchedule limitedSchedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(dayOfWeek)
                .startAt(LocalTime.of(8, 0))
                .endAt(LocalTime.of(18, 0))
                .build();

        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek))
                .thenReturn(Optional.of(limitedSchedule));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> appointmentService.create(request)
        );

        assertEquals("Appointment is outside doctor's working hours", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldCalculateEndAtCorrectly() {
        Instant startAt = futureStartAtAtHour(11, 0);
        AppointmentCreateRequest request = buildCreateRequest(startAt);

        mockBasicEntities();
        mockSchedule(startAt);

        when(appointmentRepository.countTraslapeDoctorAppointments(
                doctorId,
                startAt,
                startAt.plus(30, ChronoUnit.MINUTES)
        )).thenReturn(0L);

        when(appointmentRepository.countTraslapeOfficeAppointments(
                officeId,
                startAt,
                startAt.plus(30, ChronoUnit.MINUTES)
        )).thenReturn(0L);

        when(appointmentRepository.countOverlappingPatientAppointments(
                patientId,
                startAt,
                startAt.plus(30, ChronoUnit.MINUTES)
        )).thenReturn(0L);

        Appointment savedAppointment = Appointment.builder()
                .id(appointmentId)
                .title(request.title())
                .startAt(startAt)
                .endAt(startAt.plus(30, ChronoUnit.MINUTES))
                .status(AppointmentStatus.SCHEDULED)
                .doctor(doctor)
                .patient(patient)
                .office(office)
                .appointmentType(appointmentType)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentResponse response = appointmentService.create(request);

        assertNotNull(response);
        assertEquals(startAt.plus(30, ChronoUnit.MINUTES), response.endAt());
        assertEquals(AppointmentStatus.SCHEDULED, response.status());
    }

    @Test
    void shouldCancelScheduledAppointmentCorrectly() {
        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .title("Consulta")
                .startAt(futureStartAtAtHour(9, 0))
                .endAt(futureStartAtAtHour(9, 30))
                .status(AppointmentStatus.SCHEDULED)
                .doctor(doctor)
                .patient(patient)
                .office(office)
                .appointmentType(appointmentType)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentCancelRequest request = new AppointmentCancelRequest("Paciente no puede asistir");

        AppointmentResponse response = appointmentService.cancel(appointmentId, request);

        assertNotNull(response);
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        assertEquals("Paciente no puede asistir", appointment.getCancellationReason());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldCompleteConfirmedAppointmentCorrectly() {
        Instant startAt = Instant.now().minus(1, ChronoUnit.HOURS);

        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .title("Consulta")
                .startAt(startAt)
                .endAt(startAt.plus(30, ChronoUnit.MINUTES))
                .status(AppointmentStatus.CONFIRMED)
                .doctor(doctor)
                .patient(patient)
                .office(office)
                .appointmentType(appointmentType)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentCompleteRequest request = new AppointmentCompleteRequest("Consulta finalizada sin novedades");

        AppointmentResponse response = appointmentService.complete(appointmentId, request);

        assertNotNull(response);
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        assertEquals("Consulta finalizada sin novedades", appointment.getAdministrativeNote());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldNotCompleteAppointmentBeforeStartTime() {
        Instant startAt = Instant.now().plus(2, ChronoUnit.HOURS);

        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .title("Consulta")
                .startAt(startAt)
                .endAt(startAt.plus(30, ChronoUnit.MINUTES))
                .status(AppointmentStatus.CONFIRMED)
                .doctor(doctor)
                .patient(patient)
                .office(office)
                .appointmentType(appointmentType)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AppointmentCompleteRequest request = new AppointmentCompleteRequest("Nota administrativa");

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> appointmentService.complete(appointmentId, request)
        );

        assertEquals("Only confirmed appointments can be completed", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldMarkNoShowCorrectly() {
        Instant startAt = Instant.now().minus(2, ChronoUnit.HOURS);

        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .title("Consulta")
                .startAt(startAt)
                .endAt(startAt.plus(30, ChronoUnit.MINUTES))
                .status(AppointmentStatus.CONFIRMED)
                .doctor(doctor)
                .patient(patient)
                .office(office)
                .appointmentType(appointmentType)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentResponse response = appointmentService.markNoShow(appointmentId);

        assertNotNull(response);
        assertEquals(AppointmentStatus.NO_SHOW, appointment.getStatus());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void shouldNotMarkNoShowBeforeStartTime() {
        Instant startAt = Instant.now().plus(1, ChronoUnit.HOURS);

        Appointment appointment = Appointment.builder()
                .id(appointmentId)
                .title("Consulta")
                .startAt(startAt)
                .endAt(startAt.plus(30, ChronoUnit.MINUTES))
                .status(AppointmentStatus.CONFIRMED)
                .doctor(doctor)
                .patient(patient)
                .office(office)
                .appointmentType(appointmentType)
                .build();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> appointmentService.markNoShow(appointmentId)
        );

        assertEquals("An appointment cannot be marked as no-show before its scheduled start time", ex.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    private AppointmentCreateRequest buildCreateRequest(Instant startAt) {
        return new AppointmentCreateRequest(
                "Consulta médica",
                startAt,
                doctorId,
                patientId,
                officeId,
                appointmentTypeId
        );
    }

    private void mockBasicEntities() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(appointmentTypeRepository.findById(appointmentTypeId)).thenReturn(Optional.of(appointmentType));
    }

    private void mockSchedule(Instant startAt) {
        DayOfWeek dayOfWeek = startAt.atZone(ZoneId.systemDefault()).getDayOfWeek();

        DoctorSchedule doctorSchedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(dayOfWeek)
                .startAt(LocalTime.of(8, 0))
                .endAt(LocalTime.of(18, 0))
                .build();

        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek))
                .thenReturn(Optional.of(doctorSchedule));
    }

    private Instant futureStartAtAtHour(int hour, int minute) {
        return Instant.now()
                .plus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.systemDefault())
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0)
                .toInstant();
    }
}