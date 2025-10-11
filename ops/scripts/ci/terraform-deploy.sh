#!/bin/bash
set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Constants
readonly AWS_REGION="${AWS_DEFAULT_REGION}"

# Validate required environment variables
required_vars=("AWS_KEY" "AWS_SECRET" "BITBUCKET_CLONE_DIR" "BITBUCKET_BRANCH" "AWS_DEFAULT_REGION" )
for var in "${required_vars[@]}"; do
  if [ -z "${!var}" ]; then
    echo -e "${RED}Error: Required variable $var is not set: $var${NC}"
    exit 1
  fi
done

# Change to repository directory
cd "${BITBUCKET_CLONE_DIR}" || { echo -e "${RED}Error: Failed to change directory to ${BITBUCKET_CLONE_DIR}${NC}"; exit 1; }

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

# Configuration variables
ENVIRONMENT=$(getEnvironment)

# Check if valid environment
if [[ "${ENVIRONMENT}" == "error" ]]; then
  echo -e "${YELLOW}No environment found so doing nothing..${NC}"
  exit 0
fi

echo -e "${YELLOW}Deploying to ${ENVIRONMENT} environment${NC}"

# Terraform operations (outside the loop, run once per environment)
echo -e "${YELLOW}Attempting to create or update infra definition using Terraform...${NC}"

# Terraform needs to be installed and in the PATH
export PATH=$PATH:/usr/local/bin/terraform
if ! command -v terraform &> /dev/null; then
  echo -e "${RED}Terraform is not installed or not in PATH. Please install it.${NC}"
  exit 1
fi

# Detect modified services (Assuming a structure like /apps/<service-name>/...)
MODIFIED_SERVICES=$(git diff --name-only HEAD^ HEAD | awk -F'/' '/^services/ {print $2}' | sort -u)

# Iterate and deploy each modified service
for SERVICE in $MODIFIED_SERVICES; do
    if [ "$SERVICE" == "library" ]; then
       echo -e "${YELLOW}Changes in utils library no need to deploy, skipping...${NC}"
       continue
    fi

    # Run the terraform commands from the root directory.
    cd "./ops/infrastructure/services/${SERVICE}" || { echo -e "${RED}Error: Failed to change directory to Terraform project${NC}"; exit 1; }

    # Initialize Terraform and apply changes
    if ! terraform init -backend-config="../../common/config/backend-${ENVIRONMENT}.conf" -reconfigure; then
      echo -e "${RED}Failed to initialize Terraform${NC}"
      exit 1
    fi

    if ! terraform apply -var-file="../../common/config/${ENVIRONMENT}.tfvars" -auto-approve; then
       echo -e "${RED}Failed to create/update infrastructure using Terraform${NC}"
      exit 1
    fi
  # Change to repository directory
    cd "${BITBUCKET_CLONE_DIR}" || { echo -e "${RED}Error: Failed to change directory to ${BITBUCKET_CLONE_DIR}${NC}"; exit 1; }
    echo -e "${GREEN}Successfully created or updated infrastructure using terraform for service: ${SERVICE} ${NC}"
done

echo -e "${GREEN}Successfully deployed infrastructure to ${ENVIRONMENT}${NC}"
