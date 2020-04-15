# Covid-19 - Grupo  3
## Técnicas Avanzadas de la Construcción de Software
### UTN - FRBA

This project is the work done following this [statement](https://docs.google.com/document/u/1/d/e/2PACX-1vQo5WkN-3RTLaeB5885hlfcnuWFgxzxe-u5gPa5IGrtkeTF9BHMjeh1YScTO-Tg000gzllwmRaFFKet/pub "TACS - Covid19 - Enunciado")



##### Building  the application

First build the application using gradle, then create a local docker image using Dockerfile.

Backend 

```bash
cd backend
./gradlew build
docker build -t covid19-backend .
```
Frontend

```bash
cd frontend
./gradlew build
docker build -t covid19-frontend .
```

##### Running the application

This will start the container and run it.
```bash
docker run -it -d -p 8080:8080 --rm covid19-backend
docker run -it -d -p 8081:8080 --rm covid19-frontend
```

The -d option makes the docker to be detached, removing it will allow to see logs in real time.


#### Develop

For now the application does not have a docker-compose, so running from docker can not connect to mongo db.

If launching using Intellij, first start mongo docker container:

```bash
docker run --name <CONTAINER_NAME> -p 27017:27017 -d mongo:3.6.17
```

#### Troubleshooting

If having problems seeing the downloaded libraries, and using Intellij do this:

File -> New -> Module from existing sources 

Select backend, (then do it again) and frontend, this will add the folders as modules, and build them.

Another thing that solves some problems is on the on the right, click Gradle and Reimport All Gradle Projects.

TODO: The project structure can be further improved