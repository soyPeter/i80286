plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.flyway)
}

group = "io.bitnomio"
version = "0.0.1-SNAPSHOT"

dependencies {
    // Common libraries
    implementation(project(":services:libs:common-domain"))
    implementation(project(":services:libs:common-application"))
    implementation(project(":services:libs:common-infrastructure"))

    // Spring Boot Core
    implementation(libs.bundles.spring.boot)

    // Spring Data
    implementation(libs.postgresql)
    implementation(libs.flyway.core)

    // Spring Cloud Config
    implementation(libs.spring.cloud.config.client)

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

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
