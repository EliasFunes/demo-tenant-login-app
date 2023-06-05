FROM openjdk:17.0.2-slim-bullseye

WORKDIR /app

COPY build/libs/demo-tenant*.jar app.jar

ENV DB_URL=""
ENV DB_USERNAME=""
ENV DB_PASSWORD=""

ENV SECURITY_USER_PASS=""
ENV SECRET_KEY=""

ENV HIBERNATE_DDL_AUTO=""

ENV QR_SERVER_HOST=""
ENV QR_SERVER_USERNAME=""
ENV QR_SERVER_PASSWORD=""



EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
