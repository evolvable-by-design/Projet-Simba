# Backend

We are using [Spring Boot](https://spring.io/projects/spring-boot).

## Prerequisites

- [Maven](https://maven.apache.org/)
- [Mysql](https://www.mysql.com/fr/)

## How to use

This section provides a quick start guide on to use this application.

### Launch with Docker

You can launch the project using `Run.sh`.

### Launch locally

1. Start by creating a database using mysql with the name `doodle`.
2. Go to [EtherPad lite](https://github.com/ether/etherpad-lite) and `git clone` the project.
3. `copy/paste` the file `APIKEY.txt` from `Projet-Simba/api` to the folder `etherpad-lite/` just cloned.
4. Start the Etherpad server using `etherpad-lite/bin/run.sh`.
5. `cd` into `Projet-Simba/api` and run the backend server using `mvn install && mvn spring-boot:run`
6. Enjoy ! 
