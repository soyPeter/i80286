plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
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
    // Spring Cloud Gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    
    // Spring Cloud Config
    implementation(libs.spring.cloud.starter.config)
    
    // Spring Boot Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    // Resilience4j
    implementation(libs.bundles.resilience4j)
    
    // Micrometer for metrics and observability
    implementation(libs.micrometer.registry.prometheus)
    
    // Testing
    testImplementation(libs.bundles.testing)
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.springCloud.get()}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}