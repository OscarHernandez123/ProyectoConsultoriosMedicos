package unimagdalena.edu.rcmu.services.servicesImpls;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.AvailabilityDtos.AvailabilitySlotResponse;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.entities.Appointment;
import unimagdalena.edu.rcmu.entities.DoctorSchedule;
import unimagdalena.edu.rcmu.repositories.AppointmentRepository;
import unimagdalena.edu.rcmu.repositories.DoctorScheduleRepository;
import unimagdalena.edu.rcmu.services.service.AvailabilityService;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService{

    private final DoctorScheduleRepository doctorScheduleRepository;

    private final AppointmentRepository appointmentRepository;
    
    @Override
    public List<AvailabilitySlotResponse> getAvailableSlots(UUID doctorId, LocalDate date) {
    
        DoctorSchedule schedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctorId, date.getDayOfWeek())
                .orElseThrow(() -> new NotFoundException("Schedule not found"));

        Instant scheduleStart = date.atTime(schedule.getStartAt())
                .atZone(ZoneId.systemDefault())
                .toInstant();

        Instant scheduleEnd = date.atTime(schedule.getEndAt())
                .atZone(ZoneId.systemDefault())
                .toInstant();

        Instant dayStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<Appointment> appointments = new ArrayList<>(
                appointmentRepository.findAppointmentsByDoctorAndDate(doctorId, dayStart, dayEnd)
        );

        appointments.sort(Comparator.comparing(Appointment::getStartAt));

        List<AvailabilitySlotResponse> freeSpaces = new ArrayList<>();

        Instant currentStart = scheduleStart;

        for (Appointment appointment : appointments) {

            if (appointment.getStartAt().isAfter(currentStart)) {
                freeSpaces.add(new AvailabilitySlotResponse(
                        currentStart,
                        appointment.getStartAt()
                ));
            }

            if (appointment.getEndAt().isAfter(currentStart)) {
                currentStart = appointment.getEndAt();
            }
        }

        if (currentStart.isBefore(scheduleEnd)) {
            freeSpaces.add(new AvailabilitySlotResponse(
                    currentStart,
                    scheduleEnd
            ));
        }

        return freeSpaces;
    }

}
