# tacs-2024-grupo-4

## Setup
### Docker
    docker pull --platform linux/x86_64 mongo:latest
    docker run --platform linux/x86_64 --name mongodb -p 27017:27017 -d -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=P4ssw0rd! mongo:latest

### BD
    docker exec -it mongodb mongodb -u root -p P4ssw0rd! --authenticationDatabase admin --eval "db = db.getSiblingDB('tac_grupo_4'); db.createCollection('placeholder');"

### Docker Compose
    docker-compose up -d

