package unimagdalena.edu.rcmu.services.service;

import java.util.UUID;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCancelRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCompleteRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentCreateRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentDtos.AppointmentResponse;

public interface AppointmentService {
    AppointmentResponse create(AppointmentCreateRequest request);
    AppointmentResponse confirm(UUID appointmentId);
    AppointmentResponse cancel(UUID appointmentId, AppointmentCancelRequest reason);
    AppointmentResponse complete(UUID appointmentId, AppointmentCompleteRequest administrativeNote);
    AppointmentResponse markNoShow(UUID appointmentId);
}
