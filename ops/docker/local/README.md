# Local infra 
This repository should help local development providing a set of docker images which allows to achive a minimun dev workspace
## Authors
- [@Peter](mailto:pa@bitnomio.es)

## Index

- [Docker tips](README_DOCKER.md)
- [K8s tips](../../infra/README_K8s.md)

## Launch images

### Config
- Everything is configured via **.env** file included, if you want to modify any user or pass or port, should be done in that file.
- By default every username is **_"bitnomio"_**, every password is **_"secret"_**

### With docker daemon up!!!

To launch and build every service:
```bash
docker compose up -d
```

If only certain services are required:
```bash
docker compose up -d mysql-8 redis redis-webui mongo mongo-webui
```
