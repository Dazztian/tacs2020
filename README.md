# Covid-19 - Grupo  4
## Técnicas Avanzadas de la Construcción de Software
### UTN - FRBA

Trabajo practico realizado siguiendo el siguiente [enunciado](https://docs.google.com/document/u/1/d/e/2PACX-1vQo5WkN-3RTLaeB5885hlfcnuWFgxzxe-u5gPa5IGrtkeTF9BHMjeh1YScTO-Tg000gzllwmRaFFKet/pub "TACS - Covid19 - Enunciado")

El repositorio cuenta con tres modulos:
- Backend
- Frontend
- Telegram Bot

#### Backend

Es lo que consideramos la API, dentro se encuentra el manejo de base de datos, los controllers para request, y la asociación con la API que provee data sobre el Coronavirus.

```bash
cd backend
./gradlew build
```

Compilará y ejecutará la aplicación.
Para correrla se puede utilizar un IDE, o simplemente un docker run.

Esta aplicación requiere una conexión a un mongodb. Se puede utilizar un docker simplemente:

```bash
docker run --name tacs-mongo-db -p 27017:27017 -d mongo:latest 
```
Los parametros que requiere la aplicación siendo ejecutada en un IDE son:
 - mongoDb
 - mongoUrl
 - mongoPort
 
Si se utiliza el docker, estos son:
 - tacs 0.0.0.0 27017

Si se quiere usar docker para correr la aplicación, el comando es el siguiente: 

- Es necesario tener el docker de la db corriendo en local.
```bash
docker run --network="host" --name tacs-backend-docker -p 8080:8080 -d docker.pkg.github.com/paniaton/tacs2020/tacs-backend:1.0.3
```
- --network="host" es necesario para poder relacionar ambos docker, cuando se ejecuta con un IDE no es necesario y utilizando docker-compose tampoco.

- La API se disponibiliza en localhost:8080

#### Frontend

Para correr local se necesita primero ejecutar

```bash
cd frontend
./yarn install
npm start
```

Se puede levantar utilizando Docker, le pegará a la API real.
```bash
 docker run  --name tacs-frontend-docker -p 8081:8081 -d docker.pkg.github.com/paniaton/tacs2020/tacs-frontend:0.0.12
```
- Se disponibiliza en localhost:8081

##### Telegram bot

Para usarlo, se puede enviar un mensaje a @Tacs_2020_Grupo_4_bot

Para correrlo se necesitan 2 archivos en la carpeta \tacs2020\telegramBot\src\main\resources

1) APIKey.txt el cual tendra el api token del bot
2) Base_Url.txt el cual tendra url del backend

Para crear la imagen docker se necesita primero ejecutar:

```bash
cd telegramBot
./gradlew build
```

- Para correr el contenedor de manera local se ejecuta:
```bash
docker run --network="host" --name tacs-telegrambot -p 8082:8082 -d docker.pkg.github.com/paniaton/tacs2020/tacs-telegrambot:0.0.7
```

##### Docker compose

Ejecutando parado en donde se encuentra docker-compose.yml se levantan los tres docker relacionados, incluyendo una base de datos mongodb.

```bash
docker-compose up -d
```



