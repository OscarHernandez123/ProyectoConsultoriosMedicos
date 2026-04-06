package unimagdalena.edu.rcmu.services.servicesImpls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import unimagdalena.edu.rcmu.dtos.AvailabilityDtos.AvailabilitySlotResponse;
import unimagdalena.edu.rcmu.entities.Appointment;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.DoctorSchedule;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.repositories.AppointmentRepository;
import unimagdalena.edu.rcmu.repositories.DoctorScheduleRepository;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    private UUID doctorId;
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private Doctor doctor;
    private DoctorSchedule schedule;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        date = LocalDate.now().plusDays(1);
        dayOfWeek = date.getDayOfWeek();

        doctor = Doctor.builder()
                .id(doctorId)
                .fullName("Dr. Strange")
                .email("doctor@test.com")
                .build();

        schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(dayOfWeek)
                .startAt(LocalTime.of(8, 0))
                .endAt(LocalTime.of(12, 0))
                .build();
    }

    @Test
    void shouldThrowWhenScheduleNotFound() {
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> availabilityService.getAvailableSlots(doctorId, date)
        );

        assertEquals("Schedule not found", ex.getMessage());
    }

    @Test
    void shouldReturnWholeScheduleWhenThereAreNoAppointments() {
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek))
                .thenReturn(Optional.of(schedule));

        Instant dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(appointmentRepository.findAppointmentsByDoctorAndDate(doctorId, dayStart, dayEnd))
                .thenReturn(List.of());

        List<AvailabilitySlotResponse> result = availabilityService.getAvailableSlots(doctorId, date);

        assertEquals(1, result.size());
        assertEquals(toInstant(date, 8, 0), result.get(0).startAt());
        assertEquals(toInstant(date, 12, 0), result.get(0).endAt());
    }

    @Test
    void shouldReturnTwoFreeSpacesWhenThereIsOneAppointmentInTheMiddle() {
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek))
                .thenReturn(Optional.of(schedule));

        Instant dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Appointment appointment = Appointment.builder()
                .startAt(toInstant(date, 9, 0))
                .endAt(toInstant(date, 10, 0))
                .build();

        when(appointmentRepository.findAppointmentsByDoctorAndDate(doctorId, dayStart, dayEnd))
                .thenReturn(List.of(appointment));

        List<AvailabilitySlotResponse> result = availabilityService.getAvailableSlots(doctorId, date);

        assertEquals(2, result.size());

        assertEquals(toInstant(date, 8, 0), result.get(0).startAt());
        assertEquals(toInstant(date, 9, 0), result.get(0).endAt());

        assertEquals(toInstant(date, 10, 0), result.get(1).startAt());
        assertEquals(toInstant(date, 12, 0), result.get(1).endAt());
    }

    @Test
    void shouldReturnMultipleFreeSpacesWhenThereAreSeveralAppointments() {
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek))
                .thenReturn(Optional.of(schedule));

        Instant dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Appointment appointment1 = Appointment.builder()
                .startAt(toInstant(date, 8, 30))
                .endAt(toInstant(date, 9, 0))
                .build();

        Appointment appointment2 = Appointment.builder()
                .startAt(toInstant(date, 10, 0))
                .endAt(toInstant(date, 10, 30))
                .build();

        Appointment appointment3 = Appointment.builder()
                .startAt(toInstant(date, 11, 0))
                .endAt(toInstant(date, 11, 30))
                .build();

        when(appointmentRepository.findAppointmentsByDoctorAndDate(doctorId, dayStart, dayEnd))
                .thenReturn(List.of(appointment1, appointment2, appointment3));

        List<AvailabilitySlotResponse> result = availabilityService.getAvailableSlots(doctorId, date);

        assertEquals(4, result.size());

        assertEquals(toInstant(date, 8, 0), result.get(0).startAt());
        assertEquals(toInstant(date, 8, 30), result.get(0).endAt());

        assertEquals(toInstant(date, 9, 0), result.get(1).startAt());
        assertEquals(toInstant(date, 10, 0), result.get(1).endAt());

        assertEquals(toInstant(date, 10, 30), result.get(2).startAt());
        assertEquals(toInstant(date, 11, 0), result.get(2).endAt());

        assertEquals(toInstant(date, 11, 30), result.get(3).startAt());
        assertEquals(toInstant(date, 12, 0), result.get(3).endAt());
    }

    @Test
    void shouldReturnEmptyListWhenAppointmentOccupiesWholeSchedule() {
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek))
                .thenReturn(Optional.of(schedule));

        Instant dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Appointment appointment = Appointment.builder()
                .startAt(toInstant(date, 8, 0))
                .endAt(toInstant(date, 12, 0))
                .build();

        when(appointmentRepository.findAppointmentsByDoctorAndDate(doctorId, dayStart, dayEnd))
                .thenReturn(List.of(appointment));

        List<AvailabilitySlotResponse> result = availabilityService.getAvailableSlots(doctorId, date);

        assertEquals(0, result.size());
        assertIterableEquals(List.of(), result);
    }

    @Test
    void shouldSortAppointmentsBeforeCalculatingFreeSpaces() {
        when(doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek))
                .thenReturn(Optional.of(schedule));

        Instant dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        Appointment laterAppointment = Appointment.builder()
                .startAt(toInstant(date, 10, 0))
                .endAt(toInstant(date, 11, 0))
                .build();

        Appointment earlierAppointment = Appointment.builder()
                .startAt(toInstant(date, 8, 30))
                .endAt(toInstant(date, 9, 0))
                .build();

        when(appointmentRepository.findAppointmentsByDoctorAndDate(doctorId, dayStart, dayEnd))
                .thenReturn(List.of(laterAppointment, earlierAppointment));

        List<AvailabilitySlotResponse> result = availabilityService.getAvailableSlots(doctorId, date);

        assertEquals(3, result.size());

        assertEquals(toInstant(date, 8, 0), result.get(0).startAt());
        assertEquals(toInstant(date, 8, 30), result.get(0).endAt());

        assertEquals(toInstant(date, 9, 0), result.get(1).startAt());
        assertEquals(toInstant(date, 10, 0), result.get(1).endAt());

        assertEquals(toInstant(date, 11, 0), result.get(2).startAt());
        assertEquals(toInstant(date, 12, 0), result.get(2).endAt());
    }

    private Instant toInstant(LocalDate date, int hour, int minute) {
        return date.atTime(hour, minute)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }
}