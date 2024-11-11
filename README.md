# tacs-2024-grupo-4

## Setup
### Links
[JDK23](https://www.oracle.com/ar/java/technologies/downloads/)
Revisar Module Settings

[Docker](https://docs.docker.com/get-started/get-docker/)

### Buildear
    .\gradlew buildAll
    
### Docker Compose
    docker-compose up -d
    
### Docker Compose changes
    docker-compose up -d --build

### Docker Compose clear all
    docker-compose down -v
        
### Telegram bot
Es necesario tener un token de un bot de telegram (registrar bot con el FatherBot).

Se pone en el src/main/resources/application.yaml.

### Debuggear API / bot
Una vez hecho el compose, detener el contenedor de la API y ejecutar Grupo4Aplicacion.java

Para subir cambios al contenedor, hacer el .\gradlew build y hacer un docker-compose up -d --build

## Notas
El flujo de la aplicación, de momento, es el siguiente:
1) El usuario crea su cuenta que queda asociada a su cuenta de Telegram (crearUsuario)
2) Se crea un evento (crearEvento)
3) Se confirma el evento (confirmarEvento). En este punto se crean las entidades Asiento
4) Se reservan asientos hasta que no haya disponibles (reservar)
5.1) El usuario que creo el evento puede cerrar la venta de entradas, recibiendo la estadistica de
las cantidades de tickets vendidos, la recaudación por sector y el porcentaje de ocupacion del evento. (cerrarVenta)
5.2) El ususario que creo un evento lo puede cancelar y con esto ya no es posible la reserva de tickets. (cancelarEvento)
6) El ususario que crea eventos puede ver la liosta de estos con el detalle del estado de las ventas. (misEventos)
7) Para los ususario que han comprado tickets, puede revisarlos con el coman do misTickets o misReservas.

la API soporta la creacion de eventos con diferentes sectores de ubicaciones.

Por ahora la API confía totalmente en las solicitudes del bot. Si la hacemos pública habría que agregar autenticación.

## NOTA CLOUD
se inhabilita checkstyle y test (./gradlew build -x test), para reducir tiempo y procesamiento
se cambia de instancia de t2.micro a t2.medium
