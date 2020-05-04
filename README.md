# Covid-19 - Grupo  3
## Técnicas Avanzadas de la Construcción de Software
### UTN - FRBA

This project is the work done following this [statement](https://docs.google.com/document/u/1/d/e/2PACX-1vQo5WkN-3RTLaeB5885hlfcnuWFgxzxe-u5gPa5IGrtkeTF9BHMjeh1YScTO-Tg000gzllwmRaFFKet/pub "TACS - Covid19 - Enunciado")

##### Building the application

First build the applications using gradle.

Backend 

```bash
cd backend
./gradlew build
```
Frontend

```bash
cd frontend
./npm install (just run in case you want to install dependencies, otherwise is not needed)
```

##### Compose docker containers

Just up the docker compose. It will proceed to build all the dockers images before composing the containers.
Remember always to build your backend app

```bash
docker-compose up -d
```

##### Telegram bot

To use the telegram bot send a message to @Tacs_2020_Grupo_4_bot

#### Troubleshooting

If having problems seeing the downloaded libraries, and using Intellij do this:

File -> New -> Module from existing sources 

Select backend, (then do it again) and frontend, this will add the folders as modules, and build them.

Another thing that solves some problems is on the on the right, click Gradle and Reimport All Gradle Projects.

TODO: The project structure can be further improved
