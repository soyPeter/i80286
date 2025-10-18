# Arquitectura Blueprint 80286

Objetivo: Documentar la arquitectura hexagonal y la estructura DDD aplicada en este monorepo, con énfasis en separación de capas, contratos, convenciones de nombrado y estándares de testing, alineados con Java 25, Spring Boot 4.0.0-M3 y Gradle 9.1.0.


1. Principios de Arquitectura
- Hexagonal (Ports & Adapters): el dominio es independiente de frameworks; la comunicación se realiza a través de puertos definidos en el dominio/aplicación e implementados por adaptadores de infraestructura.
- DDD: modelado por bounded contexts; entidades ricas; value objects inmutables; agregados con invariantes; eventos de dominio cuando aplica.
- Unidireccionalidad: dominio no depende de aplicación ni infraestructura; aplicación no depende de infraestructura.
- Configuración por contrato: entradas/salidas definidas por interfaces (puertos) y DTOs.


2. Stack Tecnológico
- Java 25 (Gradle toolchains)
- Spring Boot 4.0.0-M3; Spring MVC/WebFlux según necesidad; Actuator; Micrometer.
- Persistencia: PostgreSQL (Flyway para migraciones). Alternativas JDBI/JPA según contexto.
- Mensajería: RabbitMQ (si aplica en el contexto).
- Resiliencia: Resilience4j (circuit breaker, retry, rate limiting, bulkhead).
- Build: Gradle Kotlin DSL; Version Catalog (libs.versions.toml).
- Testing: JUnit 5, AssertJ, Mockito, Testcontainers.


3. Hexagonal Architecture (aplicada)
- Dominio (core):
  - Contiene entidades, value objects, servicios de dominio, reglas e invariantes.
  - Define puertos de salida necesarios (interfaces) desde la perspectiva del negocio.
- Aplicación:
  - Orquesta casos de uso (commands/queries) a través de puertos de entrada.
  - Mapea DTOs ↔ dominio y coordina transacciones.
- Infraestructura:
  - Adaptadores primarios: REST controllers, consumidores de mensajes.
  - Adaptadores secundarios: persistencia (JPA/JDBI), clientes HTTP, mensajería, etc.
  - Configuración técnica aislada.

Diagrama (simplificado):
```
[Adapter In] -> (Ports In) -> [Application UC] -> (Ports Out) -> [Adapter Out]
                           ^                         |
                           |                         v
                         [Domain] <--------------------
```


4. Estructura DDD por módulo
Cada bounded context en apps/<contexto> se divide en tres submódulos: domain, app, infra.

Ejemplo general:
```
apps/<contexto>/
  ├─ domain/
  │   ├─ model/
  │   │   ├─ entity/
  │   │   └─ vo/
  │   ├─ repository/        # Puertos (interfaces) para persistencia u otros servicios
  │   ├─ service/           # Lógica de dominio que no cabe en entidades
  │   └─ event/             # Eventos de dominio (si aplica)
  ├─ app/
  │   ├─ usecase/           # Commands/Queries
  │   ├─ dto/               # Objetos de transferencia
  │   ├─ port/              # input/output
  │   ├─ mapper/
  │   └─ service/           # Orquestación de casos de uso
  └─ infra/
      ├─ adapter/
      │   ├─ input/ (rest, messaging)
      │   └─ output/ (persistence, http)
      └─ config/
```

Módulos compartidos en apps/libs:
- shared-domain: Value Objects comunes, errores y contratos de dominio compartidos.
- shared-api: DTOs comunes, validaciones, contratos de API.
- shared-infra: configuración técnica compartida y adaptadores base.


5. Separación de responsabilidades
- Dominio: reglas de negocio, invariantes y decisión del lenguaje ubicuo. No depende de frameworks.
- Aplicación: orquestación, transacciones, coordinación de puertos. Mínima lógica de negocio.
- Infraestructura: detalles técnicos, traducción de protocolos, implementación de puertos.


6. Convenciones de naming y organización
- Entidades: singular (User, Customer).
- Value Objects: descriptivos e inmutables (EmailAddress, PhoneNumber).
- Repositorios (puertos dominio): prefijo I + Entidad + Repository (IUserRepository).
- Implementaciones (infra): Entidad + Repository + Sufijo tecnológico (UserRepositoryJpa/UserRepositoryJdbi).
- Casos de uso: Verbo en infinitivo + Sustantivo (CreateUserUseCase, FindUserByIdUseCase).
- Paquetes por capa dentro del contexto: domain.*, app.*, infra.*.
- DTOs de entrada/salida en adaptadores REST con sufijos Req/Res cuando aplique.


7. Patrones aplicados y recomendaciones
- Factory Method: para creación compleja de entidades/VOs.
- Repository: puertos en dominio, implementaciones en infra.
- Command/Query (CQRS lite): separar comandos de consultas en casos de uso.
- Dependency Injection: por constructor; evitar @Autowired en campos/setters.
- Specification: para reglas de negocio complejas y filtrado.
- Resiliencia: timeouts, retries, CB, bulkheads en adaptadores externos.
- Idempotencia: especialmente en comandos expuestos vía HTTP/mensajería.


8. Estándares de testing por capas
- Dominio: tests unitarios puros; 80%+ cobertura mínima.
- Aplicación: tests unitarios de casos de uso con dobles de puertos; pruebas transaccionales cuando aplique.
- Infraestructura: tests de integración por adaptador con Testcontainers (Postgres, RabbitMQ). Tests de contrato si hay interacción entre servicios.
- Arquitectura: tests de arquitectura para asegurar dependencias unidireccionales y cumplimiento de reglas hexagonales.

