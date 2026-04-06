package unimagdalena.edu.rcmu.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import unimagdalena.edu.rcmu.entities.DoctorSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;


public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID>{

    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);

    boolean existsByDoctorIdAndDayOfWeekAndStartAtLessThanAndEndAtGreaterThan(UUID doctorId, DayOfWeek dayOfWeek, LocalTime endAt, LocalTime startAt);
}
