FROM openjdk:21-jdk

WORKDIR /app

COPY build/libs/tacs-2024-grupo-4-0.0.1-SANPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
