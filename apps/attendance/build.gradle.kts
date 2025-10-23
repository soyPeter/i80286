// Root build file for attendance bounded context
plugins {
  id("java")
  id("org.springframework.boot")
  id("io.spring.dependency-management")
  id("com.github.spotbugs")
}


dependencies {
  implementation(project(":apps:libs:shared-domain"))
  implementation(project(":apps:libs:shared-api"))
  implementation(project(":apps:libs:shared-infra"))


  developmentOnly(libs.spring.boot.devtools)
}
