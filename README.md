# ms-pedidos

Microservicio de gestión de pedidos. Permite registrar pedidos de clientes, listar, buscar, actualizar estado y eliminar pedidos mediante una API REST. El total del pedido se calcula automáticamente en el backend.

## Tecnologías utilizadas

| Tecnología       | Versión  |
|------------------|----------|
| Java             | 21       |
| Spring Boot      | 3.5.0    |
| Spring Web       | —        |
| Spring Data JPA  | —        |
| PostgreSQL Driver| —        |
| Validation (JSR-380) | —    |
| Lombok           | —        |
| Maven            | 3.9+     |
| Neon (PostgreSQL en la nube) | — |
| Docker           | —        |
| Render           | —        |

## Entidad: Pedido

| Campo          | Tipo         | Descripción                                   |
|----------------|--------------|-----------------------------------------------|
| id             | Long         | Identificador único (auto-generado)           |
| cliente        | String       | Nombre del cliente                            |
| correoCliente  | String       | Correo electrónico del cliente                |
| productoId     | Long         | ID del producto solicitado                    |
| nombreProducto | String       | Nombre del producto                           |
| cantidad       | Integer      | Cantidad solicitada                           |
| precioUnitario | BigDecimal   | Precio del producto                           |
| total          | BigDecimal   | Total calculado (cantidad × precioUnitario)   |
| estado         | String       | REGISTRADO / PAGADO / ENVIADO / CANCELADO     |
| fechaPedido    | LocalDateTime| Fecha de creación (auto-asignada)             |

## Endpoints disponibles

### POST /api/pedidos — Crear pedido
```http
POST /api/pedidos
Content-Type: application/json

{
  "cliente": "Juan Pérez",
  "correoCliente": "juan@email.com",
  "productoId": 1,
  "nombreProducto": "Laptop Lenovo",
  "cantidad": 2,
  "precioUnitario": 3500.00
}
```
> El campo `total` se calcula automáticamente: `total = cantidad × precioUnitario`

### GET /api/pedidos — Listar todos los pedidos
```http
GET /api/pedidos
```

### GET /api/pedidos/{id} — Buscar por ID
```http
GET /api/pedidos/1
```

### PATCH /api/pedidos/{id}/estado — Actualizar estado
```http
PATCH /api/pedidos/1/estado
Content-Type: application/json

{
  "estado": "PAGADO"
}
```
**Estados válidos:** `REGISTRADO` | `PAGADO` | `ENVIADO` | `CANCELADO`

### DELETE /api/pedidos/{id} — Eliminar (lógico)
```http
DELETE /api/pedidos/1
```
> La eliminación es lógica: cambia el estado a `CANCELADO`.

## Respuesta de error

```json
{
  "mensaje": "Pedido no encontrado",
  "detalle": "No existe un pedido con el ID 5",
  "fecha": "2026-05-09T10:30:00"
}
```

## Variables de entorno necesarias

| Variable      | Descripción                                 | Ejemplo                                                   |
|---------------|---------------------------------------------|-----------------------------------------------------------|
| `DB_URL`      | JDBC URL de conexión a PostgreSQL en Neon   | `jdbc:postgresql://host/neondb?sslmode=require`           |
| `DB_USERNAME` | Usuario de la base de datos                 | `neondb_owner`                                            |
| `DB_PASSWORD` | Contraseña de la base de datos              | `tu_password`                                             |
| `PORT`        | Puerto del servidor (default: 8081)         | `8080`                                                    |

## Ejecución en local

### 1. Clonar el repositorio
```bash
git clone https://github.com/Miguel-Sanchez241001/ms-pedidos.git
cd ms-pedidos
```

### 2. Configurar variables de entorno

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://ep-noisy-sea-aqnnmy17-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require"
$env:DB_USERNAME="neondb_owner"
$env:DB_PASSWORD="tu_password"
$env:PORT="8081"
```

**Linux / macOS (bash):**
```bash
export DB_URL="jdbc:postgresql://ep-noisy-sea-aqnnmy17-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require"
export DB_USERNAME="neondb_owner"
export DB_PASSWORD="tu_password"
export PORT=8081
```

### 3. Compilar y ejecutar
```bash
mvn clean package -DskipTests
java -jar target/ms-pedidos-0.0.1-SNAPSHOT.jar
```

O directamente:
```bash
mvn spring-boot:run
```

El servicio estará disponible en: `http://localhost:8081/api/pedidos`

