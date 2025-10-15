plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    api(project(":apps:libs:shared-domain"))
    implementation(libs.jakarta.validation.api)

    implementation(libs.jackson.annotations)
    implementation(libs.logback.classic)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenLocal") {
            groupId = project.group as String
            artifactId = "my-local-library" // Your artifact ID
            version = project.version as String

//            from(project.components.java)
        }
    }
    repositories {
        maven {
            name = "myLocalReleases"
            url = uri("http://localhost:8081/repository/my-local-releases/")
            credentials {
                username = "admin"
                password = "admin"
            }
        }
        maven {
            name = "myLocalSnapshots"
            url = uri("http://localhost:8081/repository/my-local-snapshots/")
            credentials {
                username = "admin"
                password = "admin"
            }
        }
    }
}