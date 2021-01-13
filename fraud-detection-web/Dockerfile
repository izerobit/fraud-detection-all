# Start with a base image containing Java runtime
#FROM openjdk:8-jdk-alpine as job-build
#
#WORKDIR /home/gradle/app/
#
#COPY build.gradle settings.gradle ./
#COPY gradlew ./
#COPY gradle gradle
#COPY . .
#RUN ./gradlew build
#
## ---
#FROM openjdk:8-jdk-alpine
#
#COPY --from=job-build /home/gradle/app/build/libs/fraud-detection-web-1.0-SNAPSHOT.jar demo-backend.jar
#EXPOSE 5656
#ENTRYPOINT ["java","-jar","demo-backend.jar"]


FROM openjdk:12-alpine

COPY build/libs/fraud-detection-web-1.0-SNAPSHOT.jar demo-backend.jar
EXPOSE 5656
ENTRYPOINT ["java","-jar","demo-backend.jar","-Dspring.profiles.active=dev"]
