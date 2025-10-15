plugins {
    id("java")
}

//springBoot {
//    mainClass.set("io.bitnomio.aml.infra.AMLApp")
//}

dependencies {
    implementation(project(":apps:fde:app"))
    implementation(project(":apps:fde:domain"))
    implementation(project(":apps:libs:shared-infra"))

    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.aop)

    implementation(libs.jdbi.core)
    implementation(libs.jdbi.objects)
    implementation(libs.jdbi.spring)
    implementation(libs.jdbi.caffeine.cache)
    implementation(libs.jdbi.opentelemetry)
    implementation(libs.jdbi.stringtemplate)
    implementation(libs.jdbi.jackson2)
    implementation(libs.jdbi.postgres)

    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)

    implementation(libs.resilience4j.spring.boot3)
    implementation(libs.resilience4j.circuitbreaker)
    implementation(libs.resilience4j.retry)
    implementation(libs.resilience4j.ratelimiter)
    implementation(libs.resilience4j.bulkhead)
    implementation(libs.resilience4j.timelimiter)

    implementation(libs.micrometer.core)
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.opentelemetry.sdk)
    implementation(libs.opentelemetry.exporter.otlp)

    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jsr310)

    testImplementation(libs.spring.boot.starter.test)
//    testImplementation(libs.junit.jupiter.api)
//    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.rabbitmq)
}