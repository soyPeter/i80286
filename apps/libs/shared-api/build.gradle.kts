plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {

    // Jakarta Validation
    api(libs.jakarta.validation.api)

    // Jackson for JSON serialization
    implementation(libs.jackson.databind)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.datatype.jsr310)

    // Testing
    testImplementation(libs.spring.boot.starter.test)
}

java {
    withJavadocJar()
    withSourcesJar()
}

//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            from(components["java"])
//        }
//    }
//}
