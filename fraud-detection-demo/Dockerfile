FROM openjdk:8 AS job-build
WORKDIR /home/gradle/app/

COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle gradle
# download dependencies
RUN ./gradlew build
COPY . .
RUN ./gradlew shadowJar
RUN mv /home/gradle/app/build/libs/fraud-detection-demo-1.0-all.jar /home/gradle/app/build/libs/fraud-detection-demo.jar

# ---

FROM flink:1.11

COPY --from=job-build /home/gradle/app/build/libs/fraud-detection-demo.jar lib/job.jar
COPY docker-entrypoint.sh /

USER flink
EXPOSE 8081 6123
ENTRYPOINT ["/docker-entrypoint.sh"]
