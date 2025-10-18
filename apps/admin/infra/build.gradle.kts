// AML Infrastructure module - Placeholder for future implementation
plugins {
  id("java")
  id("org.springframework.boot")
  id("io.spring.dependency-management")
  id("com.github.spotbugs")
}

dependencies {
  // Internal dependencies
  implementation(project(":apps:aml:domain"))
  implementation(project(":apps:aml:app"))

  // Spring Boot
  implementation(libs.bundles.spring.boot)
  implementation(libs.spring.boot.starter.amqp)

  // Database
  implementation(libs.bundles.postgres.flyway.jdbi)

  // Observability
  implementation(libs.micrometer.core)
  implementation(libs.micrometer.registry.prometheus)

  // Documentation
  implementation(libs.springdoc.openapi.starter.webmvc.ui)

  // Testing
  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.testcontainers.junit.jupiter)
  testImplementation(libs.testcontainers.postgresql)
  testImplementation(libs.testcontainers.rabbitmq)
}
