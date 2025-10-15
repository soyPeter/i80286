plugins {
    id("java-library")
}

dependencies {
    implementation(libs.bundles.spring.boot)
//    implementation(libs.bundles.postgres.flyway.jdbi)
//    implementation(libs.tsid)


//    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.assertj.core)
//    testRuntimeOnly(libs.junit.jupiter.engine)
}

java {
    withJavadocJar()
    withSourcesJar()
}
