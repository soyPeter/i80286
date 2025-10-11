plugins {
  java
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependency.management)
  alias(libs.plugins.flyway)
//    alias(libs.plugins.foojay.resolver)

}

allprojects {
  repositories {
    mavenCentral()
    mavenLocal()

    // Spring repositories para Spring Boot
    maven {
      name = "spring-milestones"
      url = uri("https://repo.spring.io/milestone")
    }

    // Si usas snapshots
    maven {
      name = "spring-snapshots"
      url = uri("https://repo.spring.io/snapshot")
      mavenContent {
        snapshotsOnly()
      }
    }
  }


}

subprojects {
  apply(plugin = "java")

  // Aplicar dependency management a todos los subprojects que lo necesiten
  afterEvaluate {
    if (plugins.hasPlugin("io.spring.dependency-management")) {
      configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
          mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}")
          mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.springCloud.get()}")
          mavenBom("org.testcontainers:testcontainers-bom:${libs.versions.testcontainers.get()}")
        }
      }
    }
  }

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(25))
    }
  }


}

group = "io.bitnomio"
version = "0.0.1-SNAPSHOT"

dependencies {
  // Spring Boot Core
  implementation(libs.bundles.spring.boot)

  // Spring Data
  implementation(libs.postgresql)
  implementation(libs.flyway.core)

  // Spring Cloud Config
  implementation(libs.spring.cloud.starter.config)

  // Resilience4j
  implementation(libs.bundles.resilience4j)

  // Micrometer for metrics and observability
  implementation(libs.micrometer.registry.prometheus)

  // OpenAPI documentation
  implementation(libs.springdoc.openapi.starter.webmvc.ui)

  // Testing
  testImplementation(libs.bundles.testing)
  testRuntimeOnly(libs.junit.platform.launcher)
}


tasks.withType<Test> {
  useJUnitPlatform()
}
