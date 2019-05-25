####
# This docker image is a multi-stage image that first build the apps with Maven then create the image.
#
# To build it :
# docker build -f src/main/docker/Dockerfile -t loicmathieu/quarkus-demo .
#
# Then run the container using:
# docker run -ti --rm -p 8080:8080 loicmathieu/quarkus-demo
###

## Stage 1 : build with maven builder image with native capabilities
FROM quay.io/quarkus/centos-quarkus-maven:graalvm-1.0.0-rc15 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
# we will build a native image using the native maven profile
RUN mvn -f /usr/src/app/pom.xml -DskipTests -Pnative clean package

## Stage 2 : create the docker final image form a distroless image !
FROM cescoffier/native-base
# we copy from the previous build the artifacts
COPY --from=build /usr/src/app/target/*-runner /application
EXPOSE 8080
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]