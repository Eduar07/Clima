# Vacaciones Clima

Aplicacion web construida con Spring Boot 3.2, Java 17 y un frontend estatico (HTML/CSS/JS) que consulta servicios externos para recomendar vestimenta segun el clima actual de una ciudad. El proyecto expone una API REST para el navegador y sirve los activos del frontend desde `src/main/resources/frontend`.

## Arquitectura en capas hexagonales

El codigo se organiza con puertos y adaptadores para aislar el dominio de los detalles de infraestructura:

```
[Usuario]
   │
   ▼
Frontend estatico (index.html + app.js)
   │ fetch POST /api/clima/consultar-n8n o /consultar
   ▼
Controladores REST (`ClimaController`)  ──► Mapper DTO (`ClimaMapper`)
   │
   ▼
Caso de uso (`ConsultarClimaUseCaseImpl`)
   ├─► Puerto `ClimaApiPort` → `OpenWeatherMapAdapter`
   └─► Puerto `ClimaRepository` → `ClimaRepositoryAdapter` → MySQL (`ConsultaClimaJpaRepository`)
                                      ▲
                                      └── `N8nWorkflowAdapter` (via endpoint proxy opcional)
```

1. El usuario escribe una ciudad en la SPA, `app.js` llama `POST /api/clima/consultar-n8n` para delegar la consulta a un workflow de n8n y evitar problemas de CORS.
2. `ClimaController` valida el `ClimaRequest`, invoca `N8nWorkflowAdapter` o el caso de uso, y transforma la respuesta a `ClimaResponse` mediante `ClimaMapper`.
3. `N8nWorkflowAdapter` hace `POST` al webhook configurado (`n8n.workflow.url`), mapea el JSON flexible de n8n y retorna el mismo DTO que espera el frontend. Tambien registra la consulta en la base local usando `ClimaRepository`.
4. En el flujo directo (`/consultar`), `ConsultarClimaUseCaseImpl` usa `OpenWeatherMapAdapter` para obtener datos crudos (`DatosClima`), calcula la recomendacion y construye la entidad de dominio `ConsultaClima`.
5. Todas las consultas exitosas se persisten mediante `ClimaRepositoryAdapter`, que convierte el modelo de dominio en `ConsultaClimaEntity` y delega en `ConsultaClimaJpaRepository` (Spring Data JPA) para hablar con MySQL.
6. El historial y las busquedas por ciudad leen desde la misma tabla para alimentar la seccion de consultas recientes del frontend.
7. `BeanConfiguration` expone manualmente el bean del caso de uso para mantener el dominio libre de anotaciones de Spring.

## Componentes principales

- **Frontend** (`frontend/index.html`, `css/styles.css`, `js/app.js`): interfaz responsiva, spinner de carga, render de resultado y consumo del historial via `GET /api/clima/historial`.
- **Adaptadores de entrada**:
  - `FrontendController`: redirige `/` al `index.html` empacado.
  - `ClimaController`: Endpoints REST `/api/clima` (consultar, historial, buscar, ping) y proxy hacia n8n.
- **Dominio y aplicacion**:
  - `ConsultarClimaUseCase` y `ConsultarClimaUseCaseImpl`: orquestan la regla de negocio para generar recomendaciones de vestimenta.
  - Modelos `DatosClima` (VO) y `ConsultaClima` (entidad) + puertos `ClimaApiPort` y `ClimaRepository`.
- **Adaptadores de salida**:
  - `OpenWeatherMapAdapter`: consume la API oficial usando `RestTemplate`, detecta lluvia por rangos de ID y maneja errores HTTP.
  - `N8nWorkflowAdapter`: cliente resiliente (timeouts configurables) hacia el workflow externo.
  - `ClimaRepositoryAdapter` + `ConsultaClimaJpaRepository` + `ConsultaClimaEntity`: persistencia en MySQL con Spring Data JPA.
- **Configuracion**: `BeanConfiguration` conecta puertos/implementaciones y `application.properties` define puertos, credenciales, claves y rutas de recursos.

## API REST expuesta

| Metodo | Ruta                               | Uso principal |
|--------|------------------------------------|---------------|
| POST   | `/api/clima/consultar`             | Consulta directa contra OpenWeatherMap, aplica reglas de negocio internas y persiste.
| POST   | `/api/clima/consultar-n8n`         | Proxy hacia n8n, útil cuando el workflow ya prepara la respuesta y se desea evitar CORS.
| POST   | `/api/clima/guardar`               | Guardado manual de una consulta (utilitario básico).
| GET    | `/api/clima/historial`             | Últimas 10 consultas (sección "Consultas recientes").
| GET    | `/api/clima/ciudad/{nombre}`       | Historial filtrado por ciudad.
| GET    | `/api/clima/ping`                  | Healthcheck simple.

Todas las respuestas usan `ClimaResponse`. Para peticiones POST se envía `{ "ciudad": "Bogota" }` via `ClimaRequest`.

## Configuracion y ejecucion local

1. **Requisitos**: Java 17, Maven 3.9+, MySQL 8+, n8n (opcional, solo si usas el workflow externo) y una cuenta en OpenWeatherMap para la API key.
2. **Clonar e instalar dependencias**:
   ```bash
   mvn clean install
   ```
3. **Base de datos**: ejecuta `database/schema.sql` en MySQL para crear `vacaciones_clima` y la tabla `consultas_clima` con datos de ejemplo.
4. **Configurar propiedades**:
   - Copia `src/main/resources/application.properties.example` si deseas un archivo base y edita:
     - `server.port` (8081 por defecto).
     - `spring.datasource.*` con tus credenciales de MySQL.
     - `openweather.api.key` y `openweather.api.url`.
     - `n8n.workflow.url` y `n8n.workflow.timeout-seconds` si usaras n8n.
   - `spring.web.resources.static-locations` ya expone el frontend empaquetado.
5. **Ejecutar la aplicacion**:
   ```bash
   mvn spring-boot:run
   ```
   Accede a `http://localhost:8081` para ver la interfaz. El frontend consume los endpoints REST bajo el mismo host (no requiere servidor separado).
6. **Verificar**: usa `curl http://localhost:8081/api/clima/ping` o consulta una ciudad desde la UI. Revisar logs SQL si activaste `spring.jpa.show-sql=true`.

## Flujo del frontend

- `consultarClima()` (en `js/app.js`) valida el input, muestra el spinner y realiza `fetch` a `/api/clima/consultar-n8n`.
- Al recibir datos, renderiza temperatura, condiciones, recomendaciones e iconos, y luego vuelve a cargar el historial.
- `cargarHistorial()` pobla la lista lateral llamando a `/api/clima/historial`.
- Los errores de validacion o red se muestran con el componente `<div id="error">`.

## Datos persistidos

La tabla `consultas_clima` almacena ciudad, pais, condiciones, humedad, viento, recomendacion, indicador de lluvia y marca de tiempo. `ClimaRepositoryAdapter` siempre convierte entre `ConsultaClima` y `ConsultaClimaEntity`, por lo que el dominio se mantiene libre de anotaciones JPA. El archivo `schema.sql` incluye inserts de prueba y consultas de verificacion.

## Extensiones posibles

- Sustituir `OpenWeatherMapAdapter` por otro proveedor implementando `ClimaApiPort`.
- Añadir nuevos consumidores (por ejemplo, colas o eventos) conectandose al caso de uso sin romper el dominio.
- Reemplazar n8n por otro orquestador actualizando solo `N8nWorkflowAdapter` y `application.properties`.

Con este README tienes un mapa del flujo completo y los puntos de extension mas relevantes del proyecto.
