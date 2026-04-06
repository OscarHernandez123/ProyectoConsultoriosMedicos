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
import unimagdalena.edu.rcmu.enums.AppointmentStatus;
import jakarta.persistence.GenerationType;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id @GeneratedValue (strategy = GenerationType.UUID) private UUID id;
    @Column (nullable = false) private String title;
    @Column (nullable = false, name = "start_at") private Instant startAt;
    @Column (name = "end_at") private Instant endAt;
    @Enumerated(EnumType.STRING) @Column (nullable = false) private AppointmentStatus status;
    @Column (name = "created_at") private Instant createdAt;
    @Column (name = "updated_at") private Instant updatedAt;
    @Column (name = "cancellation_reason") private String cancellationReason;
    @Column (name = "administrative_note") private String administrativeNote;
    @ManyToOne @JoinColumn (name = "doctor_id") private Doctor doctor;
    @ManyToOne @JoinColumn (name = "patient_id") private Patient patient;
    @ManyToOne @JoinColumn (name = "office_id") private Office office;
    @ManyToOne @JoinColumn (name = "appointment_type_id") private AppointmentType appointmentType;
}
