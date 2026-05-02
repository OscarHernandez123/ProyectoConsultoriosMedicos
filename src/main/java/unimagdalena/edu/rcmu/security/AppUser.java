package unimagdalena.edu.rcmu.security;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Column @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column (nullable = false, unique = true, length = 120) private String email;
    @Column (nullable = false) private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="user_roles", joinColumns = @JoinColumn(name="user_id"))
    @Column Set<Role> roles;
}
