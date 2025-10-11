# Monorepo Setup for Micronaut Microservices

This document outlines the steps and configurations used to set up a monorepo for a Java Micronaut multi-module project, including Gradle configuration, Docker image creation, and other relevant details.

## Project Structure

The project has the following directory structure:
```
monorepo/
├── gradle/
│ └── wrapper/
├── services/
│ ├── auth/
│ │   ├── api/
│ │   └── service/
│ │       ├── resources/
│ │       ├── src/
│ │       │    ├── java/
│ │       │    └── tests/
│ │       └── build.gradle.kts
│ └── users/
│     ├── api/
│     └── service/
│         ├── resources/
│         ├── src/
│         │    ├── java/
│         │    └── tests/
│         └── build.gradle.kts
├── ops/
│   ├── infrastructure/
│   │ ├── common/
│   │ ├── modules/
│   │ └── services/
│   │     ├── auth/
│   │     └── users/
│   ├── scripts/
│   │   ├── docker/
│   │   └── ci/  
│   │       ├── deploy.sh
│   │       └── terraform-deploy.sh
│   └── docker/
│       ├── local/
│       └── services/
│           ├── auth/
│           └── users/
├── .gitignore
├── .editorconfig
├── build.gradle.kts
└── README.md
```


## Prerequisites

Before you begin, make sure you have the following installed:

*   [SDKMAN!](https://sdkman.io/)
*   Java 21 or higher
*   Gradle (version 8.11.1 or higher)
*   Docker
*   AWS CLI
*   jq

## Step-by-Step Configuration

### 1. Project Setup

1. Create the base project structure:
   ```bash
     mkdir monorepo
     cd monorepo
     mkdir -p gradle/wrapper services/auth services/users ops/docker ops/scripts ops/infrastructure ops/infrastructure/common ops/infrastructure/auth ops/infrastructure/users ops/infrastructure/modules
   ```
2. Initialize gradle project with basic option
   ```bash
   gradle init
   ```
   Select type of project to generate: **basic**
<br><br>
3. Initialize the Gradle Wrapper:
     ```bash
     gradle wrapper
     ```
4. Initialize a Git repository:
    ```bash
    git init
    ```

### 2. Gradle Configuration

1. **`settings.gradle.kts` (root):**
  *   This file defines the root project name and includes subprojects in the build, using a multi-module approach. This configuration applies the foojay-resolver plugin in the plugin management block, to configure the java toolchain.
     
  ```kotlin
        rootProject.name = "i8086-monorepo"
        include("services:auth")
        include("services:users")
        // Include other services as they get created
        pluginManagement {
             plugins {
                 id("org.gradle.toolchains.foojay-resolver") version "0.5.0"
            }
        }
   ```
2. **`build.gradle.kts` (root):**
  * This file sets up the build for the root project.
  * Configures plugins, repositories, and the java version to use for all subprojects.
    ```kotlin
        plugins {
            id("org.gradle.toolchains.foojay-resolver") version "0.5.0"
        }

        allprojects {
            repositories {
                mavenCentral()
            }
        }

        subprojects {
          apply(plugin="java")
          java {
            toolchain {
              languageVersion.set(JavaVersion.VERSION_21)
            }
          }
        }
    ```

3. **Version Catalog (`gradle/libs.versions.toml`):**
  *   A version catalog (`libs.versions.toml`) is used to centralize and manage dependency versions.
  *   It contains:
    *   `[versions]`: Defines the versions of dependencies and plugins.
    *   `[libraries]`: Defines the libraries to be used with the defined versions
    *   `[plugins]`: Defines the plugins to be used with the defined versions.
  *   Using a version catalog helps to avoid duplicated configurations, and ensures consistent versions across all modules.

4. **`build.gradle.kts` (in `services/auth` and `services/users`):**
* These files are responsible for defining the build process for each of the microservices.
* They define the plugins, dependencies, and specific configuration for each service.
* They include the configuration for the jib plugin to generate docker images.

5.  **`gradle.properties` (root):**
  * This file configures the project using properties, like disabling configuration cache to avoid issues with jib.
    ```properties
    org.gradle.unsafe.configuration-cache=false
    org.gradle.configuration-cache.problems=warn
    ```
### 3. Docker Configuration

1.  **`ops/docker/auth/Dockerfile` (and `ops/docker/users/Dockerfile`):**
  *  These files are used to create the Docker images for each service.
  *  They define the base image, copy the necessary files, configure the entrypoint, and set up the non root user.
  *  They do not include environment variables or health check configurations, as those are managed by the orchestration platform.

### 4. Environment Variables

*   `AWS_ACCOUNT_ID`: Your AWS Account ID.
*   `AWS_DEFAULT_REGION`: The AWS region where your ECR repository is located.
*   `BITBUCKET_COMMIT`: The commit id from your bitbucket pipeline. This variable can be set manually for testing purposes.
*   `AWS_PROFILE` (Optional): Your AWS CLI profile name
*   `DEPLOY_ENVIRONMENT` (Optional): Set to `prod` for production builds or unset for other environment builds.
*   `BUILD_TARGET` (Optional): Set to `local` for local builds, or unset for ECR builds.

### 5. Commands

*   **In case of errors:**
    ```bash
    ./gradlew --stop
    ```
*   **Compile the project:**
    ```bash
    ./gradlew :services:auth:build :services:users:build
    ```    
*  **Build docker images using jib:**
      ```bash
     ./gradlew :services:auth:jibDockerBuild :services:users:jibDockerBuild
      ```
   For local images, use the `BUILD_TARGET` variable:
   ```bash
   export BUILD_TARGET="local"
   ./gradlew :services:auth:jibDockerBuild :services:users:jibDockerBuild
      ```
*   **Build Docker Images (with Dockerfiles):**
       ```bash
       cd monorepo
       docker build -t auth-dockerfile-image:tests -f ops/docker/services/auth/Dockerfile .
       docker build -t users-dockerfile-image:tests -f ops/docker/services/users/Dockerfile .
       ```

*   **Clean Gradle Cache:**
     ```bash
    ./gradlew clean --build-cache
     ```
### 6. Validating Docker images

* **Check image size:** Check the image sizes for both types of images, using the command `docker images`
     ```bash
     docker images auth-dockerfile-image
     docker images users-dockerfile-image
     docker images  ${System.getenv("AWS_ACCOUNT_ID")}.dkr.ecr.${System.getenv("AWS_DEFAULT_REGION")}.amazonaws.com/bitnomio/auth
     docker images  ${System.getenv("AWS_ACCOUNT_ID")}.dkr.ecr.${System.getenv("AWS_DEFAULT_REGION")}.amazonaws.com/bitnomio/users
     ```
*   **Compare Image History:** Compare the history of both images:
    ```bash
      docker history auth-dockerfile-image
      docker history ${System.getenv("AWS_ACCOUNT_ID")}.dkr.ecr.${System.getenv("AWS_DEFAULT_REGION")}.amazonaws.com/bitnomio/auth
      docker history users-dockerfile-image
      docker history ${System.getenv("AWS_ACCOUNT_ID")}.dkr.ecr.${System.getenv("AWS_DEFAULT_REGION")}.amazonaws.com/bitnomio/users
    ```
*    **Run the Images:** Test that the application runs in the containers, and that they are running correctly using `docker run`. Remember to set the `SERVICE_PORT` environment variable in the docker run command:
     ```bash
     docker run -p 8020:8020 -e SERVICE_PORT=8020 auth-dockerfile-image:tests
     docker run -p 8030:8030 -e SERVICE_PORT=8030 users-dockerfile-image:tests
     docker run -p 8020:8020 -e SERVICE_PORT=8020  ${System.getenv("AWS_ACCOUNT_ID")}.dkr.ecr.${System.getenv("AWS_DEFAULT_REGION")}.amazonaws.com/bitnomio/auth
     docker run -p 8030:8030 -e SERVICE_PORT=8030  ${System.getenv("AWS_ACCOUNT_ID")}.dkr.ecr.${System.getenv("AWS_DEFAULT_REGION")}.amazonaws.com/bitnomio/users
     ```
### 7. Bitbucket Pipeline

### 8. Terraform Configuration

Remote Backend:

As [this article explains](https://medium.com/@aaloktrivedi/configuring-a-terraform-remote-backend-with-s3-and-dynamodb-ebcefa8432ea), using a remote backend is crucial for collaborative Terraform projects. It addresses the limitations of storing state files locally, such as potential conflicts and lack of versioning.
Implementation: We're using the S3 backend, as specified in your staging-backend.conf (or a similar file). This tells Terraform to store its state file in an S3 bucket, making it more reliable and accessible from different environments.

#### S3 Bucket:

The S3 bucket (tf-remote-bitnomio in your setup) acts as the storage location for the Terraform state file (remote.tfstate in our case, with a prefix for each environment) .
Creation: You manually created the S3 bucket, as recommended by the article, and we are not managing the S3 bucket using terraform.
Permissions: We have discussed the IAM permissions necessary to access the S3 bucket, and we have confirmed that the peter profile that you are using, has the correct permissions.

#### DynamoDB Table:

As described in the article, a DynamoDB table is used for state locking, which ensures that only one Terraform operation can modify the state at a time.
Creation: You manually created a DynamoDB table ( tf-infra-state-lock) with the required partition key (LockID, String), which is also described in the article.
Permissions: The IAM user associated with the profile that you are using to initialize terraform should have the necessary permissions to perform operations in this table (read, write, delete)

#### Terraform Backend Configuration:

backend-config parameter: We are using the terraform init command with the -backend-config option to specify the backend configuration file that we created earlier.
backend block: We are using an S3 backend, specifying the bucket name, key, region, and the DynamoDB table name in our backend configuration file.

```properties
bucket  = "tf-remote-bitnomio"
key     = "staging/i8086/remote.tfstate"
encrypt = true
region  = "eu-west-1"
dynamodb_table = "tf-infra-state-lock"
```
#### Credentials and Profiles

Using the --profile parameter, with the environment variable AWS_PROFILE in our terraform init commands, to indicate which credentials should be used to access both the S3 bucket and the dynamoDB table.
We have also set the profile attribute in the provider block, using the aws_profile variable, to specify the credentials that should be used when performing operations with terraform resources.
Key points

Manual Creation: Both the S3 bucket and the DynamoDB table were created manually in the AWS Console. Terraform is not managing this resources, since they are a pre-requisite for managing other resources.