Buenas prácticas:
- TDD para componentes críticos.
- Fixtures reutilizables y datos deterministas.
- Mockear dependencias externas en tests unitarios; usar Testcontainers en integración.


9. Microservicios: buenas prácticas
- Bounded contexts bien delimitados; despliegue independiente por servicio.
- APIs REST pequeñas, versionadas (v1, v2) y documentadas (OpenAPI).
- Configuración externalizada por entorno (12-factor app). Secretos gestionados de forma segura.
- Observabilidad: Actuator, Micrometer, logs estructurados (MDC), tracing distribuido.
- Tolerancia a fallos: Circuit Breaker, Retry, Rate Limiting, Bulkhead. Timeouts obligatorios en clientes externos.
- Backward compatibility en contratos; gestión de deprecaciones.


10. Integración con el repositorio
- Toolchains Java 25 activas via Gradle.
- Versiones gestionadas en gradle/libs.versions.toml (Spring Boot 4.0.0-M3, Spring Cloud 2023.0.0, Micrometer 1.14.x, Testcontainers 1.19.x, Flyway 11.x, PostgreSQL 42.7.x).
- Módulos declarados en settings.gradle.kts:
  - libs: apps:libs:shared-api, apps:libs:shared-domain, apps:libs:shared-infra
  - contexts: apps:admin, apps:aml, apps:attendance, apps:customer, apps:fde, apps:report, apps:security, apps:user (cada uno con :domain, :app, :infra)


11. Ejemplo práctico (REST → Caso de uso → Dominio → Repositorio)
```
POST /api/v1/users
  UserController (infra/adapter/input/rest)
    -> CreateUserUseCase (app/usecase/command)
      -> IUserRepository (domain/repository)
        -> UserRepositoryJpa (infra/adapter/output/persistence)
```

DTOs: UserCreateReq (entrada) / UserRes (salida). El mapeo entre DTO y dominio se realiza en el caso de uso o mediante mapper dedicado en app.


12. Diagramas y referencias
- Usa C4 (Context, Container, Component) para documentar vistas.
- Mantén ejemplos ASCII simples en README/ARCHITECTURE para orientación rápida.
- Referencias: Evans (DDD), Vernon (IDDD), Cockburn (Hexagonal Architecture), documentación oficial de Spring/Resilience4j/Testcontainers.


---

## Anexo (contenido anterior - desactualizado)

## Objetivo:
Diseñar un sistema basado en arquitectura hexagonal y principios DDD usando Spring Boot 3 para implementar servicios REST.
## 1. CONTEXTO:
- Microservicios con Spring Boot 3 y Spring MVC
- API REST síncrona
- Mensajería asíncrona con RabbitMQ
- Tests unitarios, de integración y arquitectura

## 2. STACK TECNOLÓGICO:
- Java 21 y Spring Boot 3
- Spring MVC para APIs REST
- Spring Data JPA para persistencia en PostgreSQL
- RabbitMQ para mensajería asíncrona
- Resilience4j para Circuit Breaker, Retry, Rate Limiting y Bulkhead
- Spring Cloud Config para configuración centralizada
- Docker y Docker Compose para desarrollo y despliegue
- Gradle con Kotlin DSL para build
- JUnit 5, AssertJ, Mockito y TestContainers para testing
- Micrometer para métricas y observabilidad
- Spring Boot Actuator para health checks y métricas

## 3. ARQUITECTURA HEXAGONAL (PORTS & ADAPTERS):
- Capa de Dominio (centro): Entidades, Value Objects, Servicios de Dominio y Puertos
- Capa de Aplicación: Casos de uso, DTOs, Orquestadores y Servicios de Aplicación
- Capa de Infraestructura: Adaptadores primarios (REST, RabbitMQ) y secundarios (repositorios, clientes HTTP)

## 4. PRINCIPIOS DDD:
- Contextos acotados claros para cada microservicio
- Entidades ricas con comportamiento (no anémicas)
- Value Objects inmutables para conceptos sin identidad
- Agregados con reglas de invarianza
- Repositorios por agregado
- Eventos de dominio para comunicación entre contextos
- Factory patterns para creación compleja
- Separación de lenguaje ubicuo y técnico


