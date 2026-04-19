package unimagdalena.edu.rcmu.repositories;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import unimagdalena.edu.rcmu.dtos.ReportDtos.DoctorProductivityResponse;
import unimagdalena.edu.rcmu.dtos.ReportDtos.NoShowPatientResponse;
import unimagdalena.edu.rcmu.dtos.ReportDtos.OfficeOccupancyResponse;
import unimagdalena.edu.rcmu.entities.Appointment;
import unimagdalena.edu.rcmu.enums.AppointmentStatus;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID>{

    List<Appointment> findByPatientIdAndStatus(UUID patientId, AppointmentStatus status);

    List<Appointment> findByStartAtBetween(Instant startAt, Instant endAt);

    @Query("""
            SELECT COUNT(a)
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
            AND a.status IN ('SCHEDULED', 'CONFIRMED')
            AND a.startAt < :newEnd
            AND a.endAt > :newStart
            """)
    Long countTraslapeDoctorAppointments(
            @Param("doctorId") UUID doctorId,
            @Param("newStart") Instant newStart,
            @Param("newEnd") Instant newEnd
    );

    @Query("""
            SELECT COUNT(a)
            FROM Appointment a
            WHERE a.office.id = :officeId
            AND a.status IN ('SCHEDULED', 'CONFIRMED')
            AND a.startAt < :newEnd
            AND a.endAt > :newStart
            """)
    Long countTraslapeOfficeAppointments(
            @Param("officeId") UUID officeId,
            @Param("newStart") Instant newStart,
            @Param("newEnd") Instant newEnd
    );

    @Query("""
            SELECT COUNT(a)
            FROM Appointment a
            WHERE a.patient.id = :patientId
            AND a.status IN ('SCHEDULED', 'CONFIRMED')
            AND a.startAt < :newEnd
            AND a.endAt > :newStart
            """)
        Long countOverlappingPatientAppointments(
            @Param("patientId") UUID patientId,
            @Param("newStart") Instant newStart,
            @Param("newEnd") Instant newEnd
        );

    @Query("""
            SELECT COUNT(a)
            FROM Appointment a
            WHERE a.doctor.specialty.id = :specialtyId
            AND (a.status = 'NO_SHOW' OR a.status = 'CANCELLED')
            """)
    Long countCancelledOrNoShowAppointmentBySpecialty(@Param("specialtyId") UUID specialtyId);
                                        
   @Query("""
            SELECT COUNT(a)
            FROM Appointment a
            WHERE a.patient.id = :patientId
            AND a.status = 'NO_SHOW'
            AND a.startAt BETWEEN :from AND :to
            """)
   Long countNoShowAppointmentsByPatientAndPeriod(
        @Param("patientId") UUID patientId,
        @Param("from") Instant from,
        @Param("to") Instant to
   );

   @Query("""
            SELECT a
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
            AND a.startAt < :dayEnd
            AND a.endAt > :dayStart
            AND a.status IN ('PENDING', 'CONFIRMED')
            ORDER BY a.startAt
            """)
   List<Appointment> findAppointmentsByDoctorAndDate(
        @Param("doctorId") UUID doctorId,
        @Param("dayStart") Instant dayStart,
        @Param("dayEnd")Instant dayEnd
   );

   @Query("""
            SELECT new unimagdalena.edu.rcmu.dtos.ReportDtos$OfficeOccupancyResponse(
                   a.office.id,
                   SUM(a.appointmentType.durationMinutes)
            )
            FROM Appointment a
            WHERE a.status = 'SCHEDULED'
              AND a.startAt BETWEEN :startDate AND :endDate
            GROUP BY a.office.id
            """)
    List<OfficeOccupancyResponse> getOfficeOccupation(
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );

    @Query("""
            SELECT new unimagdalena.edu.rcmu.dtos.ReportDtos$DoctorProductivityResponse(
                   a.doctor.id,
                   a.doctor.fullName,
                   COUNT(a)
            )
            FROM Appointment a
            WHERE a.status = 'COMPLETED'
            GROUP BY a.doctor.id, a.doctor.fullName
            ORDER BY COUNT(a) DESC
            """)
    List<DoctorProductivityResponse> getDoctorByAppointmentsCompleteDesc();

    @Query("""
            SELECT new unimagdalena.edu.rcmu.dtos.ReportDtos$NoShowPatientResponse(
                   a.patient.id,
                   a.patient.fullName,
                   COUNT(a)
            )
            FROM Appointment a
            WHERE a.status = 'NO_SHOW'
            GROUP BY a.patient.id, a.patient.fullName
            ORDER BY COUNT(a) DESC
            """)
    List<NoShowPatientResponse> getPatientbyAppointmentsNoShowDesc();
}
