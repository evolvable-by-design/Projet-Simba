To generate the native executable : 

    mvnw package -Pnative -Dnative-image.docker-build=true
        or 
    mvnw package -Pnative -Dnative-image.container-runtime=docker

    This generation has provided a Dockerfile.native in the src/main/docker

To build the Dockerfile.native :

    docker build -f src/main/docker/Dockerfile.native -t simba .

To run the simba image :

    docker run -i -p 8080:8080 simba