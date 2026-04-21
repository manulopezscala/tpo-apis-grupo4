# TPO APIs Interactivas - Ecommerce (Grupo 4)

API REST de ecommerce desarrollada con Spring Boot 3, Spring Security + JWT, JPA/Hibernate y MySQL.

## Stack tecnológico

- Java 17
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (jjwt)
- MySQL 8
- Maven Wrapper (mvnw)

## Estructura del proyecto

### Entidades principales

- Role: roles de seguridad (ADMIN, USER)
- User: usuarios de la plataforma
- Category: categorías de productos
- Product: productos publicados
- ProductImage: imágenes asociadas a productos
- Discount: descuentos asociados a productos
- Cart: carrito de compras
- CartItem: item dentro de carrito
- Order: orden de compra
- OrderItem: item dentro de orden

### Enumeraciones

- RoleName: ADMIN, USER
- CartStatus: estados del carrito
- OrderStatus: estados de la orden

### Controladores (API)

- AuthController: /auth/login
- UserController: /users
- CategoryController: /categories
- ProductController: /products
- ProductImageController: /product-images
- DiscountController: /discounts
- CartController: /carts
- CartItemController: /cart-items
- OrderController: /orders
- OrderItemController: /order-items

### Servicios

- CustomUserDetailsService: integración de usuarios con Spring Security
- AuthenticatedUserService: recuperación de usuario autenticado
- UserService
- CategoryService
- ProductService
- ProductImageService
- DiscountService
- CartService
- CartItemService
- OrderService
- OrderItemService

### Repositorios

- RoleRepository
- UserRepository
- CategoryRepository
- ProductRepository
- ProductImageRepository
- DiscountRepository
- CartRepository
- CartItemRepository
- OrderRepository
- OrderItemRepository

## Configuración

### Archivos de configuración relevantes

- src/main/resources/application.properties
- src/main/java/com/uade/tpo/ecommerce/config/SecurityConfig.java
- src/main/java/com/uade/tpo/ecommerce/auth/JwtAuthFilter.java
- src/main/java/com/uade/tpo/ecommerce/auth/JwtService.java

### Propiedades actuales (application.properties)

- server.port=4002
- spring.datasource.url=jdbc:mysql://localhost:3306/marketplace
- spring.datasource.username=root
- spring.datasource.password=root
- spring.jpa.hibernate.ddl-auto=update
- jwt.secret=<clave>
- jwt.expiration-ms=3600000

## Seguridad y autorización

- Autenticación JWT stateless.
- Endpoint público (sin token):
  - POST /auth/login
- Endpoints protegidos con token:
  - GET /products/** y GET /categories/**: ROLE_USER o ROLE_ADMIN
  - /users/**: ROLE_ADMIN
  - Operaciones de escritura sobre productos/categorías/descuentos/imágenes: ROLE_ADMIN
  - /carts/**, /cart-items/**, /orders/**, /order-items/**: ROLE_USER o ROLE_ADMIN (con restricciones por método definidas en SecurityConfig)

### Respuestas esperadas de seguridad

Si no se envía token o es inválido:

```json
{
  "success": false,
  "message": "No autorizado"
}
```

Si el usuario está autenticado pero no tiene permisos:

```json
{
  "success": false,
  "message": "No tiene permisos para realizar esta acción"
}
```

## SQL opcional (semillas)

Se incluye script en doc/Query Init.sql para:

- usar la base marketplace
- insertar roles ADMIN y USER (sin duplicar)
- insertar usuario admin semilla

Usuario semilla:

- username: admin
- password: admin123

Nota: las tablas se crean automáticamente al levantar la app (spring.jpa.hibernate.ddl-auto=update).

## Colecciones API (Postman)

### Colección funcional general

- doc/TPO Ecommerce - Grupo 4.postman_collection.json

Incluye CRUD y flujo principal del sistema.

## Pasos mínimos para ejecutar localmente

1. Instalar y levantar MySQL 8.
2. Crear base de datos vacía marketplace (si no existe).
3. Verificar credenciales en application.properties.
4. Levantar la API:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

5. (Opcional) ejecutar script de semillas:

```sql
SOURCE doc/Query Init.sql;
```

6. Confirmar que la API responde en:

- http://localhost:4002

## Cómo probar token y roles (Postman)

Usar la colección doc/TPO Ecommerce - Seguridad.postman_collection.json con este orden:

1. Auth/Publico - Login ADMIN
2. Setup - Crear USER de pruebas (si ya existe, puede devolver 409)
3. Auth/Protegido - Login USER
4. Protegidos con Token - GET /products (ADMIN) -> 200
5. Seguridad - GET /products sin token -> 401
6. Seguridad - POST /products con token USER -> 403

## Captura de fallos esperados (401/403)

### Caso 401 (sin token)

Request:

- GET /products
- Sin header Authorization

Expected:

- HTTP 401
- Body:

```json
{
  "success": false,
  "message": "No autorizado"
}
```

### Caso 403 (rol insuficiente)

Request:

- POST /products
- Header Authorization: Bearer <token_user>

Expected:

- HTTP 403
- Body:

```json
{
  "success": false,
  "message": "No tiene permisos para realizar esta acción"
}
```

## Endpoint protegido con token

Ejemplo:

- GET /products
- Header Authorization: Bearer <token_admin> o <token_user>
- Expected: 200 OK
