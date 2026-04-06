package unimagdalena.edu.rcmu.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import unimagdalena.edu.rcmu.entities.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, UUID>{
    List<Doctor> findBySpecialtyId(UUID specialtyId);  
    
} 