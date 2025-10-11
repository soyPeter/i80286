plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.flyway)
}

group = "com.company"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

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

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.springCloud.get()}")
        mavenBom("org.testcontainers:testcontainers-bom:${libs.versions.testcontainers.get()}")
    }
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
