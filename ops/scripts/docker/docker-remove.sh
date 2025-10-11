#!/bin/bash
set -e # Exit on error

COMPOSE_FILE="../../docker/local/docker-infra-base.yml"
 echo "Removing docker containers with compose file: $COMPOSE_FILE"

docker compose -f "$COMPOSE_FILE" down

if [ $? -ne 0 ]; then
    echo "Error: docker down failed."
   exit 1
fi
echo "Docker down completed."