## 5. ESTRUCTURA GENERAL DE CARPETAS:
```
/
├── gradle/
│   ├── libs.versions.toml    # Gestión central de versiones
│   └── wrapper/
├── services/                     # Verticales de negocio
│    ├── libs/                         # Bibliotecas compartidas
│    │   ├── common-domain/
│    │   │   ├── src/main/java/com/company/common/domain/
│    │   │   │   ├── model/
│    │   │   │   │   ├── vo/            # Value Objects compartidos
│    │   │   │   │   └── event/         # Eventos de dominio compartidos
│    │   │   │   └── exception/
│    │   │   └── build.gradle
│    │   ├── common-application/
│    │   │   ├── src/main/java/com/company/common/application/
│    │   │   │   ├── dto/               # DTOs base compartidos
│    │   │   │   ├── usecase/           # Interfaces de casos de uso comunes
│    │   │   │   └── config/            # Configuraciones a nivel de aplicación
│    │   │   │       └── validation/    # Configuración de validadores
│    │   │   └── build.gradle
│    │   ├── common-infrastructure/
│    │   │   ├── src/main/java/com/company/common/infrastructure/
│    │   │   │   ├── persistence/
│    │   │   │   │   ├── model/         # Clases base para entidades
│    │   │   │   │   └── config/        # Configuración JPA compartida
│    │   │   │   ├── messaging/
│    │   │   │   │   ├── rabbitmq/      # Configuración común para RabbitMQ
│    │   │   │   │   │   ├── config/
│    │   │   │   │   │   │   ├── RabbitMQCommonConfig.java
│    │   │   │   │   │   │   └── RabbitMQErrorHandler.java
│    │   │   │   │   │   ├── producer/
│    │   │   │   │   │   │   └── base/
│    │   │   │   │   │   └── consumer/
│    │   │   │   │   │       └── base/
│    │   │   │   │   └── event/
│    │   │   │   │       ├── BaseEvent.java
│    │   │   │   │       └── EventPublisher.java
│    │   │   │   ├── exception/
│    │   │   │   │   ├── GlobalExceptionHandler.java
│    │   │   │   │   └── ApiError.java
│    │   │   │   ├── web/
│    │   │   │   │   ├── config/
│    │   │   │   │   │   ├── WebMvcConfig.java
│    │   │   │   │   │   └── SwaggerConfig.java
│    │   │   │   │   └── filter/
│    │   │   │   ├── security/
│    │   │   │   │   ├── config/
│    │   │   │   │   │   └── SecurityConfig.java
│    │   │   │   │   ├── filter/
│    │   │   │   │   │   └── JwtAuthenticationFilter.java
│    │   │   │   │   └── util/
│    │   │   │   │       └── JwtUtil.java
│    │   │   │   ├── logging/
│    │   │   │   │   ├── config/
│    │   │   │   │   │   └── LoggingConfig.java
│    │   │   │   │   └── aspect/
│    │   │   │   │       └── LoggingAspect.java
│    │   │   │   └── config/
│    │   │   │       ├── audit/         # Configuración de auditoría común
│    │   │   │       │   ├── AuditConfig.java
│    │   │   │       │   └── AuditableEntityListener.java
│    │   │   │       └── resilience/    # Configuración de Resilience4j
│    │   │   │           ├── CircuitBreakerConfig.java
│    │   │   │           ├── RetryConfig.java
│    │   │   │           ├── RateLimiterConfig.java
│    │   │   │           └── BulkheadConfig.java
│    │   │   ├── src/main/resources/
│    │   │   │   └── config/
│    │   │   │       ├── rabbitmq-common.properties
│    │   │   │       └── resilience-common.properties
│    │   │   └── build.gradle
│    │   ├── common-test/              # Utilidades comunes para tests
│    │   │   ├── src/main/java/com/company/common/test/
│    │   │   │   ├── fixture/
│    │   │   │   │   ├── domain/
│    │   │   │   │   └── application/
│    │   │   │   ├── config/
│    │   │   │   │   ├── TestContainersConfig.java
│    │   │   │   │   └── WireMockConfig.java
│    │   │   │   └── util/
│    │   │   │       ├── RestAssuredUtil.java
│    │   │   │       └── MockMvcUtil.java
│    │   │   └── build.gradle
│    │   └── common-contracts/         # Definición de contratos entre servicios
│    │       ├── src/main/resources/contracts/
│    │       │   ├── customer/
│    │       │   ├── product/
│    │       │   └── order/
│    │       └── build.gradle
│    ├── api-gateway/                  # Punto de entrada unificado (opcional)
│    │   ├── src/main/java/com/company/gateway/
│    │   │   ├── config/
│    │   │   │   ├── GatewayConfig.java
│    │   │   │   └── RouteConfig.java
│    │   │   ├── filter/
│    │   │   │   ├── AuthenticationFilter.java
│    │   │   │   └── LoggingFilter.java
│    │   │   └── security/
│    │   │       └── config/
│    │   │           └── SecurityConfig.java
│    │   ├── src/main/resources/
│    │   │   ├── application.yml
│    │   │   └── routes/
│    │   │       ├── customer-routes.yml
│    │   │       ├── product-routes.yml
│    │   │       ├── order-routes.yml
│    │   │       └── payment-routes.yml
│    │   └── build.gradle
│    ├── customer-service/         # Vertical de gestión de clientes
│    │   ├── src/main/java/com/company/customer/
│    │   │   ├── application/
│    │   │   │   ├── usecase/
│    │   │   │   │   ├── command/
│    │   │   │   │   └── query/
│    │   │   │   ├── dto/
│    │   │   │   ├── event/
│    │   │   │   ├── port/
│    │   │   │   │   ├── input/
│    │   │   │   │   └── output/
│    │   │   │   ├── mapper/
│    │   │   │   └── service/
│    │   │   ├── domain/
│    │   │   │   ├── model/
│    │   │   │   │   ├── entity/
│    │   │   │   │   ├── vo/
│    │   │   │   │   └── enum/
│    │   │   │   ├── repository/
│    │   │   │   ├── service/
│    │   │   │   ├── exception/
│    │   │   │   └── event/
│    │   │   │       └── listener/
│    │   │   └── infrastructure/
│    │   │       ├── adapter/
│    │   │       │   ├── input/
│    │   │       │   │   ├── rest/
│    │   │       │   │   │   ├── req/
│    │   │       │   │   │   ├── res/
│    │   │       │   │   │   └── mapper/
│    │   │       │   │   └── rabbitmq/
│    │   │       │   │       ├── consumer/
│    │   │       │   │       └── mapper/
│    │   │       │   └── output/
│    │   │       │       ├── persistence/
│    │   │       │       │   ├── entity/
│    │   │       │       │   ├── repository/
│    │   │       │       │   └── mapper/
│    │   │       │       ├── http/
│    │   │       │       │   ├── client/
│    │   │       │       │   ├── req/
│    │   │       │       │   ├── res/
│    │   │       │       │   └── mapper/
│    │   │       │       └── rabbitmq/
│    │   │       │           ├── producer/
│    │   │       │           └── mapper/
│    │   │       ├── config/
│    │   │       │   ├── rabbitmq/
│    │   │       │   │   ├── RabbitMQConfig.java
│    │   │       │   │   └── RabbitMQProperties.java
│    │   │       │   ├── jpa/
│    │   │       │   │   ├── JpaConfig.java
│    │   │       │   │   └── JpaProperties.java
│    │   │       │   ├── web/
│    │   │       │   │   ├── WebConfig.java
│    │   │       │   │   └── WebProperties.java
│    │   │       │   ├── resilience/
│    │   │       │   │   └── ResilienceConfig.java
│    │   │       │   └── ApplicationConfig.java
│    │   │       └── exception/
│    │   ├── src/main/resources/
│    │   │   ├── application.yml
│    │   │   ├── application-dev.yml
│    │   │   └── application-prod.yml
│    │   └── build.gradle
│    ├── onboarding-service/          # Vertical de productos
│    │   └── [misma estructura interna]
│    ├── referral-service/            # Vertical de pedidos
│    │   └── [misma estructura interna]
│    └── signup-service/          # Vertical de pagos
│        └── [misma estructura interna]
├── build.gradle                  # Build principal del proyecto
├── settings.gradle               # Configuración de subproyectos
└── infrastructure/               # Configuración de infraestructura
    ├── docker/
    │   ├── docker-compose.yml
    │   └── services/
    │       ├── rabbitmq/
    │       │   └── rabbitmq.conf
    │       └── postgres/
    │           └── init-scripts/
    ├── kubernetes/
    │   ├── base/
    │   └── overlays/
    │       ├── dev/
    │       ├── staging/
    │       └── prod/
    └── db/
        ├── migrations/           # Migraciones Flyway
        │   ├── customer/
        │   ├── product/
        │   ├── order/
        │   └── payment/
        └── scripts/
            ├── init/
            └── audit/
```
## 6. NORMAS DE ESTILO DE CÓDIGO JAVA Y PATRONES AVANZADOS:
- Principios fundamentales:
    - SOLID: adherirse estrictamente a estos principios
    - Inmutabilidad: preferir objetos inmutables siempre que sea posible
    - Tell, don't ask: minimizar la exposición del estado interno
    - Composición sobre herencia: preferir composición excepto cuando:
        - Ambas clases están en el mismo dominio lógico
        - La subclase es un subtipo apropiado de la superclase
        - La implementación de la superclase es necesaria para la subclase
        - Las mejoras de la subclase son principalmente aditivas

