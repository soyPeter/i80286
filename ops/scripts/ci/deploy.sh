#!/bin/bash
set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Constants
readonly AWS_REGION="eu-west-1"
readonly REPO_URL="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"


# Initial step: Install AWS CLI and configure credentials
echo -e "${YELLOW}Configuring AWS CLI...${NC}"
if ! pip3 install awscli; then
    echo -e "${RED}Error: Failed to install AWS CLI${NC}"
    exit 1
fi

aws configure set aws_access_key_id "${AWS_KEY}" || { echo -e "${RED}Error: Failed to set AWS access key${NC}"; exit 1; }
aws configure set aws_secret_access_key "${AWS_SECRET}" || { echo -e "${RED}Error: Failed to set AWS secret key${NC}"; exit 1; }

# Check if running in CI
if [[ "${CI}" != "true" ]]; then
echo -e "${RED}Not in CI/CD tool, exiting${NC}"
exit 0
fi

# Function to determine environment
getEnvironment() {
case "${BITBUCKET_BRANCH}" in
    "develop")
      echo "staging"
      ;;
    "main")
      echo "production"
      ;;
    *)
      echo "error"
      ;;
esac
}

# Function to determine image tag type
getImageTagType() {
case "${BITBUCKET_BRANCH}" in
    "develop")
      echo "latest"
      ;;
    "main")
      echo "production"
      ;;
    *)
      echo "error"
      ;;
esac
}

# Validate required environment variables
required_vars=("AWS_KEY" "AWS_SECRET" "BITBUCKET_BUILD_NUMBER" "BITBUCKET_CLONE_DIR" "BITBUCKET_BRANCH" "AWS_ACCOUNT_ID")
for var in "${required_vars[@]}"; do
  if [ -z "${!var}" ]; then
    echo -e "${RED}Error: Required variable $var is not set: $var${NC}"
    exit 1
  fi
done

# Change to repository directory
cd "${BITBUCKET_CLONE_DIR}" || { echo -e "${RED}Error: Failed to change directory to ${BITBUCKET_CLONE_DIR}${NC}"; exit 1; }

# Configuration variables
ENVIRONMENT=$(getEnvironment)
TAG_TYPE=$(getImageTagType)

# Check if valid environment
if [[ "${ENVIRONMENT}" == "error" ]]; then
echo -e "${YELLOW}No environment found so doing nothing..${NC}"
exit 0
fi

echo -e "${YELLOW}Deploying to ${ENVIRONMENT} environment${NC}"
echo -e "${YELLOW}Using tag type: ${TAG_TYPE}${NC}"

# Login to ECR (Use the AWS region from the environment variable or default to eu-west-1)
echo -e "${YELLOW}Logging into ECR...${NC}"
if ! aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$REPO_URL"; then
  echo -e "${RED}Failed to login to ECR${NC}"
  exit 1
fi

# Detect modified services (Assuming a structure like /apps/<service-name>/...)
MODIFIED_SERVICES=$(git diff --name-only HEAD^ HEAD | awk -F'/' '/^services/ {print $2}' | sort -u)

if [ -z "$MODIFIED_SERVICES" ]; then
  echo -e "${YELLOW}No services modified, exiting...${NC}"
  exit 0
fi


