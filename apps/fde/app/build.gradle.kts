plugins {
    id("java-library")
}

dependencies {
    implementation(project(":apps:fde:domain"))
    implementation(project(":apps:libs:shared-infra"))
    implementation(project(":apps:libs:shared-domain"))
    implementation(libs.spring.boot.starter.webflux)


//    testImplementation(libs.junit.jupiter.api)
//    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.spring.boot.starter.test)
}