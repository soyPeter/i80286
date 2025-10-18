// AML Domain module - Placeholder for future implementation

plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    // Domain dependencies
    api(project(":apps:libs:shared-domain"))
    
    // Testing
    testImplementation(libs.spring.boot.starter.test)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenPublishAml") {
            from(components["java"])
        }
    }
}