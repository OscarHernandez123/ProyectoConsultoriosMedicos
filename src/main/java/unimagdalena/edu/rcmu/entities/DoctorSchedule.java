package unimagdalena.edu.rcmu.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.GenerationType;
import java.util.UUID;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name="doctors_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSchedule {
    @Id @GeneratedValue (strategy = GenerationType.UUID) private UUID id;
    @Enumerated(EnumType.STRING) @Column (nullable = false, name = "day_of_week") private DayOfWeek dayOfWeek;
    @Column (nullable = false, name = "start_at") private LocalTime startAt;
    @Column (nullable = false, name = "end_at") private LocalTime endAt;
    @ManyToOne @JoinColumn (name = "doctor_id") private Doctor doctor;
}
