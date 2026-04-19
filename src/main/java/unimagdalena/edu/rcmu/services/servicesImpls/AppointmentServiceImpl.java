package unimagdalena.edu.rcmu.services.servicesImpls;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCancelRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCompleteRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCreateRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentResponse;
import unimagdalena.edu.rcmu.entities.Appointment;
import unimagdalena.edu.rcmu.entities.AppointmentType;
import unimagdalena.edu.rcmu.entities.Doctor;
import unimagdalena.edu.rcmu.entities.DoctorSchedule;
import unimagdalena.edu.rcmu.entities.Office;
import unimagdalena.edu.rcmu.entities.Patient;
import unimagdalena.edu.rcmu.enums.AppointmentStatus;
import unimagdalena.edu.rcmu.enums.OfficeStatus;
import unimagdalena.edu.rcmu.enums.PatientStatus;
import unimagdalena.edu.rcmu.exceptions.BadRequestException;
import unimagdalena.edu.rcmu.exceptions.NotFoundException;
import unimagdalena.edu.rcmu.mappers.AppointmentMapper;
import unimagdalena.edu.rcmu.repositories.AppointmentRepository;
import unimagdalena.edu.rcmu.repositories.AppointmentTypeRepository;
import unimagdalena.edu.rcmu.repositories.DoctorRepository;
import unimagdalena.edu.rcmu.repositories.DoctorScheduleRepository;
import unimagdalena.edu.rcmu.repositories.OfficeRepository;
import unimagdalena.edu.rcmu.repositories.PatientRepository;
import unimagdalena.edu.rcmu.services.service.AppointmentService;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentRepository appointmentRepository;

    private final DoctorRepository doctorRepository;

    private final PatientRepository patientRepository;

    private final OfficeRepository officeRepository;

    private final DoctorScheduleRepository doctorScheduleRepository;
    
    private final AppointmentTypeRepository appointmentTypeRepository;
    
    @Override
    public AppointmentResponse create(AppointmentCreateRequest request){
             
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));

        Office office = officeRepository.findById(request.officeId())
                .orElseThrow(() -> new NotFoundException("Office not found"));

        AppointmentType type = appointmentTypeRepository.findById(request.appointmentTypeId())
                .orElseThrow(() -> new NotFoundException("Appointment type not found"));

        if (patient.getStatus() == PatientStatus.BLOCKED || patient.getStatus() == PatientStatus.INACTIVE) {
            throw new BadRequestException("Patient can not schedule");
        }

        if (office.getStatus() == OfficeStatus.INACTIVE || office.getStatus() == OfficeStatus.MAINTENANCE) {
            throw new BadRequestException("Office can not schedule");
        }

        Instant present = Instant.now();
        if (request.startAt().isBefore(present)) {
            throw new BadRequestException("Appointment can not be created in the past");
        }

        Instant endAt = request.startAt().plus(Duration.ofMinutes(type.getDurationMinutes()));

        DayOfWeek dayOfWeek = request.startAt()
                .atZone(ZoneId.systemDefault())
                .getDayOfWeek();

        DoctorSchedule schedule = doctorScheduleRepository
                .findByDoctorIdAndDayOfWeek(doctor.getId(), dayOfWeek)
                .orElseThrow(() -> new BadRequestException("Doctor has no schedule for this day"));

        LocalTime appointmentStart = request.startAt()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        LocalTime appointmentEnd = endAt
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        if (appointmentStart.isBefore(schedule.getStartAt()) || appointmentEnd.isAfter(schedule.getEndAt())) {
            throw new BadRequestException("Appointment is outside doctor's working hours");
        }

        Long countTraslapesDoctor = appointmentRepository
                .countTraslapeDoctorAppointments(doctor.getId(), request.startAt(), endAt);

        if (countTraslapesDoctor > 0) {
            throw new BadRequestException("Doctor already has an appointment in that time range");
        }

        Long countTraslapesOffice = appointmentRepository
                .countTraslapeOfficeAppointments(office.getId(), request.startAt(), endAt);

        if (countTraslapesOffice > 0) {
            throw new BadRequestException("Office already has an appointment in that time range");
        }

        Long countOverLappingPatients = appointmentRepository
                .countOverlappingPatientAppointments(patient.getId(), request.startAt(), endAt);

        if (countOverLappingPatients > 0) {
            throw new BadRequestException("Patient already has an active appointment in that time range");
        }

        Appointment appointment = Appointment.builder()
                .title(request.title())
                .startAt(request.startAt())
                .endAt(endAt)
                .status(AppointmentStatus.SCHEDULED)
                .doctor(doctor)
                .patient(patient)
                .office(office)
                .appointmentType(type)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return AppointmentMapper.toResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse confirm(UUID appointmentId) {
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if(appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED
            || appointment.getStatus() == AppointmentStatus.NO_SHOW){
                throw new BadRequestException("Appointment can not confirm");
        }

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BadRequestException("Only scheduled appointments can be confirmed");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(Instant.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return AppointmentMapper.toResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse cancel(UUID appointmentId, AppointmentCancelRequest request) {
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if(appointment.getStatus() != AppointmentStatus.SCHEDULED && appointment.getStatus() != AppointmentStatus.CONFIRMED){
            throw new BadRequestException("Appointment can not cancelled");
        }

        if (request.reason() == null || request.reason().isBlank()) {
            throw new BadRequestException("Cancellation reason is required");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(request.reason());
        appointment.setUpdatedAt(Instant.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return AppointmentMapper.toResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse complete(UUID appointmentId, AppointmentCompleteRequest request){

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if(appointment.getStatus() != AppointmentStatus.CONFIRMED){
            throw new BadRequestException("Only confirmed appointments can be completed");
        }

        if(Instant.now().isBefore(appointment.getStartAt())){
            throw new BadRequestException("Only confirmed appointments can be completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setAdministrativeNote(request.administrativeNotes());
        appointment.setUpdatedAt(Instant.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return AppointmentMapper.toResponse(savedAppointment);
    }

    public AppointmentResponse markNoShow(UUID appointmentId){

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
                
        if(appointment.getStatus() != AppointmentStatus.CONFIRMED){
            throw new BadRequestException("Only confirmed appointments can be marked as no-show");
        }

        if(Instant.now().isBefore(appointment.getStartAt())){
            throw new BadRequestException(("An appointment cannot be marked as no-show before its scheduled start time"));
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointment.setUpdatedAt(Instant.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return AppointmentMapper.toResponse(savedAppointment);
    }

    public AppointmentResponse get(UUID appointmentId){

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        return AppointmentMapper.toResponse(appointment);
    }

    public Page<AppointmentResponse> list(Pageable pageable){
        return appointmentRepository.findAll(pageable).map(AppointmentMapper::toResponse);
    }
}
