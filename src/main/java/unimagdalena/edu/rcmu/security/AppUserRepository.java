package unimagdalena.edu.rcmu.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String>{
    Optional<AppUser> findByEmailIgnoreCase(String email);
    boolean exisexistsByEmailIgnoreCase(String email);
}
