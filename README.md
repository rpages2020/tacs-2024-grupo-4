# tacs-2024-grupo-4

## Setup
### Docker
    docker pull --platform linux/x86_64 mysql:8.0
    docker run --platform linux/x86_64 --name mysql -p 3306:3306 -d -e 'MYSQL_ROOT_PASSWORD=P4ssw0rd!' mysql:8.0


