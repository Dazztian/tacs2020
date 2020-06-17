FROM openjdk:8-jre-alpine

ENV dbName tacs
ENV dbUrl localhost
ENV dbPort 27017


ENV APPLICATION_USER grupo4
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY ./build/libs/covid19-grupo4-0.0.1.jar /app/covid19-grupo4.jar
WORKDIR /app

CMD java -server -jar covid19-grupo4.jar $dbName $dbUrl $dbPort