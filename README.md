# Blueprint 80286 Monorepo

[![Java](https://img.shields.io/badge/Java-25-007396?logo=java)](https://openjdk.org/projects/jdk/25/)
[![Gradle](https://img.shields.io/badge/Gradle-9.1.0-02303A?logo=gradle)](https://gradle.org/releases/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![CI](https://img.shields.io/badge/CI-Bitbucket%20Pipelines-2684FF?logo=bitbucket)](bitbucket-pipelines.yml)

Monorepo de microservicios Java con Spring Boot. Incluye configuración moderna de Gradle (Version Catalog), Dockerfiles para containerización, Terraform para IaC en AWS (S3/DynamoDB backend), y pipeline de Bitbucket para CI/CD.


## Estructura del proyecto

```
.
├── ARCHITECTURE.md
├── README.md
├── bitbucket-pipelines.yml
├── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── gradle.properties
├── settings.gradle.kts
├── ops/
│   ├── docker/
│   │   ├── local/
│   │   └── services/
│   │       ├── auth/
│   │       └── users/
│   ├── infra/
│   │   ├── common/
│   │   │   └── config/
│   │   │       ├── staging-backend.conf
│   │   │       └── production-backend.conf
│   │   ├── modules/
│   │   │   ├── rds/
│   │   │   └── security-group/
│   │   └── services/
│   │       ├── auth/
│   │       └── users/
│   └── scripts/
│       ├── ci/
│       │   └── deploy.sh
│       └── docker/
├── services/
│   ├── admin/
│   ├── api-gateway/
│   ├── attendance/
│   ├── customer/
│   ├── reports/
│   ├── security/
│   ├── user/
│   └── libs/
│       ├── common-application/
│       ├── common-contracts/
│       ├── common-domain/
│       └── common-infrastructure/
└── gradlew, gradlew.bat
```

Los módulos incluidos en settings.gradle.kts:
- services: customer, user, admin, attendance, reports, security
- libs: common-domain, common-application, common-infrastructure, common-contracts

Nombre de raíz: blueprint-80286


## Versiones y dependencias principales

- Java: 25 (toolchain Gradle)
- Gradle Wrapper: 9.1.0 (gradle/wrapper/gradle-wrapper.properties)
- Spring Boot: 3.4.5 (gradle/libs.versions.toml)
- Spring Cloud BOM: 2023.0.0
- Flyway: 9.22.3
- PostgreSQL Driver: 42.7.2
- Resilience4j: 2.3.0
- Micrometer Prometheus: 1.12.4
- Testcontainers BOM: 1.19.3
- JUnit: 5.10.2

Nota: El pipeline de Bitbucket usa una imagen base Temurin 21; Gradle Toolchains descarga automáticamente JDK 25 para compilar/ejecutar las tareas.


## Gradle (configuración moderna)

- Version Catalog: gradle/libs.versions.toml
  - [versions]: springBoot=3.4.5, springCloud=2023.0.0, etc.
  - [libraries]: alias para starters, PostgreSQL, Flyway, Micrometer, SpringDoc, Testcontainers…
  - [bundles]: spring-boot, resilience4j, testing
  - [plugins]: org.springframework.boot, io.spring.dependency-management, org.flywaydb.flyway
- Toolchain Java 25: configurada en build.gradle.kts (root y servicios)
- Plugins por módulo: java, spring-boot, dependency-management, flyway

Ejemplo root build.gradle.kts (fragmento):
```kotlin
plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.flyway)
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
}
```

Ejemplo de dependencias comunes (root y servicios):
```kotlin
dependencies {
    implementation(libs.bundles.spring.boot)
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    implementation(libs.spring.cloud.starter.config)
    implementation(libs.bundles.resilience4j)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.platform.launcher)
}
```

Manejo de BOMs:
```kotlin
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.springCloud.get()}")
        mavenBom("org.testcontainers:testcontainers-bom:${libs.versions.testcontainers.get()}")
    }
}
```

Propiedades Gradle relevantes (gradle.properties):
- org.gradle.configuration-cache=false
- org.gradle.parallel=true
- org.gradle.caching=false


## Comandos útiles

- Construir todo: `./gradlew build`
- Test unitarios: `./gradlew test`
- Limpiar: `./gradlew clean`
- Ejecutar un servicio (ej. customer): `./gradlew :services:customer:bootRun`
- Compilar solo un servicio: `./gradlew :services:customer:build`

En caso de bloqueo del daemon: `./gradlew --stop`


## Docker y containerización

Actualmente, el repositorio NO tiene configurado el plugin Jib en Gradle. La construcción de imágenes se realiza con Dockerfiles ubicados en ops/docker/services/<servicio>.

- Dockerfiles por servicio:
  - ops/docker/services/auth/Dockerfile
  - ops/docker/services/users/Dockerfile
- Servicios de infraestructura local (Elasticsearch, Postgres, Redis, RabbitMQ, etc.) disponen de Dockerfiles bajo ops/docker/local/* para desarrollo local.

Ejemplos de build y run:
```bash
# Desde la raíz del repo
# Construir imágenes
docker build -t auth:local -f ops/docker/services/auth/Dockerfile .
docker build -t users:local -f ops/docker/services/users/Dockerfile .

# Ejecutar exponiendo puertos ejemplo
docker run --rm -p 8020:8020 -e SERVICE_PORT=8020 auth:local
docker run --rm -p 8030:8030 -e SERVICE_PORT=8030 users:local
```

Publicación en AWS ECR (CI): el script ops/scripts/ci/deploy.sh inicia sesión en ECR, construye imágenes y actualiza servicios ECS. Asegúrate de tener exportadas estas variables en CI:
- AWS_ACCOUNT_ID
- AWS_KEY, AWS_SECRET
- AWS_DEFAULT_REGION (eu-west-1 por defecto en scripts)

Nota sobre Jib: el pipeline invoca tareas jibDockerBuild, pero no existe configuración Jib en los build.gradle.kts. Si se decide usar Jib en el futuro, agrega el plugin com.google.cloud.tools.jib y actualiza este README y los scripts.


## Terraform (IaC en AWS)

Backend remoto (S3/DynamoDB):
- Archivo de backend (staging): ops/infra/common/config/staging-backend.conf
- Contenido real:
```hcl
bucket  = "tf-remote-state-bitnomio"
key     = "staging/i8086/remote.tfstate"
encrypt = true
region  = "eu-west-1"
dynamodb_table = "tf-infra-bitnomio-locking"
```
- Existe también production-backend.conf con la configuración equivalente para producción.

Inicialización por servicio (ej. auth):
```bash
# Posicionarse en la carpeta del servicio de infraestructura
cd ops/infra/services/auth

# Inicializar con backend remoto para staging
terraform init -backend-config=../../common/config/staging-backend.conf -reconfigure

# Ver versión
terraform --version

# Plan y apply (ejemplo; define tus variables/secretos vía TF_VAR_*)
terraform plan
terraform apply
```

Perfiles y credenciales AWS:
- Usa AWS_PROFILE en tu entorno si manejas múltiples perfiles.
- Permisos necesarios para: S3 (lectura/escritura del state) y DynamoDB (lock/unlock).

Recursos representativos en Terraform:
- Módulo RDS para Postgres (ops/infra/modules/rds)
- Security Groups (ops/infra/modules/security-group)
- Redis/ElastiCache y SG específicos por servicio (ejemplo en ops/infra/services/auth/main.tf)


## CI/CD con Bitbucket Pipelines

Archivo: bitbucket-pipelines.yml
- Imagen base: eclipse-temurin:21-jdk-jammy
- Caches: Gradle, Docker, Terraform
- Variables: AWS_DEFAULT_REGION, AWS_ACCOUNT_ID
- Pasos:
  - Unit Tests: `./gradlew test`
  - SonarCloud: `./gradlew sonarqube` (requiere SONAR_TOKEN)
  - Terraform Deploy: instala Terraform 1.10.1 y ejecuta ops/ci/terraform-deploy.sh
  - Build and Deploy Services: ejecuta ops/scripts/docker/deploy.sh (login ECR, build, actualización ECS)

Ramas: default, develop, main con pipeline similar.


## Troubleshooting

- Toolchains y JDK 25 en CI:
  - Si ves errores de versión de Java, verifica que Gradle descargó la toolchain 25. Agrega `org.gradle.java.installations.auto-download=true` si fuera necesario (Gradle lo hace por defecto).
- Configuration cache desactivada:
  - Está desactivada en gradle.properties para evitar problemas con algunos plugins/tareas.
- Login en ECR falla:
  - Verifica AWS_ACCOUNT_ID, AWS_KEY, AWS_SECRET y región. Asegúrate de que la política del usuario permite ecr:GetAuthorizationToken.
- Terraform backend y permisos:
  - 403/AccessDenied al hacer init suele indicar permisos insuficientes sobre el bucket S3 o la tabla DynamoDB.
- Puertos ocupados al ejecutar contenedores locales:
  - Cambia los mapeos -p 80xx:80xx o detén procesos que usen esos puertos.


## Estándares de arquitectura

Consulta ARCHITECTURE.md para guías de Arquitectura Hexagonal, DDD, y prácticas de testing y despliegue que rigen este monorepo.
