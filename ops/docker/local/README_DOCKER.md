
#### [Docker](https://docs.docker.com/get-docker/)

For local development an [infrastructure project](https://gitlab.com/bitnomio-it-team/backend-team/bitnomio-i8086/i8086-local-infra) is provided,
and maintained by the team, with all docker images preconfigured, to this date:

- ELK for monitoring
- Jaeger for tracing
- Mysql, Mongo and Postgres as persistence units
- Redis as cache and ephemeral persistence
- Sonarqube for static code analysis
- Redis and Mongo WebUIs

Docker is <b>not mandatory</b> for this to work, please follow this instructions [Docker installation guide](https://docs.docker.com/get-docker/)

All the images are set up via docker context, and a .env file for further customization/personalization
A docker-infra.yml is present to facilitate kick-start, from the infra root run:

```bash
$ docker-compose up -d -f ./docker-infra.yml
```
For further information please see this [Docker compose guide by Baeldung team](https://www.baeldung.com/docker-compose)

To run this microservice dockerized within a local config you should add it to the preconfigured docker network, this can be achieved with:

```bash
$ docker build . -f Dockerfile -t i8086_backend
$ docker run -d -p 8020:8020 --network i8086_backend --name i8086_auth auth_service:latest
```

If we only need a couple of docker images we can do the following
```bash

# Get the image we want
$ docker pull mysql

# Get a list of images on our system
$ docker docker images
REPOSITORY   TAG       IMAGE ID       CREATED       SIZE
mysql        latest    5a4e492065c7   11 days ago   514MB

# Create the container with exposed port 3306
$ docker run -d --name=mysql.local -p=3306:3306 -e MYSQL_ROOT_USER=root -e MYSQL_ROOT_PASSWORD=secret 5a4e492065c7

# Connect to the container bash to test if everything is ok
$ docker exec -it mysql.local bash

```
