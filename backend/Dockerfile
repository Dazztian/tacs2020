FROM openjdk:8-jre-alpine

ENV APPLICATION_USER grupo3
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY ./build/libs/covid19-grupo3.jar /app/covid19-grupo3.jar
WORKDIR /app

CMD ["java", "-server", "-jar", "covid19-grupo3.jar"]