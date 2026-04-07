# Sistema de Reservas de Consultorios Universitarios

API REST desarrollada con Spring Boot para la gestión de citas médicas en un entorno universitario.

Permite administrar pacientes, doctores, especialidades, consultorios, tipos de cita, horarios y reservas médicas, garantizando reglas de negocio como control de traslapes, disponibilidad y estados de citas.

---

## Tecnologías utilizadas

* Java 21
* Spring Boot 4
* Spring Data JPA
* PostgreSQL
* Testcontainers
* JUnit 5
* Mockito

---

## Arquitectura

El proyecto está organizado por capas:

* entities: modelo de datos
* dtos: objetos de transferencia (request/response)
* mappers: conversión entre entidades y DTOs
* repositories: acceso a datos mediante JPA y JPQL
* services:

  * service: interfaces
  * servicesImpls: implementaciones
* exceptions: manejo de errores

---

## Modelo de datos

El sistema maneja las siguientes entidades principales:

* Patient
* Doctor
* Specialty
* Office
* AppointmentType
* DoctorSchedule
* Appointment

Relaciones principales:

* Un doctor pertenece a una especialidad
* Un doctor tiene múltiples horarios de atención
* Un paciente puede tener múltiples citas
* Una cita relaciona doctor, paciente, consultorio y tipo de cita

---

## Reglas de negocio implementadas

### Creación de citas

* No se permite crear citas en el pasado
* Validación de existencia y estado de paciente, doctor y consultorio
* La cita debe estar dentro del horario del doctor
* El campo endAt se calcula automáticamente a partir del tipo de cita
* No se permiten traslapes de horario para:

  * doctor
  * consultorio
  * paciente
* Toda cita se crea con estado inicial SCHEDULED

---

### Estados de cita

Estados posibles:

* SCHEDULED
* CONFIRMED
* CANCELLED
* COMPLETED
* NO_SHOW

Reglas:

* Solo citas SCHEDULED pueden pasar a CONFIRMED
* Solo citas CONFIRMED pueden pasar a COMPLETED o NO_SHOW
* La cancelación requiere un motivo

---

### Disponibilidad y reportes

* Generación de slots disponibles por doctor y fecha
* Cálculo de ocupación de consultorios
* Ranking de doctores por citas completadas
* Identificación de pacientes con mayor número de inasistencias

---

## Testing

### Repository (Integration Tests)

* Uso de Testcontainers con PostgreSQL
* Validación de consultas:

  * traslapes
  * rangos de fechas
  * agregaciones

### Service (Unit Tests)

* Uso de Mockito
* Validación de reglas de negocio:

  * traslapes
  * fechas inválidas
  * cambios de estado

---

## Autor

Oscar Turizo