- Características modernas de Java 21:
    - Records para DTOs, Value Objects y transferencia de datos
    - Text blocks para consultas SQL/JPQL complejas y JSON
    - Pattern matching para instanceof con binding de variables
    - Sealed classes para modelado de dominio limitado
    - Virtual threads para operaciones bloqueantes (con @Async)
    - Switch expressions con sintaxis moderna
    - Functional interfaces y Streams API para procesamiento de colecciones

- Convenciones de nomenclatura:
    - CamelCase para variables, métodos y campos
    - PascalCase para clases e interfaces
    - UPPER_SNAKE_CASE para constantes
    - Prefijos "get", "set", "is" para métodos convencionales
    - Nombres autodescriptivos que reflejen el lenguaje de dominio
    - Para DTOs seguir estos sufijos:
        - Controller incoming data -> sufijo "Req" (OrderReq)
        - Controller outgoing data -> sufijo "Res" (OrderRes)
        - SQL row representation -> sufijo "Table" (TopupOrderTable)
        - NO SQL document -> sufijo "Document" (TopupOrderDocument)

    - Para métodos REST seguir estos verbos:
        - GET -> "retrieve" (retrieveTopupOrder)
        - POST -> "create" (createTopupOrder)
        - PATCH -> "update" (updateTopupOrder)
        - PUT -> "upsert" (upsertTopupOrder)
        - DELETE -> "delete" (deleteTopupOrder)

    - Para casos de uso complejos, usar verbos en modo imperativo:
        - RegisterUser, DepositFunds, SendMoneyBetweenHolders

- Diseño de clases:
    - Clases para entidades de dominio
    - Records para todos los DTOs (req, res, table, document)
    - Constructores completos para inyección de dependencias
    - Constructores privados para Value Objects o Factory Methods
    - Usar static factory methods descriptivos en lugar de constructores públicos
    - Métodos defensivos para validación de argumentos (Objects.requireNonNull)
    - Usar Optional para valores opcionales (no usar null)

- Control de acceso:
    - Hacer campos private por defecto
    - Limitar el uso del modificador public a la API necesaria
    - Preferir package-private (default) para implementaciones internas
    - Usar interfaces para ocultar detalles de implementación

