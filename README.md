# Covid-19 - Grupo  3
## Técnicas Avanzadas de la Construcción de Software
### UTN - FRBA

This project is the work done following this [statement](https://docs.google.com/document/u/1/d/e/2PACX-1vQo5WkN-3RTLaeB5885hlfcnuWFgxzxe-u5gPa5IGrtkeTF9BHMjeh1YScTO-Tg000gzllwmRaFFKet/pub "TACS - Covid19 - Enunciado")



##### Building  the application

First build the application using gradle, then create a local docker image using Dockerfile.

Backend 

```
cd backend
./gradlew build
docker build -t covid19-backend .
```
Frontend

```
cd frontend
./gradlew build
docker build -t covid19-frontend .
```

##### Running the application

This will start the container and run it.
```
docker run -it -d -p 8080:8080 --rm covid19-backend
docker run -it -d -p 8081:8080 --rm covid19-frontend
```

The -d option makes the docker to be detached, removing it will allow to see logs in real time.
