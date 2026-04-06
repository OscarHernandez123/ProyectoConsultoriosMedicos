package unimagdalena.edu.rcmu.services.servicesImpls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleCreateRequest;
import unimagdalena.edu.rcmu.dtos.DoctorScheduleDtos.DoctorScheduleResponse;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.DoctorProfile;
import unimagdalena.edu.rcmu.entities.DoctorSchedule;
import unimagdalena.edu.rcmu.entities.Specialty;
import unimagdalena.edu.rcmu.exceptions.BadRequestException;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.repositories.DoctorRepository;
import unimagdalena.edu.rcmu.repositories.DoctorScheduleRepository;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceImplTest {

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorScheduleServiceImpl doctorScheduleService;

    private UUID doctorId;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();

        Specialty specialty = Specialty.builder()
                .id(UUID.randomUUID())
                .title("Cardiología")
                .build();

        DoctorProfile profile = DoctorProfile.builder()
                .id(UUID.randomUUID())
                .phone("3001234567")
                .bio("Cardiólogo")
                .build();

        doctor = Doctor.builder()
                .id(doctorId)
                .fullName("Oscar Turizo")
                .email("oscar@gmail.com")
                .specialty(specialty)
                .doctorProfile(profile)
                .build();
    }

    @Test
    void shouldCreateDoctorSchedule() {
        DoctorScheduleCreateRequest request = new DoctorScheduleCreateRequest(
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                doctorId
        );

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        when(doctorScheduleRepository
                .existsByDoctorIdAndDayOfWeekAndStartAtLessThanAndEndAtGreaterThan(
                        doctorId,
                        DayOfWeek.MONDAY,
                        LocalTime.of(8, 0),
                        LocalTime.of(16, 0)
                )).thenReturn(false);

        DoctorSchedule savedSchedule = DoctorSchedule.builder()
                .id(UUID.randomUUID())
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startAt(LocalTime.of(8, 0))
                .endAt(LocalTime.of(16, 0))
                .build();

        when(doctorScheduleRepository.save(any(DoctorSchedule.class))).thenReturn(savedSchedule);

        DoctorScheduleResponse response = doctorScheduleService.create(request);

        assertNotNull(response);
        assertEquals(DayOfWeek.MONDAY, response.dayOfWeek());
        assertEquals(LocalTime.of(8, 0), response.startAt());
        assertEquals(LocalTime.of(16, 0), response.endAt());
    }

    @Test
    void shouldNotCreateDoctorScheduleWhenDoctorNotFound() {
        DoctorScheduleCreateRequest request = new DoctorScheduleCreateRequest(
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                doctorId
        );

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> doctorScheduleService.create(request)
        );

        assertEquals("Doctor not found", ex.getMessage());
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateDoctorScheduleWhenStartAtIsAfterEndAt() {
        DoctorScheduleCreateRequest request = new DoctorScheduleCreateRequest(
                DayOfWeek.MONDAY,
                LocalTime.of(16, 0),
                LocalTime.of(8, 0),
                doctorId
        );

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> doctorScheduleService.create(request)
        );

        assertEquals("Start time must be before end time", ex.getMessage());
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateDoctorScheduleWhenStartAtIsEqualToEndAt() {
        DoctorScheduleCreateRequest request = new DoctorScheduleCreateRequest(
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(8, 0),
                doctorId
        );

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> doctorScheduleService.create(request)
        );

        assertEquals("Start time must be before end time", ex.getMessage());
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateDoctorScheduleWhenOverlapExists() {
        DoctorScheduleCreateRequest request = new DoctorScheduleCreateRequest(
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                doctorId
        );

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        when(doctorScheduleRepository
                .existsByDoctorIdAndDayOfWeekAndStartAtLessThanAndEndAtGreaterThan(
                        doctorId,
                        DayOfWeek.MONDAY,
                        LocalTime.of(9, 0),
                        LocalTime.of(12, 0)
                )).thenReturn(true);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> doctorScheduleService.create(request)
        );

        assertEquals("Doctor schedule overlaps with an existing schedule", ex.getMessage());
        verify(doctorScheduleRepository, never()).save(any());
    }

    @Test
    void shouldListAllDoctorSchedules() {
        DoctorSchedule schedule1 = DoctorSchedule.builder()
                .id(UUID.randomUUID())
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startAt(LocalTime.of(8, 0))
                .endAt(LocalTime.of(12, 0))
                .build();

        DoctorSchedule schedule2 = DoctorSchedule.builder()
                .id(UUID.randomUUID())
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startAt(LocalTime.of(14, 0))
                .endAt(LocalTime.of(18, 0))
                .build();

        when(doctorScheduleRepository.findAll()).thenReturn(List.of(schedule1, schedule2));

        List<DoctorScheduleResponse> response = doctorScheduleService.listAll();

        assertEquals(2, response.size());

        assertEquals(DayOfWeek.MONDAY, response.get(0).dayOfWeek());
        assertEquals(LocalTime.of(8, 0), response.get(0).startAt());
        assertEquals(LocalTime.of(12, 0), response.get(0).endAt());

        assertEquals(DayOfWeek.TUESDAY, response.get(1).dayOfWeek());
        assertEquals(LocalTime.of(14, 0), response.get(1).startAt());
        assertEquals(LocalTime.of(18, 0), response.get(1).endAt());
    }
}