### 4. Ejecutar con Docker (local)
```bash
docker build -t ms-pedidos .
docker run -p 8081:8080 \
  -e DB_URL="jdbc:postgresql://ep-noisy-sea-aqnnmy17-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require" \
  -e DB_USERNAME="neondb_owner" \
  -e DB_PASSWORD="tu_password" \
  ms-pedidos
```

## Despliegue en Render

### Prerrequisitos
- Cuenta en [Render](https://render.com)
- Repositorio en GitHub con el código de este proyecto
- Base de datos activa en [Neon](https://neon.tech)

### Pasos de despliegue

1. **Subir el código a GitHub:**
   ```bash
   git init
   git add .
   git commit -m "feat: ms-pedidos inicial"
   git remote add origin https://github.com/Miguel-Sanchez241001/ms-pedidos.git
   git push -u origin main
   ```

2. **Crear servicio en Render:**
   - Ir a [render.com](https://render.com) → **New** → **Web Service**
   - Conectar tu cuenta de GitHub y seleccionar el repositorio `ms-pedidos`
   - Configurar:
     - **Name:** `ms-pedidos`
     - **Language:** `Docker`
     - **Branch:** `main`
     - **Dockerfile Path:** `./Dockerfile`

3. **Configurar variables de entorno en Render:**

   En la sección **Environment** del servicio, agregar:

   | Key           | Value                                                                                  |
   |---------------|----------------------------------------------------------------------------------------|
   | `DB_URL`      | `jdbc:postgresql://ep-noisy-sea-aqnnmy17-pooler.c-8.us-east-1.aws.neon.tech/neondb?sslmode=require` |
   | `DB_USERNAME` | `neondb_owner`                                                                         |
   | `DB_PASSWORD` | *(tu contraseña de Neon)*                                                              |
   | `PORT`        | `8080`                                                                                 |

4. **Iniciar el despliegue:**
   - Hacer clic en **Create Web Service**
   - Esperar a que el build de Docker finalice (3-5 minutos la primera vez)

5. **Verificar el despliegue:**
   ```bash
   curl https://ms-pedidos.onrender.com/api/pedidos
   ```

> **Nota:** En el plan gratuito de Render, el servicio entra en "sleep" tras 15 minutos de inactividad. La primera petición puede tardar ~30 segundos en despertar.

### Uso con render.yaml (Blueprint)

Alternativamente, usa el archivo `render.yaml` incluido en el proyecto:
- Ir a Render → **New** → **Blueprint**
- Conectar el repositorio
- Render detectará el `render.yaml` y configurará el servicio automáticamente
- Solo tendrás que ingresar las variables marcadas como `sync: false` (las credenciales)

## URL del servicio desplegado

```
https://ms-pedidos.onrender.com/api/pedidos
```
*(URL de ejemplo — reemplazar con la URL real de Render tras el despliegue)*

## Configuración de Neon (base de datos)

La tabla `pedidos` se crea automáticamente al iniciar el servicio gracias a `spring.jpa.hibernate.ddl-auto=update`.

Para verificar en Neon:
1. Ir a [neon.tech](https://neon.tech) → tu proyecto
2. Abrir el **SQL Editor**
3. Ejecutar: `SELECT * FROM pedidos;`

## Estructura del proyecto

```
ms-pedidos/
├── src/main/java/com/examen/pedidos/
│   ├── MsPedidosApplication.java
│   ├── controller/
│   │   └── PedidoController.java
│   ├── service/
│   │   └── PedidoService.java
│   ├── repository/
│   │   └── PedidoRepository.java
│   ├── entity/
│   │   └── Pedido.java
│   ├── dto/
│   │   ├── PedidoRequestDTO.java
│   │   ├── PedidoResponseDTO.java
│   │   └── EstadoUpdateDTO.java
│   └── exception/
│       ├── PedidoNotFoundException.java
│       ├── GlobalExceptionHandler.java
│       └── ErrorResponse.java
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── render.yaml
├── .env.example
└── pom.xml
```
