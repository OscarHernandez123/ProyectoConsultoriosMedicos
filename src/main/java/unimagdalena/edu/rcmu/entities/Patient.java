package unimagdalena.edu.rcmu.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unimagdalena.edu.rcmu.enums.PatientStatus;
import jakarta.persistence.GenerationType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id @GeneratedValue (strategy = GenerationType.UUID) private UUID id;
    @Column (nullable = false, name = "full_name") private String fullName;
    @Column (nullable = false) private String phone;
    @Column (nullable = false) private String email;
    @Enumerated(EnumType.STRING) @Column (nullable = false) private PatientStatus status;
    @Column (name = "createdAt") private Instant createdAt;
    @Column (name = "updatedAt") private Instant updatedAt;
    @OneToMany (mappedBy = "patient") private List<Appointment> appointments;
}
