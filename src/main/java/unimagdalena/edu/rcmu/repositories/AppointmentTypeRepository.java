package unimagdalena.edu.rcmu.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import unimagdalena.edu.rcmu.entities.AppointmentType;

public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, UUID>{

}
