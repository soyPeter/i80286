plugins {
  id("java-library")
  id("io.spring.dependency-management")
}

dependencies {
  api(libs.bundles.spring.boot)
  api(libs.bundles.postgres.flyway.jdbi)
  api(libs.bundles.resilience4j)
  api(libs.spring.boot.starter.amqp)
  api(libs.reactor.core)
  api(libs.tsid)

//    testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.assertj.core)
//    testRuntimeOnly(libs.junit.jupiter.engine)
}

java {
  withJavadocJar()
  withSourcesJar()
}
