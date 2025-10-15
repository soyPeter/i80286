plugins {
  java
  alias(libs.plugins.spring.boot) apply false
  alias(libs.plugins.dependency.management) apply false
  id("checkstyle")
  id("pmd")
  id("jacoco")
  id("com.github.spotbugs") version "6.0.0" apply false
}

allprojects {
  group = "io.bitnomio"
  version = "0.0.1-SNAPSHOT"
  repositories {
    mavenCentral()
//    mavenLocal()

//    // Spring repositories para Spring Boot
//    maven {
//      name = "spring-milestones"
//      url = uri("https://repo.spring.io/milestone")
//    }
//
//    // Si usas snapshots
//    maven {
//      name = "spring-snapshots"
//      url = uri("https://repo.spring.io/snapshot")
//      mavenContent {
//        snapshotsOnly()
//      }
//    }
  }

}

subprojects {
  apply(plugin = "java")

  // Aplicar dependency management a todos los subprojects que lo necesiten
  afterEvaluate {
    if (plugins.hasPlugin("io.spring.dependency-management")) {
      configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
          mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}")
          mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.springCloud.get()}")
          mavenBom("org.testcontainers:testcontainers-bom:${libs.versions.testcontainers.get()}")
        }
      }
    }
  }


  configure<JavaPluginExtension> {
    toolchain {
      languageVersion = JavaLanguageVersion.of(25)
    }
  }

  tasks.withType<Test> {
    useJUnitPlatform()
  }

  when {
    name == "domain" -> {
      // Solo Java puro - sin configuración adicional
      configureLibraryModule()
    }

    name == "application" || name == "app" -> {
      apply(plugin = "io.spring.dependency-management")
    }

    name == "infrastructure" -> {
      apply(plugin = "org.springframework.boot")
      apply(plugin = "io.spring.dependency-management")
      apply(plugin = "com.github.spotbugs")

      // Configuración de SpotBugs
      configure<com.github.spotbugs.snom.SpotBugsExtension> {
        ignoreFailures.set(false)
        showStackTraces.set(true)
      }
    }

    project.path.startsWith(":apps:libs:") -> {
      configureLibraryModule()
    }
  }
}

// Función helper para evitar duplicación de código
fun Project.configureLibraryModule() {
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")

  configure<PublishingExtension> {
    publications {
      create<MavenPublication>("maven") {
        from(components["java"])
      }
    }
  }
}
