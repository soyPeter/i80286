rootProject.name = "myinvestor-080286"

//// Enable version catalog
//enableFeaturePreview("VERSION_CATALOGS")

// Common libraries
include(":services:libs:common-domain")
include(":services:libs:common-application")
include(":services:libs:common-infrastructure")
include(":services:libs:common-test")
include(":services:libs:common-contracts")

// Services
include(":services:customer-service")

// API Gateway
include(":services:api-gateway")

// Service Registry
//include("service-registry")

// Build tools
include("build-tools:checkstyle")
include("build-tools:spotless")
include("build-tools:test-common")

// Configure project structure
project(":services:customer-service").projectDir = file("services/customer-service")
project(":services:api-gateway").projectDir = file("services/api-gateway")
project(":services:libs:common-domain").projectDir = file("services/libs/common-domain")
project(":services:libs:common-application").projectDir = file("services/libs/common-application")
project(":services:libs:common-infrastructure").projectDir = file("services/libs/common-infrastructure")
project(":services:libs:common-test").projectDir = file("services/libs/common-test")
project(":services:libs:common-contracts").projectDir = file("services/libs/common-contracts")
project(":build-tools:checkstyle").projectDir = file("build-tools/checkstyle")
project(":build-tools:spotless").projectDir = file("build-tools/spotless")
project(":build-tools:test-common").projectDir = file("build-tools/test-common")
