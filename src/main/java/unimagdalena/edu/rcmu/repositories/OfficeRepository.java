package unimagdalena.edu.rcmu.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import unimagdalena.edu.rcmu.entities.Office;

public interface OfficeRepository extends JpaRepository<Office,UUID>{

}
