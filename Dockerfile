FROM adoptopenjdk/openjdk17

COPY ./build/libs/CUK-CAPSTONE-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]