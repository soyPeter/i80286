# Blueprint 80286 Monorepo

[![Java](https://img.shields.io/badge/Java-25-007396?logo=java)](https://openjdk.org/projects/jdk/25/)
[![Gradle](https://img.shields.io/badge/Gradle-9.1.0-02303A?logo=gradle)](https://gradle.org/releases/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0--M3-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
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
│   ├── infra/
│   │   ├── common/
│   │   │   └── config/
│   │   │       ├── staging-backend.conf
│   │   │       └── production-backend.conf
│   │   ├── modules/
│   │   │   ├── rds/
│   │   │   └── security-group/
│   │   └── services/
│   └── scripts/
│       ├── ci/
│       └── docker/
├── apps/
│   ├── admin/
│   │   ├── app/
│   │   ├── domain/
│   │   └── infra/
│   ├── aml/
│   │   ├── app/
│   │   ├── domain/
│   │   └── infra/
│   ├── attendance/
│   │   ├── app/
│   │   ├── domain/
│   │   └── infra/
│   ├── customer/
│   │   ├── app/
│   │   ├── domain/
│   │   └── infra/
│   ├── fde/
│   │   ├── app/
│   │   ├── domain/
│   │   └── infra/
│   ├── report/
│   │   ├── app/
│   │   ├── domain/
│   │   └── infra/
│   ├── security/
│   │   ├── app/
│   │   ├── domain/
│   │   └── infra/
│   ├── user/
│   │   ├── app/
│   │   ├── domain/
│   │   └── infra/
│   └── libs/
│       ├── shared-api/
│       ├── shared-domain/
│       └── shared-infra/
└── gradlew, gradlew.bat
```

Módulos declarados en settings.gradle.kts:
- libs: apps:libs:shared-api, apps:libs:shared-domain, apps:libs:shared-infra
- bounded contexts: apps:admin, apps:aml, apps:attendance, apps:customer, apps:fde, apps:report, apps:security, apps:user (cada uno con submódulos :domain, :app, :infra)

Nombre de raíz: blueprint-80286


## Versiones y dependencias principales

- Java: 25 (toolchain Gradle)
- Gradle Wrapper: 9.1.0
- Spring Boot: 4.0.0-M3 (version catalog)
- Spring Cloud BOM: 2023.0.0
- Flyway: 11.10.4
- PostgreSQL Driver: 42.7.3
- Resilience4j: 2.3.0
- Micrometer: 1.14.0
- Testcontainers: 1.19.7
- JUnit: 5.10.2

Nota: El pipeline de Bitbucket usa una imagen base Temurin 21; Gradle Toolchains descargará automáticamente JDK 25 para compilar/ejecutar.


## Gradle (configuración moderna)

- Version Catalog: gradle/libs.versions.toml
  - [versions]: define springBoot, springCloud, flyway, testcontainers, etc.
  - [libraries]: alias para starters, PostgreSQL, Flyway, Micrometer, SpringDoc, Testcontainers…
  - [bundles]: spring-boot, resilience4j, postgres-flyway-jdbi, testing
  - [plugins]: org.springframework.boot, io.spring.dependency-management, org.flywaydb.flyway
- Toolchain Java 25: configurada en build.gradle.kts
- Plugins típicos por módulo: java, org.springframework.boot, io.spring.dependency-management, org.flywaydb.flyway

Ejemplo root build.gradle.kts (fragmento):
```kotlin
plugins {
  java
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.dependency.management)
  alias(libs.plugins.flyway)
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }
```

Ejemplo de dependencias comunes:
```kotlin
dependencies {
  implementation(libs.bundles.spring.boot)
  implementation(libs.bundles.postgres.flyway.jdbi)
  implementation(libs.micrometer.core)
  implementation(libs.micrometer.registry.prometheus)
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
- Tests unitarios: `./gradlew test`
- Limpiar: `./gradlew clean`
- Ejecutar un servicio (ej. admin): `./gradlew :apps:admin:app:bootRun`
- Compilar solo un servicio: `./gradlew :apps:admin:build`

Sugerencia: si el daemon queda bloqueado: `./gradlew --stop`


## Docker y containerización

Este repositorio construye imágenes con Dockerfiles (no hay configuración Jib activa). Las ubicaciones típicas son `ops/docker/services/<servicio>/Dockerfile` para servicios y `ops/docker/local/*` para infraestructura local.

Ejemplo de build y run:
```bash
# Desde la raíz del repo
# Construir imágenes
SERVICE=admin
docker build -t ${SERVICE}:local -f ops/docker/services/${SERVICE}/Dockerfile .

# Ejecutar exponiendo puertos (ajusta variables según el servicio)
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=local ${SERVICE}:local
```

Publicación en AWS ECR (CI): los scripts en `ops/scripts/docker/deploy.sh` inician sesión en ECR, construyen imágenes y actualizan servicios ECS.
Variables necesarias en CI:
- AWS_ACCOUNT_ID
- AWS_KEY, AWS_SECRET
- AWS_DEFAULT_REGION (por defecto eu-west-1)


## Terraform (IaC en AWS)

Backend remoto (S3/DynamoDB):
- Staging backend config: `ops/infra/common/config/staging-backend.conf`
- Producción backend config: `ops/infra/common/config/production-backend.conf`

Inicialización por servicio (ej. auth):
```bash
cd ops/infra/services/auth
terraform init -backend-config=../../common/config/staging-backend.conf -reconfigure
terraform plan
terraform apply
```

Permisos AWS mínimos: acceso a S3 (state) y DynamoDB (locking). Usa `AWS_PROFILE` si manejas múltiples cuentas.

Módulos ejemplo:
- RDS PostgreSQL: `ops/infra/modules/rds`
- Security Groups: `ops/infra/modules/security-group`


## CI/CD con Bitbucket Pipelines

Archivo: `bitbucket-pipelines.yml`
- Imagen base: `eclipse-temurin:21-jdk-jammy`
- Caches: Gradle, Docker, Terraform
- Pasos principales:
  - Unit Tests: `./gradlew test`
  - SonarCloud: `./gradlew sonarqube` (requiere SONAR_TOKEN)
  - Terraform Deploy: instala Terraform 1.10.1 y ejecuta `ops/ci/terraform-deploy.sh`
  - Build and Deploy Services: ejecuta `ops/scripts/docker/deploy.sh` (login ECR, build, update ECS)

Ramas: default, develop, main con pipeline similar.


## Troubleshooting

- Toolchains y JDK 25 en CI: si ves errores de versión, verifica que Gradle descargó la toolchain 25.
- Configuration cache desactivada: está a `false` para evitar incompatibilidades con tareas/plugins.
- ECR login falla: revisa credenciales y permisos `ecr:GetAuthorizationToken`.
- Terraform backend: errores 403 suelen ser permisos insuficientes en S3/DynamoDB.
- Puertos ocupados: ajusta `-p` al ejecutar contenedores locales.


## Estándares de arquitectura

Consulta `ARCHITECTURE.md` para guías de Arquitectura Hexagonal, DDD y prácticas de testing y despliegue que rigen este monorepo.
