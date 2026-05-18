# ms-pedidos

API REST para gestión de pedidos de clientes. El campo `total` se calcula automáticamente en el backend (`total = cantidad × precioUnitario`). El estado del pedido se maneja con un enum tipado que garantiza integridad de datos.

🌐 **Producción:** https://ms-pedidos-zgv6.onrender.com

---

## Stack tecnológico

| Capa              | Tecnología                          |
|-------------------|-------------------------------------|
| Lenguaje          | Java 21                             |
| Framework         | Spring Boot 3.5.0                   |
| Persistencia      | Spring Data JPA + Hibernate 6.6     |
| Base de datos     | PostgreSQL (Neon — serverless)      |
| Validaciones      | Jakarta Bean Validation (JSR-380)   |
| Build             | Maven 3.9                           |
| Contenedor        | Docker (multi-stage build)          |
| Despliegue        | Render (Docker runtime)             |

---

## Estados del pedido — Enum `EstadoPedido`

El estado se define con un enum Java y se persiste como `VARCHAR` en la BD (`@Enumerated(EnumType.STRING)`). Esto elimina errores por cadenas inválidas.

```java
public enum EstadoPedido {
    REGISTRADO,  // estado inicial al crear el pedido
    PAGADO,      // pago confirmado
    ENVIADO,     // pedido en camino al cliente
    CANCELADO    // eliminación lógica (DELETE) o cancelación manual
}
```

> La eliminación es **lógica**: el `DELETE` cambia `estado → CANCELADO`. El registro permanece en la BD.

---

## Modelo de datos

| Campo          | Tipo          | Restricción                                      |
|----------------|---------------|--------------------------------------------------|
| id             | Long          | PK, autoincremental                              |
| cliente        | String        | Obligatorio                                      |
| correoCliente  | String        | Obligatorio, formato email válido                |
| productoId     | Long          | Obligatorio                                      |
| nombreProducto | String        | Obligatorio                                      |
| cantidad       | Integer       | Obligatorio, mínimo 1                            |
| precioUnitario | BigDecimal    | Obligatorio, mayor que 0                         |
| total          | BigDecimal    | **Calculado en backend**: `cantidad × precioUnitario` |
| estado         | EstadoPedido  | Enum: REGISTRADO / PAGADO / ENVIADO / CANCELADO  |
| fechaPedido    | LocalDateTime | Asignada automáticamente en `@PrePersist`        |

---

## Endpoints

| Método    | Ruta                         | Descripción                        | Respuesta        |
|-----------|------------------------------|------------------------------------|------------------|
| `POST`    | `/api/pedidos`               | Crear pedido (total auto-calculado)| `201 Created`    |
| `GET`     | `/api/pedidos`               | Listar todos los pedidos           | `200 OK`         |
| `GET`     | `/api/pedidos/{id}`          | Buscar pedido por ID               | `200` / `404`    |
| `PATCH`   | `/api/pedidos/{id}/estado`   | Actualizar solo el estado          | `200` / `400` / `404` |
| `DELETE`  | `/api/pedidos/{id}`          | Cancelar pedido (lógico)           | `204 No Content` |

### POST /api/pedidos
> ⚠ No incluir `total` en el body — el backend lo calcula automáticamente.

```json
{
  "cliente": "Juan Pérez",
  "correoCliente": "juan@email.com",
  "productoId": 1,
  "nombreProducto": "Laptop Lenovo",
  "cantidad": 2,
  "precioUnitario": 3500.00
}
```

### PATCH /api/pedidos/{id}/estado
Los valores deben coincidir **exactamente** con el enum (mayúsculas):

```json
{ "estado": "PAGADO" }
```

Valores válidos: `REGISTRADO` | `PAGADO` | `ENVIADO` | `CANCELADO`

### Respuesta exitosa (ejemplo POST)
```json
{
  "id": 1,
  "cliente": "Juan Pérez",
  "correoCliente": "juan@email.com",
  "productoId": 1,
  "nombreProducto": "Laptop Lenovo",
  "cantidad": 2,
  "precioUnitario": 3500.00,
  "total": 7000.00,
  "estado": "REGISTRADO",
  "fechaPedido": "2026-05-17T15:48:18.675"
}
```

### Respuesta de error
```json
{
  "mensaje": "Pedido no encontrado",
  "detalle": "No existe un pedido con el ID 5",
  "fecha": "2026-05-17T15:48:00"
}
```

---

## Ejecución local

### Prerrequisitos
- Java 21
- Maven 3.9+

### 1. Clonar
```bash
git clone https://github.com/TU_USUARIO/ms-pedidos.git
cd ms-pedidos
```

### 2. Configurar variables de entorno

**Windows (PowerShell)**
```powershell
$env:DB_URL      = "jdbc:postgresql://HOST/neondb?sslmode=require"
$env:DB_USERNAME = "tu_usuario"
$env:DB_PASSWORD = "tu_password"
$env:PORT        = "8081"
```

**Linux / macOS**
```bash
export DB_URL="jdbc:postgresql://HOST/neondb?sslmode=require"
export DB_USERNAME="tu_usuario"
export DB_PASSWORD="tu_password"
export PORT=8081
```

### 3. Ejecutar
```bash
mvn spring-boot:run
# API disponible en http://localhost:8081/api/pedidos
```

### 4. Docker (local)
```bash
docker build -t ms-pedidos .
docker run -p 8081:8080 \
  -e DB_URL="jdbc:postgresql://HOST/neondb?sslmode=require" \
  -e DB_USERNAME="tu_usuario" \
  -e DB_PASSWORD="tu_password" \
  ms-pedidos
```

---

## Despliegue en Render

### Variables de entorno requeridas

| Variable      | Descripción                      |
|---------------|----------------------------------|
| `DB_URL`      | `jdbc:postgresql://HOST/neondb?sslmode=require` |
| `DB_USERNAME` | Usuario de Neon                  |
| `DB_PASSWORD` | Contraseña de Neon               |
| `PORT`        | `8080`                           |

### Pasos
1. Ir a [render.com](https://render.com) → **New** → **Web Service**
2. Conectar el repo `ms-pedidos` desde GitHub
3. Seleccionar **Language: Docker**
4. Agregar las 4 variables de entorno
5. Clic en **Create Web Service** — build tarda ~5 min

> ⚠ En el plan gratuito, el servicio duerme tras 15 min de inactividad. La primera petición puede tardar ~30 s.

---

## Base de datos — Neon

La tabla `pedidos` se crea automáticamente con `ddl-auto=update`. La columna `estado` se almacena como `VARCHAR(20)` con los valores del enum.

Para verificar:
```sql
-- En el SQL Editor de Neon
SELECT * FROM pedidos;
SELECT id, cliente, estado, total FROM pedidos ORDER BY fecha_pedido DESC;
```

---

## Estructura del proyecto

```
ms-pedidos/
├── src/main/java/com/examen/pedidos/
│   ├── controller/   PedidoController.java
│   ├── service/      PedidoService.java
│   ├── repository/   PedidoRepository.java
│   ├── entity/       Pedido.java · EstadoPedido.java
│   ├── dto/          PedidoRequestDTO.java · PedidoResponseDTO.java · EstadoUpdateDTO.java
│   └── exception/    GlobalExceptionHandler.java · PedidoNotFoundException.java · ErrorResponse.java
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── render.yaml
└── pom.xml
```
