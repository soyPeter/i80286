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
    implementation(project(":apps:libs:shared-domain"))
    implementation(project(":apps:libs:shared-api"))
    implementation(project(":apps:libs:shared-infra"))

    // Spring Boot
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.jdbc)

    // Database
    implementation(libs.jdbi.core)
    implementation(libs.jdbi.postgres)
    implementation(libs.jdbi.jackson2)
    implementation(libs.jdbi.spring)
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)

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
