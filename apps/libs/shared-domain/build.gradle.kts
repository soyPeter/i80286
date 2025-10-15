plugins {
    id("java-library")
}

dependencies {
    implementation(libs.jakarta.validation.api)
    
//    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.assertj.core)
//    testRuntimeOnly(libs.junit.jupiter.engine)
}

java {
    withJavadocJar()
    withSourcesJar()
}