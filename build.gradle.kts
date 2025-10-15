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
}

subprojects {
  apply(plugin = "java")

  repositories {
    mavenCentral()
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
