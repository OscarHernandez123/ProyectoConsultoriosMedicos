package unimagdalena.edu.rcmu.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import unimagdalena.edu.rcmu.entities.Patient;

public interface PatientRepository extends JpaRepository<Patient, UUID>{

}
