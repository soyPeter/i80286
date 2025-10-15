// AML Application module - Placeholder for future implementation

plugins {
    id("java-library")
//    id("io.spring.dependency-management")
}

dependencies {
    // Internal dependencies
    implementation(project(":apps:aml:domain"))
    implementation(project(":apps:libs:shared-domain"))
    implementation(project(":apps:libs:shared-api"))
    
    // Spring dependencies
    implementation(libs.spring.boot.starter.validation)
    
    // Testing
    testImplementation(libs.spring.boot.starter.test)
}