- Manejo de excepciones:
    - Excepciones personalizadas para errores de dominio
    - RuntimeException para errores irrecuperables
    - Checked exceptions solo para casos donde la recuperación es posible
    - Evitar swallowing exceptions (capturar sin manejar)
    - Siempre incluir mensaje descriptivo y causa original

- Prácticas específicas:
    - NO usar Lombok - utilizar records y métodos nativos de Java 21
    - Evitar auto-boxing/unboxing implícito (usar tipos primitivos para rendimiento)
    - Implementar equals() y hashCode() para entidades y value objects
    - Sobrescribir toString() para facilitar depuración
    - Agregar anotaciones @Override cuando corresponda
    - Simplicidad primero: modelar lo suficientemente bien a un costo manejable
    - Sin caracteres de tabulación, sin espacios al final
    - Sin imports con comodín (*)
    - Usar static imports para cosas bien conocidas (Boolean.TRUE, StandardCharsets.UTF_8, etc.)
    - ALL_CAPS para campos static final
    - Siempre usar llaves {} en bloques if/else
    - Comentar solo para explicar POR QUÉ, no QUÉ hace el código

- Logging:
    - ERROR: para configuraciones incorrectas o excepciones inesperadas
    - INFO: para diagnósticos poco frecuentes, inicio y configuración inicial
    - DEBUG: para diagnósticos ligeros adicionales durante la ejecución (verificar log.isDebugEnabled())
    - TRACE: para diagnósticos pesados cuando la salida esperada es más de unas pocas líneas
    - Loggers HTTP esenciales al interactuar con sistemas externos

- Guías de estilo:
    - Seguir Google Java Style Guide como guía principal
    - Consultar Java Style Guide de Twitter para ejemplos prácticos

## 7. NORMAS DE ESTILO PARA SPRING BOOT Y PATRONES ARQUITECTÓNICOS:
- Inyección de dependencias:
    - Usar inyección SIEMPRE por constructor sin @Autowired (Spring lo detecta automáticamente)
    - Declarar dependencias como campos finales
    - NO usar @Autowired en campos o setters
    - Para casos complejos, utilizar builder pattern con @Builder
    - Evitar dependencias cíclicas

- Configuración de aplicación:
    - Usar @ConfigurationProperties con clases tipadas (no @Value)
    - Implementar validación para propiedades (@Validated, @NotNull, etc.)
    - Agrupar propiedades relacionadas en clases anidadas
    - Usar prefijos significativos para las propiedades
    - Implementar @PostConstruct para validación compleja de configuración

- Diseño de componentes Spring:
    - @RestController: solo para adaptadores primarios REST (capa externa)
    - @Service: para implementaciones de casos de uso (capa de aplicación)
    - @Repository: solo para interfaces de adaptadores secundarios (no directamente en JPA repositories)
    - @Component: para otros beans que no entren en categorías anteriores
    - @Configuration: para clases de configuración de infraestructura

- Transacciones:
    - @Transactional en la capa de aplicación (nivel de caso de uso)
    - @Transactional(readOnly=true) para operaciones de solo lectura
    - @Transactional(propagation=...) para control granular
    - NO usar transacciones en controladores o adaptadores
    - Definir estrategia de manejo de excepciones transaccionales

- Spring Data:
    - Definir interfaces JpaRepository con tipos correctos
    - Nombrar métodos siguiendo la nomenclatura de Spring Data
    - Usar @Query con JPQL nombrado para consultas complejas
    - Preferir consultas derivadas (findByX) para casos simples
    - Implementar Specification para filtros dinámicos
    - Usar Pageable para paginación (siempre en APIs que devuelven colecciones)
    - Definir proyecciones para consultas específicas

- REST API:
    - Usar DTOs como Java records para entrada/salida
    - RESTful: seguir convenciones de recursos y verbos HTTP
    - Implementar versionado en URLs (/api/v1/...)
    - @ResponseStatus para códigos HTTP específicos
    - Usar ResponseEntity para control completo de respuestas
    - Documentar con OpenAPI (@Operation, @ApiResponse, etc.)
    - Implementar HATEOAS cuando sea relevante
    - Seguir Microsoft API Guidelines y HTTP RFC 7231 para diseño de API

- Validación:
    - @Valid/@Validated en controladores para validar entrada
    - Implementar validaciones personalizadas con @Constraint
    - Validar en capa de dominio para reglas de negocio
    - Usar grupos de validación para diferentes contextos
    - Implementar mensajes de error internacionalizados

- Seguridad:
    - @PreAuthorize con SpEL para autorización basada en roles/permisos
    - Implementar autenticación stateless con JWT
    - Usar SecurityContext correctamente (nunca hardcoded)
    - Implementar auditoría con @CreatedBy, @LastModifiedBy
    - Evitar información sensible en logs o respuestas

- Manejo de excepciones:
    - @RestControllerAdvice para manejar excepciones globalmente
    - Mapear excepciones de dominio a códigos HTTP apropiados
    - Estructurar respuestas de error consistentes (timestamp, path, message, etc.)
    - Ocultar detalles técnicos en producción
    - Implementar logging adecuado para excepciones

- Aspectos avanzados:
    - Usar @Async para operaciones no bloqueantes (con Virtual Threads)
    - Implementar resilience con Resilience4j (circuit breaker, retry, rate limiter, bulkhead)
    - Utilizar caching declarativo (@Cacheable, @CacheEvict)
    - Implementar auditoría con Spring Data Auditing
    - Usar Spring Actuator para monitoreo y health checks

