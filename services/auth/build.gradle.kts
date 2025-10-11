plugins {
  alias(libs.plugins.micronaut.application)
  alias(libs.plugins.versions.plugin)
  alias(libs.plugins.jib.plugin)
}

dependencies {
  annotationProcessor(libs.micronaut.http.validation)

  implementation(libs.micronaut.runtime)
  implementation(libs.micronaut.management)
  implementation(libs.micronaut.validation)
  implementation(libs.micronaut.data.jdbc)
  implementation(libs.micronaut.jdbc.hikari)
  implementation(libs.micronaut.flyway)
  implementation(libs.jakarta.annotation)

  runtimeOnly(libs.logback.classic)
  testImplementation(libs.micronaut.test.junit5)
  testImplementation(libs.junit.jupiter.api)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

micronaut {
  runtime("netty")
  testRuntime("junit5")
  processing {
    incremental(true)
    annotations("io.bitnomio.*")
  }
  application {
    mainClass.set("io.bitnomio.service.AuthApp")
  }
}
sourceSets {
  main {
    java {
      srcDirs("api/src/java", "service/src/java")
    }
    resources {
      srcDirs("api/resources", "service/resources")
    }
  }
  test {
    java {
      srcDirs("api/src/test/java", "service/src/test/java")
    }
  }
}
jib {
  container {
    creationTime.set("USE_CURRENT_TIMESTAMP")
    mainClass = "io.bitnomio.service.AuthApp"
    jvmFlags = listOf(
      "-XX:+UseContainerSupport",
      "-XX:MaxRAMPercentage=75.0",
      "-XX:InitialRAMPercentage=50.0",
      "-XX:+UseG1GC",
      "-XX:+ExitOnOutOfMemoryError",
      "-Dfile.encoding=UTF-8",
      "-Djava.security.egd=file:/dev/./urandom"
    )
  }
  from {
    image = "eclipse-temurin:21-jre-jammy"
  }
  to {
    val buildTarget = System.getenv("BUILD_TARGET") ?: "remote"
    if (buildTarget == "local") {
      image = "bitnomio.io/auth" // This will build the image to the local Docker daemon.
    } else {
      val environment = System.getenv("DEPLOY_ENVIRONMENT") ?: "staging"
      val imageName = "${System.getenv("AWS_ACCOUNT_ID")}.dkr.ecr.${System.getenv("AWS_DEFAULT_REGION")}.amazonaws.com/bitnomio/auth"
      image = imageName
      tags = when (environment) {
        "prod" -> setOf("prod", "${System.getenv("BITBUCKET_COMMIT")}")
        else -> setOf("latest", "${System.getenv("BITBUCKET_COMMIT")}")
      }
    }
  }
}
