! All these commands must be done in the api folder !

To generate target folder :

    mvn install

To generate the native executable :

    mvnw package -Pnative -Dnative-image.docker-build=true
        or 
    mvnw package -Pnative -Dnative-image.container-runtime=docker

    This generation has provided a Dockerfile.native in the src/main/docker

To build the Dockerfile.native :

    docker build -f src/main/docker/Dockerfile.native -t simba .
    
You can instantiate a postgresql dabase via docker by using the command line below 
    docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name postgres-quarkus-hibernate -e        POSTGRES_USER=root -e POSTGRES_PASSWORD=root -e POSTGRES_DB=simba -p 5433:5432 postgres:10.5

To run the simba image :

    docker run -i -p 8080:8080 simba
OR with the command below if you are using a postresql intance via docker :
    docker run -ti -p 8080:8080 --link postgres-quarkus-hibernate quarkus/api -Dquarkus.datasource.url=jdbc:postgresql://postgres:5432/postgres-quarkus-hibernate
