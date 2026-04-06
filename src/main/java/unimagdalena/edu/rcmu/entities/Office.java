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
import unimagdalena.edu.rcmu.enums.OfficeStatus;
import jakarta.persistence.GenerationType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="offices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Office {
    @Id @GeneratedValue (strategy = GenerationType.UUID) private UUID id;
    @Column (nullable = false) private String location;
    @Enumerated(EnumType.STRING) @Column (nullable = false) private OfficeStatus status;
    @Column (name = "create_at") private Instant createdAt;
    @Column (name = "updated_at") private Instant updatedAt;
    @OneToMany (mappedBy = "office") private List<Appointment> appointments;
}
