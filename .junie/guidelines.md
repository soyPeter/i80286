## 1. Principios Fundamentales
### 1.1 Arquitectura Hexagonal
- **Separación clara de capas**: Dominio, Aplicación e Infraestructura.
- **Independencia del dominio**: El núcleo del negocio no debe depender de tecnologías externas.
- **Comunicación mediante puertos y adaptadores**: Definir interfaces claras para entrada y salida.

### 1.2 Principios DDD
- **Contextos acotados (Bounded Contexts)**: Delimitar claramente cada microservicio.
- **Entidades con comportamiento**: No utilizar modelos anémicos sin lógica.
- **Value Objects inmutables**: Para conceptos sin identidad.
- **Agregados**: Mantener invariantes de negocio dentro de límites transaccionales.
- **Eventos de dominio**: Para comunicación entre contextos acotados.

## 2. Estructura de Proyecto
### 2.1 Organización de Capas
- **Capa de Dominio** (centro):
  - `domain/model/entity`: Entidades ricas con comportamiento
  - `domain/model/vo`: Value Objects inmutables
  - `domain/repository`: Interfaces de repositorio (puertos)
  - `domain/service`: Servicios de dominio con lógica compleja
  - `domain/event`: Eventos de dominio

- **Capa de Aplicación**:
  - `application/usecase`: Casos de uso (commands/queries)
  - `application/dto`: Objetos de transferencia de datos
  - `application/port`: Interfaces para comunicación con el exterior
  - `application/service`: Orquestadores de casos de uso

- **Capa de Infraestructura**:
  - `infrastructure/adapter/input`: Adaptadores primarios (REST, mensajería)
  - `infrastructure/adapter/output`: Adaptadores secundarios (persistencia, clientes HTTP)
  - `infrastructure/config`: Configuraciones específicas de tecnología

### 2.2 Convenciones de Nomenclatura
- **Entidades**: Nombres sustantivos en singular (ej. , ) `Customer``Product`
- **Value Objects**: Nombres descriptivos (ej. `EmailAddress`, `PhoneNumber`)
- **Interfaces de repositorio**: Prefijo "I" + nombre de la entidad + "Repository" (ej. `ICustomerRepository`)
- **Implementaciones de repositorio**: Nombre de la entidad + "Repository" + Sufijo de tecnología (ej. `CustomerRepositoryJpa`)
- **Casos de uso**: Verbos en infinitivo + sustantivos (ej. , `FindProductByIdUseCase`) `CreateCustomerUseCase`

## 3. Guías de Implementación
### 3.1 Capa de Dominio
- Las entidades deben encapsular comportamiento y validaciones.
- Los value objects deben ser inmutables y validar su estado en construcción.
- Las interfaces de repositorio deben definirse desde la perspectiva del dominio.
- Los servicios de dominio solo cuando la lógica no pertenece naturalmente a una entidad.

### 3.2 Capa de Aplicación
- Los casos de uso representan intenciones de usuarios o sistemas.
- Los DTOs deben ser simples y sin lógica.
- Los mappers deben transformar objetos entre capas.
- Los puertos definen los contratos para adaptadores.

### 3.3 Capa de Infraestructura
- Los adaptadores primarios traducen peticiones externas a llamadas de aplicación.
- Los adaptadores secundarios implementan interfaces de repositorio.
- Los controladores REST deben ser delgados y solo orquestar llamadas a casos de uso.
- Las configuraciones técnicas deben aislarse en esta capa.

## 4. Patrones a Aplicar
### 4.1 Patrones Esenciales
- **Factory Method**: Para creación compleja de entidades y value objects.
- **Repository**: Para persistencia y recuperación de agregados.
- **Command/Query**: Separación de operaciones de modificación y consulta.
- **Dependency Injection**: Para inversión de control.
- **Specification**: Para encapsular reglas de negocio complejas.

### 4.2 Patrones Avanzados
- **Event Sourcing**: Para sistemas que necesitan auditoría completa.
- **CQRS**: Para separación avanzada de responsabilidades.
- **Saga**: Para transacciones distribuidas en microservicios.
- **Circuit Breaker**: Para tolerancia a fallos con Resilience4j.

## 5. Prácticas de Testing
### 5.1 Tipos de Tests
- **Tests Unitarios**: Para lógica de dominio y aplicación.
- **Tests de Integración**: Para adaptadores y configuraciones.
- **Tests de Arquitectura**: Para validar cumplimiento de reglas arquitectónicas.
- **Tests de Contrato**: Para validar contratos entre servicios.

### 5.2 Enfoque de Testing
- Test-Driven Development (TDD) para componentes críticos.
- Usar fixtures para datos de prueba consistentes.
- Mockear dependencias externas en tests unitarios.
- Usar TestContainers para tests de integración con bases de datos y mensajería.

## 6. Gestión de Dependencias y Build
### 6.1 Gestión de Dependencias
- Centralizar versiones en `libs.versions.toml`.
- Separar dependencias por entorno (main, test, development).
- Minimizar dependencias transitivas.

### 6.2 Configuración de Gradle
- Estructura modular para facilitar compilación independiente.
- Plugins comunes centralizados.
- Configuración específica por módulo cuando sea necesario.

## 7. Consideraciones de Despliegue
### 7.1 Contenerización
- Crear imágenes Docker optimizadas y seguras.
- Utilizar Docker Compose para desarrollo local.
- Configurar Kubernetes para producción.

### 7.2 Configuración
- Externalizar configuración según entorno.
- Usar Spring Cloud Config para configuración centralizada.
- Seguir principios de 12-factor app.

## 8. Ciclo de Desarrollo Junie
### 8.1 Proceso de Desarrollo
1. **Análisis**: Identificar contextos acotados y entidades.
2. **Diseño**: Modelar dominio con entidades, value objects y agregados.
3. **Implementación**: Seguir de adentro hacia afuera (dominio → aplicación → infraestructura).
4. **Testing**: Tests unitarios, integración y arquitectura.
5. **Despliegue**: Automatizar con CI/CD.

### 8.2 Revisión de Código
- Verificar adhesión a principios SOLID.
- Comprobar el correcto encapsulamiento del dominio.
- Validar separación de capas y dependencias unidireccionales.
- Asegurar cobertura de tests adecuada.