- Puertos y acciones:
    - Nombres concisos que oscurezcan la complejidad de implementación
    - Preferir: `login(Principal principal)`
    - Sobre: `loginAndGenerateStatsByPassingInfoToTheStatsService(Principal principal)`

## 8. PATRONES DE ARQUITECTURA Y DISEÑO:
- Implementar puertos primarios como interfaces de "actions" en la capa de aplicación
    - Ejemplos de comandos: `CreateUserAction`, `UpdateUserPasswordAction`
    - Ejemplos de consultas: `FindUserByIdAction`, `GetUserProfileAction`

- Implementar puertos secundarios como interfaces en el dominio
    - Ejemplos para comandos: `UserCommandRepository`, `NotificationPort`
    - Ejemplos para consultas: `UserQueryRepository`, `AuthenticationQueryPort`

- Implementar adaptadores primarios en la capa de infraestructura
    - Ejemplos: `UserCommandController`, `UserQueryController`

- Implementar adaptadores secundarios en la capa de infraestructura con sufijo "Adapter"
    - Para comandos: `PostgresUserCommandRepositoryAdapter`
    - Para consultas: `PostgresUserQueryRepositoryAdapter` (usando vistas de DB)

- Separar datastore de lectura y escritura:
    - Configurar datasources en application.yml siguiendo esta convención:
        - **default**: datasource de sólo lectura (réplicas)
        - **default-w**: datasource de sólo escritura (instancia writer)
        - **default-ddl**: datasource para lenguaje de definición de datos (si es necesario)

    - Aplicar Eventual Consistency para manejar posibles inconsistencias entre modelos de lectura/escritura
    - Documentar en comentarios los trade-offs de CAP aplicados (Consistency vs. Availability)

- Implementar vistas materializadas en PostgreSQL para consultas complejas
    - Usar Flyway para crear/actualizar estas vistas
    - Mapear estas vistas a entidades JPA específicas para consulta (sufijo "Table")

- Separar entidades de dominio de entidades JPA
- Usar agregados, entidades y value objects según DDD
- Implementar eventos de dominio para mantener consistencia eventual entre modelos
- Diseñar alrededor de contextos acotados (Bounded Contexts)
- Usar un lenguaje ubicuo en cada contexto acotado

## 9. PATRONES APLICADOS:
- Correlation ID:
    - Implementar un mecanismo para rastrear solicitudes a través de múltiples servicios
    - Crear o propagar los siguientes headers:
        - **request-trace-id**: identifica de forma única la transacción
        - **request-username**: identifica al usuario que realiza la solicitud

    - Propagar desde el punto de entrada del servidor hasta la base de datos y viceversa
    - Mostrar esta información en logs para facilitar el rastreo de transacciones
    - Implementar filtros y interceptores para manejar automáticamente los headers
    - Configurar MDC (Mapped Diagnostic Context) para incluir trace-id en todos los logs

- CQRS lite:
    - Separar operaciones de lectura y escritura a nivel de datasource
    - Configurar diferentes usuarios de base de datos con diferentes permisos
    - Usar réplicas para operaciones de lectura y la instancia principal para escritura
    - Manejar la consistencia eventual como un trade-off aceptable en favor de la disponibilidad
    - Documentar claramente los posibles escenarios de inconsistencia temporal

- Outbox Pattern:
    - Implementar para garantizar la entrega de eventos en transacciones distribuidas
    - Almacenar eventos en una tabla "outbox" como parte de la transacción original
    - Procesar eventos de la tabla outbox de forma asíncrona para publicación
    - Garantizar al menos una entrega de mensajes entre servicios
    - Usar para mantener consistencia eventual entre servicios

- Saga Pattern:
    - Implementar para transacciones que abarcan múltiples servicios
    - Orquestar secuencias de transacciones locales en diferentes servicios
    - Proporcionar transacciones compensatorias para rollback en caso de fallo
    - Mantener la consistencia eventual entre servicios
    - Documentar el flujo de saga para cada proceso de negocio crítico

- Circuit Breaker con Resilience4j:
    - Implementar para prevenir fallos en cascada entre servicios
    - Configurar umbrales de fallos y tiempos de espera apropiados
    - Proporcionar respuestas alternativas cuando el circuito está abierto
    - Monitorear el estado de los circuitos en cada servicio
    - Integrar con Spring Actuator para exponer métricas y estados

- Retry Pattern con Resilience4j:
    - Configurar reintentos automáticos para operaciones fallidas
    - Implementar backoff exponencial con jitter para espaciar los reintentos
    - Limitar el número máximo de reintentos
    - Registrar intentos fallidos para monitoreo
    - Combinar con Circuit Breaker para máxima resiliencia

- Rate Limiting con Resilience4j:
    - Implementar límites de tasa para proteger APIs y servicios
    - Configurar límites basados en cliente, usuario o tipo de operación
    - Implementar algoritmos de bucket de tokens para rate limiting
    - Proporcionar respuestas claras cuando se supera el límite (429 Too Many Requests)
    - Monitorear tasas de solicitud para ajustar límites según sea necesario

- Bulkhead con Resilience4j:
    - Aislar recursos para prevenir fallos en cascada
    - Configurar pools de hilos separados para diferentes operaciones
    - Limitar la concurrencia máxima por tipo de operación
    - Rechazar solicitudes cuando el bulkhead está lleno
    - Combinar con Circuit Breaker para máxima protección

