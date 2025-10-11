#!/bin/bash
set -e # Exit on error

COMPOSE_FILE="../../docker/local/docker-infra-base.yml"
echo "Starting docker containers with compose file: $COMPOSE_FILE"

docker compose -f "$COMPOSE_FILE" up -d

if [ $? -ne 0 ]; then
   echo "Error: docker up failed."
   exit 1
fi
echo "Docker up completed."
