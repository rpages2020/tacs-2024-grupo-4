# tacs-2024-grupo-4

## Setup
### Links
[JDK23](https://www.oracle.com/ar/java/technologies/downloads/)
Revisar Module Settings

[Docker](https://docs.docker.com/get-started/get-docker/)

### Buildear
    .\gradlew build
    
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

Por ahora el bot no permite crear eventos con múltiples sectores ni reservar asientos específicos (ej por número de asiento).

Por ahora la API confía totalmente en las solicitudes del bot. Si la hacemos pública habría que agregar autenticación.

## NOTA CLOUD
se inhabilita checkstyle y test (./gradlew build -x test), para reducir tiempo y procesamiento
se cambia de instancia de t2.micro a t2.medium