## 10. GESTIÓN DE ESQUEMA DE BASE DE DATOS:
- Usar Flyway para migraciones de base de datos
- Scripts de migración en src/main/resources/db/migration
- Convención de nomenclatura: V{versión}__{descripción}.sql
- Crear vistas para operaciones de lectura: V{versión}__create_view_{nombre_vista}.sql
- Configurar Hibernate con ddl-auto=validate
- Separar scripts de índices para optimizar consultas en las vistas
- Auditoría y Tracking de Cambios:
    - Implementar Change Data Capture (CDC):
        - Utilizar PostgreSQL Logical Replication para CDC en tablas críticas
        - Configurar pgoutput para generar logs WAL (Write-Ahead Logging)
        - Implementar Debezium para consumir eventos CDC y publicarlos en RabbitMQ
        - Definir las tablas a auditar en la configuración de Debezium

    - Triggers para Auditoría Detallada:
        - Crear tablas de historia con el esquema "_history" (por ejemplo, "users_history")
        - Implementar triggers AFTER INSERT/UPDATE/DELETE que registren todos los cambios
        - Ejemplo de tabla de historia:
``` sql
      CREATE TABLE users_history (
          history_id SERIAL PRIMARY KEY,
          user_id BIGINT NOT NULL,
          action VARCHAR(10) NOT NULL, -- 'INSERT', 'UPDATE', 'DELETE'
          changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
          changed_by VARCHAR(255) NOT NULL,
          old_data JSONB,
          new_data JSONB,
          change_reason VARCHAR(255)
      );
```
- Ejemplo de trigger:
``` sql
      CREATE OR REPLACE FUNCTION log_user_changes()
      RETURNS TRIGGER AS $$
      BEGIN
          IF TG_OP = 'INSERT' THEN
              INSERT INTO users_history(user_id, action, changed_by, old_data, new_data, change_reason)
              VALUES (NEW.id, 'INSERT', CURRENT_USER, NULL, to_jsonb(NEW), NEW.change_reason);
              RETURN NEW;
          ELSIF TG_OP = 'UPDATE' THEN
              INSERT INTO users_history(user_id, action, changed_by, old_data, new_data, change_reason)
              VALUES (NEW.id, 'UPDATE', CURRENT_USER, to_jsonb(OLD), to_jsonb(NEW), NEW.change_reason);
              RETURN NEW;
          ELSIF TG_OP = 'DELETE' THEN
              INSERT INTO users_history(user_id, action, changed_by, old_data, new_data, change_reason)
              VALUES (OLD.id, 'DELETE', CURRENT_USER, to_jsonb(OLD), NULL, NULL);
              RETURN OLD;
          END IF;
      END;
      $$ LANGUAGE plpgsql;
      
      CREATE TRIGGER user_audit
      AFTER INSERT OR UPDATE OR DELETE ON users
      FOR EACH ROW EXECUTE FUNCTION log_user_changes();
```
- Implementar estas migraciones con Flyway:
    - V1__initial_schema.sql: Esquema base
    - V2__create_history_tables.sql: Tablas de historia
    - V3__create_audit_triggers.sql: Funciones y triggers

- Consideraciones y ventajas:
    - Los triggers mantienen el historial completo independientemente de cómo se accede a los datos
    - Funciona incluso si se hacen modificaciones directas a la base de datos
    - Proporciona una pista de auditoría inmutable para cumplimiento normativo
    - Combinado con soft delete, ofrece múltiples niveles de protección de datos
    - Permite análisis forense y recuperación de datos a cualquier punto en el tiempo

## 11. MEJORES PRÁCTICAS DE PRUEBAS CON TESTCONTAINERS:
- Configurar Testcontainers para simular entorno de producción:
    - PostgresTestContainer configurado con dos bases de datos (command/query)
    - Aplicar migraciones Flyway automáticamente al iniciar contenedores
    - RabbitMQTestContainer para probar mensajería asíncrona

- Implementar test fixtures para escenarios comunes:
    - AbstractIntegrationTest como clase base con configuración común
    - Usar @TestPropertySource para cargar application-test.yml

- Configurar datasources separados para test:
    - TestCommandDataSourceConfig para comandos
    - TestQueryDataSourceConfig para consultas

- Tests unitarios:
    - Probar componentes individuales
    - Usar @WebMvcTest para controladores con MockMvc
    - Preferir dependencias reales en lugar de mocks cuando sea posible

- Tests de integración:
    - Usar @SpringBootTest con Testcontainers
    - Probar interacciones entre componentes reales
    - Validar comportamiento del sistema completo

- Tests de API:
    - Probar endpoints REST completos
    - Validar formatos JSON/XML de respuesta
    - Verificar códigos HTTP y manejo de errores

- Nombres de test descriptivos que expliquen claramente qué se verifica
- Seguir patrón Arrange-Act-Assert (Given-When-Then)
- Usar AssertJ para aserciones más legibles
- Tests de rendimiento básicos para validar consultas eficientes
- Cobertura de código mínima del 80%

## 12. CONFIGURACIÓN:
- Ambientes múltiples (dev, test, prod) con propiedades específicas
- Configuración separada para DataSource de comando y consulta:
    - spring.datasource.command.* para instancia writer
    - spring.datasource.query.* para réplicas reader

