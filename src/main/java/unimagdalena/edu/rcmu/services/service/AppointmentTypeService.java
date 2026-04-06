package unimagdalena.edu.rcmu.services.service;

import java.util.List;

import unimagdalena.edu.rcmu.dtos.AppointmentTypeDtos.AppointmentTypeCreateRequest;
import unimagdalena.edu.rcmu.dtos.AppointmentTypeDtos.AppointmentTypeResponse;

public interface AppointmentTypeService{
    AppointmentTypeResponse create(AppointmentTypeCreateRequest request);
    List<AppointmentTypeResponse> listAll();
}