# Iterate and deploy each modified service
for SERVICE in $MODIFIED_SERVICES; do

    if [ "$SERVICE" == "library" ]; then
        echo -e "${YELLOW}Changes in utils library no need to deploy, skipping...${NC}"
        continue
    fi

    # Change to repository directory
    cd "${BITBUCKET_CLONE_DIR}" || { echo -e "${RED}Error: Failed to change directory to ${BITBUCKET_CLONE_DIR}${NC}"; exit 1; }

    echo -e "${YELLOW}Deploying service: ${SERVICE}${NC}"

    # Configure service-specific variables
    IMAGE="${REPO_URL}/bitnomio/${SERVICE}"
    export IMAGE_NAME="${REPO_URL}/bitnomio/${SERVICE}"
    export VERSION_TAG="${BITBUCKET_BUILD_NUMBER}"
    export DEPLOY_ENVIRONMENT="${ENVIRONMENT}"
    export AWS_DEFAULT_REGION="$AWS_REGION"

    # Build and push Docker images using jib
    echo -e "${YELLOW}Building and pushing docker image for service: $SERVICE${NC}"

    if ! ./gradlew ":services:${SERVICE}:jibDockerBuild" ; then
       echo -e "${RED}Jib docker build failed for service ${SERVICE}${NC}"
       exit 1
    fi
    # Construct the cluster name and service name
    CLUSTER_NAME="${ENVIRONMENT}-core"
    SERVICE_NAME="${ENVIRONMENT}-${SERVICE}-service"

    # Debugging: Print the constructed cluster and service names
    echo -e "${YELLOW}Cluster Name: ${CLUSTER_NAME}${NC}"
    echo -e "${YELLOW}Service Name: ${SERVICE_NAME}${NC}"

    # Get the current task definition
    if ! TASK_DEFINITION=$(aws ecs describe-services --cluster "${CLUSTER_NAME}" --services "${SERVICE_NAME}" --region "${AWS_REGION}" | jq -r '.services[0].taskDefinition'); then
        echo -e "${RED}Error getting task definition for service ${SERVICE_NAME} in cluster ${CLUSTER_NAME}: ${TASK_DEFINITION}${NC}"
        exit 1
    fi

    # Debugging: Print task definition
    echo -e "${YELLOW}Task Definition: ${TASK_DEFINITION}${NC}"

    # Check if task definition is empty
    if [ -z "$TASK_DEFINITION" ]; then
        echo -e "${RED}Error: Task definition is empty for service ${SERVICE_NAME}${NC}"
        exit 1
    fi

    # Get the current task definition
    TASK_DEFINITION=$(aws ecs describe-services --cluster "${CLUSTER_NAME}" --services "${SERVICE_NAME}" --region "${AWS_REGION}" | jq -r '.services[0].taskDefinition')

    # Get the task definition details
    TASK_DEFINITION_DETAILS=$(aws ecs describe-task-definition --task-definition "$TASK_DEFINITION" --region "${AWS_REGION}")

    # Debugging: Print task definition details
    echo -e "${YELLOW}Task Definition Details: ${TASK_DEFINITION_DETAILS}${NC}"


    # Update the image in the task definition
    UPDATED_TASK_DEFINITION=$(echo "$TASK_DEFINITION_DETAILS" | jq --arg IMAGE "${IMAGE}:${BITBUCKET_BUILD_NUMBER}" '.taskDefinition | .containerDefinitions[0].image = $IMAGE | del(.taskDefinitionArn, .revision, .status, .requiresAttributes, .compatibilities, .registeredAt, .registeredBy)')

    # Check if the task definition has changed
    if [ "$(echo "$TASK_DEFINITION_DETAILS" | jq -r '.taskDefinition')" = "$(echo "$UPDATED_TASK_DEFINITION" | jq -r '.')" ]; then
        echo -e "${YELLOW}Task definition has not changed, skipping update for service ${SERVICE_NAME}...${NC}"
        continue
    fi

    # Register a new task definition
    NEW_TASK_DEFINITION_ARN=$(aws ecs register-task-definition --region "${AWS_REGION}" --cli-input-json "$UPDATED_TASK_DEFINITION" | jq -r '.taskDefinition.taskDefinitionArn')

    # Extract the value of the service_created output variable for the current service
    SERVICE_CREATED=$(echo "$OUTPUT" | jq -r ".service_created_${SERVICE}.value")

    # Update the ECS service only if it was created by Terraform
    if [[ "$SERVICE_CREATED" == "true" ]]; then
        echo -e "${YELLOW}Updating ECS service for ${SERVICE}${NC}"
        if ! aws ecs update-service \
            --region "${AWS_REGION}" \
            --cluster "${CLUSTER_NAME}" \
            --service "${SERVICE_NAME}" \
            --task-definition "$NEW_TASK_DEFINITION_ARN"; then
            echo -e "${RED}Failed to update ECS service for ${SERVICE}${NC}"
            # Get recent events for debugging before exiting
            aws ecs describe-services \
                --region "${AWS_REGION}" \
                --cluster "${CLUSTER_NAME}" \
                --services "${SERVICE_NAME}" \
                | jq -r '.services[0].events[0:5]'
            exit 1
        fi

        # Wait for deployment to stabilize
        echo -e "${YELLOW}Waiting for service ${SERVICE} to stabilize in ${ENVIRONMENT} environment${NC}"
        if ! aws ecs wait services-stable \
            --region "${AWS_REGION}" \
            --cluster "${CLUSTER_NAME}" \
            --services "${SERVICE_NAME}"; then
            echo -e "${RED}Service ${SERVICE} failed to stabilize in ${ENVIRONMENT} environment${NC}"
            # Get recent events for debugging before exiting
            aws ecs describe-services \
                --region "${AWS_REGION}" \
                --cluster "${CLUSTER_NAME}" \
                --services "${SERVICE_NAME}" \
                | jq -r '.services[0].events[0:5]'
            exit 1
        fi
    else
        echo -e "${YELLOW}Service ${SERVICE} already exists, skipping update${NC}"
    fi

    echo -e "${GREEN}Successfully deployed ${SERVICE} to ${ENVIRONMENT}${NC}"
done

echo -e "${GREEN}Successfully deployed all modified services to ${ENVIRONMENT}${NC}"

