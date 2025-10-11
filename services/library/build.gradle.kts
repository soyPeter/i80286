plugins {
  alias(libs.plugins.micronaut.library)
  alias(libs.plugins.versions.plugin)
}
sourceSets {
  main {
    java {
      srcDirs("utilities/src/java")
    }
  }
}
//
dependencies {
  annotationProcessor(libs.micronaut.validation)
  annotationProcessor(libs.micronaut.http.validation)
  annotationProcessor(libs.micronaut.data.jdbc)

  implementation(libs.micronaut.http.client)
  implementation(libs.micronaut.runtime)
  implementation(libs.micronaut.management)
  implementation(libs.micronaut.validation)
  implementation(libs.micronaut.data.jdbc)
  implementation(libs.micronaut.jdbc.hikari)
  implementation(libs.micronaut.cache.core)
  implementation(libs.micronaut.cache.redis.lettuce)
  implementation(libs.micronaut.http.server)

  implementation(libs.micronaut.security.jwt)
  implementation(libs.spring.security.crypto)

  implementation(libs.zalando.logbook.core)
  implementation(libs.zalando.logbook.netty)
  implementation(libs.zalando.logbook.filter)
  implementation(libs.zalando.logbook.logstash)
  implementation(libs.zalando.logbook.json)

  implementation(libs.jakarta.annotation)
  implementation(libs.jakarta.validation)

  implementation(libs.apache.commons.codec)


//
//  implementation("io.micronaut.reactor:micronaut-reactor")
//  implementation("io.micronaut.reactor:micronaut-reactor-http-client")
//  implementation("org.springframework.security:spring-security-crypto")
}