- Gestión centralizada de dependencias en libs.versions.toml
- Dockerfiles y docker-compose para desarrollo local
    - Incluir configuración de PostgreSQL con réplicas para simular Aurora

## 13. CALIDAD DEL CÓDIGO:
- Incluye Checkstyle, SpotBugs y PMD
- Configura JaCoCo para cobertura de código
- Añade pruebas unitarias e integración con JUnit 5 y Mockito

## 14. GESTIÓN DE ERRORES:
- Implementa un manejador global de excepciones
- Define DTOs de error estandarizados
- Agrega validación para todos los endpoints REST

## 15. INTERFACES Y CONTRATOS:
- Definir interfaces claras para todos los puertos
- Documentar contratos con OpenAPI (Springdoc)
- Versionado de API en URL (/api/v1/resource)
- DTOs específicos para cada endpoint (no reutilizar)
- Validación con Bean Validation (javax.validation)
- Esquema de respuesta consistente:
    - success: boolean
    - data: payload o null
    - errors: array de errores o null
    - metadata: paginación, timestamps, etc.

## 16. MANEJO DE EVENTOS:
- Eventos de dominio para comunicación entre bounded contexts
- Implementación con Spring ApplicationEventPublisher
- Estructura del evento: tipo, timestamp, payload, metadatos
- Publicación asíncrona en RabbitMQ para eventos entre servicios
- Formato de eventos en JSON o Protocol Buffers

## 17. MONITORIZACIÓN Y OBSERVABILIDAD:
- Health checks con Spring Boot Actuator
- Métricas con Micrometer exportadas a Prometheus
- Logs estructurados en JSON con MDC para correlación
- Distributed tracing con Spring Cloud Sleuth y Zipkin
- Dashboards en Grafana para monitorización

## 18. DESPLIEGUE:
- Dockerización con multi-stage builds
- Docker Compose para entorno local y testing
- Kubernetes para producción
- Helm charts para despliegue
- GitLab CI/CD para pipelines automatizados
- Entornos: local, dev, test, stage, prod

## 19. CONSIDERACIONES:
- Usa interfaces para repositorios y servicios siguiendo principios SOLID
- Implementa seguridad a nivel de endpoint con @PreAuthorize
- Auditoría y Soft Deletes:
    - Implementa auditoría básica en todas las entidades:
        - created_at: timestamp de creación
        - updated_at: timestamp de última modificación
        - created_by: usuario o sistema que creó el registro
        - updated_by: usuario o sistema que realizó la última modificación

    - Implementa soft deletes para todas las entidades principales:
        - deleted: booleano para indicar si el registro está eliminado (default false)
        - deleted_at: timestamp de eliminación lógica (null si no está eliminado)
        - deleted_by: usuario que realizó la eliminación lógica

    - Crea una clase base AbstractAuditableEntity con estos campos
    - Configura un EntityListener para manejar automáticamente estos campos
    - Implementa un filtro global que excluya automáticamente registros con deleted=true
    - Proporciona métodos específicos en los repositorios para recuperar registros eliminados cuando sea necesario
    - Usa interfaces de Spring Data como SoftDeleteRepository para implementar estos comportamientos
    - Para consultas JOIN, asegúrate de incluir la condición deleted=false en todas las tablas involucradas

- Asegura que la comunicación entre servicios sea resiliente usando Circuit Breaker y Retry
- Implementa rate limiting para proteger APIs de abusos
- Documenta claramente los contratos de API y las transacciones entre servicios
- Estrategia de Auditoría en Múltiples Niveles:
    - Nivel 1 - Auditoría de aplicación con campos básicos (created_at, updated_at, etc.)
    - Nivel 2 - Soft delete para mantener datos eliminados lógicamente
    - Nivel 3 - Tablas de historia vía triggers para cambios detallados a nivel de campo
    - Nivel 4 - CDC para replicación de eventos de cambio en tiempo real

Esta estrategia en capas proporciona:
- Rendimiento optimizado para operaciones normales (Nivel 1)
- Recuperación sencilla de registros eliminados (Nivel 2)
- Historial detallado para auditoría y cumplimiento (Nivel 3)
- Integración con sistemas externos de analítica y monitoreo (Nivel 4)

Implementar según los requisitos de:
- Cumplimiento regulatorio (GDPR, SOX, PCI-DSS, etc.)
- Políticas internas de seguridad y auditoría
- Necesidades operativas (recovery, troubleshooting)
- Analítica avanzada y business intelligence

## 20. BONUS: IMPLEMENTACIÓN DE PATRONES AVANZADOS
- Event Sourcing para dominios donde la historia es crítica
- CQRS con separación completa del modelo de lectura/escritura
- Saga pattern para transacciones distribuidas
- API Gateway con Spring Cloud Gateway
- Implementación de Envelope Encryption para datos sensibles
- Feature Toggles con FF4J
- Cache distribuida con Redis
- Endpoints de Actuator para métricas de Resilience4j:
    - /actuator/circuitbreakers
    - /actuator/retries
    - /actuator/ratelimiters
    - /actuator/bulkheads

- Dashboard de monitoreo para Circuit Breakers y Rate Limiters

## 21. DOCUMENTACIÓN:
- Diagramas de arquitectura (C4 Model)
- Documentación de API con OpenAPI
- README completo con instrucciones de setup
- Documentación de patrones implementados
- Decisiones de arquitectura documentadas (ADRs)
- Runbooks para operaciones comunes
- Diagramas de componentes mostrando adaptadores y puertos
