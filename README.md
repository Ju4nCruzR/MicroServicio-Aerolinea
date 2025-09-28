✈️ Microservicio de Aerolínea – Proyecto Arquitectura de Software

Este repositorio contiene el microservicio de Aerolínea, desarrollado como parte del proyecto académico de Arquitectura de Software bajo el enfoque de microservicios. El sistema de Aerolínea se encarga de la gestión de vuelos, reservas, pasajeros, aeropuertos, destinos y asientos, y se integra con otros microservicios del ecosistema turístico como Banco y Hotel, siendo consumido a través del Portal Turístico.

📌 Características principales

Gestión de vuelos (CRUD).

Administración de pasajeros, destinos y aeropuertos.

Creación, consulta, confirmación y cancelación de reservas.

Asignación de asientos a pasajeros.

Exposición de APIs REST para integración con el portal turístico.

Seguridad mediante autenticación JWT.

Base de datos ligera H2 para desarrollo y pruebas.

🛠️ Tecnologías utilizadas

Backend: Java + Spring Boot

Frontend administrativo: Angular

Base de datos: H2 (en memoria, ideal para prototipado)

Persistencia: Spring Data JPA

Control de versiones: Git / GitHub

Pruebas de API: Postman

Documentación de API: Swagger / OpenAPI

⚙️ Instalación y ejecución
1. Clonar el repositorio
git clone https://github.com/tuusuario/microservicio-aerolinea.git
cd microservicio-aerolinea

2. Backend (Spring Boot)

Ejecutar la aplicación desde la raíz del proyecto backend:

./mvnw spring-boot:run


El servicio quedará disponible en:

http://localhost:8080

3. Frontend (Angular)

Ubicarse en la carpeta del frontend y ejecutar:

npm install
ng serve -o


La interfaz estará disponible en:

http://localhost:4200

🌐 Endpoints principales
Vuelos

GET /api/flights → Consultar vuelos disponibles.

POST /api/flights → Crear un vuelo.

PUT /api/flights/{id} → Actualizar vuelo.

DELETE /api/flights/{id} → Eliminar vuelo.

Reservas

POST /api/reservations → Crear reserva.

PUT /api/reservations/{id}/confirm → Confirmar reserva.

GET /api/reservations?passengerId=XXX → Consultar reservas por pasajero.

DELETE /api/reservations/{id} → Cancelar reserva.

🔐 Seguridad

Autenticación mediante JWT (Bearer Token).

Cada petición debe incluir el header:

Authorization: Bearer <token>

👨‍💻 Equipo

Este microservicio fue desarrollado por el Grupo Aerolínea como parte del curso de Arquitectura de Software. Forma parte de un ecosistema académico mayor junto con los microservicios de Banco y Hotel, consumidos por el Portal Turístico.

📄 Licencia

Este proyecto es de carácter académico y no cuenta con una licencia comercial.
