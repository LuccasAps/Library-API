# build
FROM maven:3.9.9-amazoncorretto-21-debian-bookworm as build
WORKDIR /build

COPY . .

RUN mvn clean package -DskipTests


# run

FROM amazoncorretto:21.0.5
WORKDIR /app

COPY --from=build ./build/target/*.jar ./libraryapi.jar

EXPOSE 8080
EXPOSE 9090

ENV DATASOURCE_URL=''
ENV DATASOURCE_USER=''
ENV DATASOURCE_PASSWORD=''
ENV GOOGLE_CLIENT_ID='client-id'
ENV GOOGLE_CLIENT_SECRET='client-id'

ENV SPRING_PROFILE_ACTIVE='producao'
ENV TZ='America/Sao_Paulo'

ENTRYPOINT java -jar libraryapi.jar