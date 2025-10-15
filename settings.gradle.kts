rootProject.name = "blueprint-80286"

pluginManagement {
  repositories {
    gradlePluginPortal()
//        maven {
//            url = uri("http://localhost:8081/repository/maven-public/")
//            isAllowInsecureProtocol = true
//            credentials {
//                username = "admin"
//                password = "admin"
//            }
//        }
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}

// Librerías compartidas entre TODOS los bounded contexts
include("apps:libs:shared-api")        // DTOs comunes, validaciones cross-domain
include("apps:libs:shared-domain")     // Value Objects, interfaces y conceptos comunes
include("apps:libs:shared-infra")      // Configuraciones, adaptadores base, utils

// Bounded Context: FDE (ACTUAL - migración completa)
//include("apps:admin")
//include("apps:admin:domain")
//include("apps:admin:app")
//include("apps:admin:infra")
//
////// Bounded Context: AML (FUTURO - placeholder para preparar)
//include("apps:aml")
//include("apps:aml:app")
//include("apps:aml:domain")
//include("apps:aml:infra")
//
//// Bounded Context: PBC (FUTURO - placeholder)
//include("apps:pbc")
//include("apps:pbc:domain")
//include("apps:pbc:application")
//include("apps:pbc:infrastructure")
//
//// Bounded Context: Rules Management (FUTURO)
//include("apps:rules-management")
//include("apps:rules-management:domain")
//include("apps:rules-management:application")
//include("apps:rules-management:infrastructure")
//
//// Bounded Context: Gateway/Orchestrator (FUTURO)
//include("apps:gateway")
//include("apps:gateway:domain")
//include("apps:gateway:application")
//include("apps:gateway:infrastructure")
