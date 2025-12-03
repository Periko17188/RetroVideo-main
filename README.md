# 🎬 RetroVideo - Plataforma de Streaming Estilo Netflix

RetroVideo es una aplicación web completa desarrollada con Spring Boot que simula una plataforma de streaming tipo Netflix. Incluye gestión de usuarios, películas, géneros, carrito de compras, biblioteca personal, favoritos y panel de administración.

---

## 🧱 Estructura del Proyecto

```
RetroVideo/
│
├── 📂 src/main/java/com/pedrosanchez/netflix_clone
│   │
│   ├── 📂 config
│   │   ├── DatabaseInitializer.java     # Inicialización de datos prueba
│   │   ├── SecurityConfig.java          # Configuración de Spring Security
│   │   └── WebConfig.java               # Configuración web (CORS, etc.)
│   │
│   ├── 📂 controller                    # Endpoints REST del backend
│   │   ├── AdminController.java         # Gestión admin (usuarios, ventas)
│   │   ├── AuthController.java          # Autenticación (login, registro)
│   │   ├── BibliotecaController.java    # Biblioteca películas compradas
│   │   ├── CartController.java          # Carrito de compras
│   │   ├── FavoriteController.java      # Sistema de favoritos
│   │   ├── GenreController.java         # CRUD de géneros
│   │   ├── MovieController.java         # CRUD de películas
│   │   ├── OrderController.java         # Gestión de pedidos/compras
│   │   ├── UserController.java          # Gestión de usuarios
│   │   └── UserProfileController.java   # Perfil de usuario
│   │
│   ├── 📂 dto                           # Data Transfer Objects
│   │   ├── MovieRequestDTO.java
│   │   ├── UserRegisterDTO.java
│   │   └── [otros DTOs]
│   │
│   ├── 📂 exception                     # Manejo global de errores
│   │   ├── GlobalExceptionHandler.java
│   │   └── NotFoundException.java
│   │
│   ├── 📂 model                         # Entidades JPA
│   │   ├── CartItem.java                 # Items del carrito
│   │   ├── Genre.java                    # Géneros de películas
│   │   ├── Movie.java                    # Películas
│   │   ├── Order.java                    # Pedidos/compras
│   │   └── User.java                     # Usuarios (con favoritos)
│   │
│   ├── 📂 repository                    # Repositorios JPA
│   │   ├── CartItemRepository.java
│   │   ├── GenreRepository.java
│   │   ├── MovieRepository.java
│   │   ├── OrderRepository.java
│   │   └── UserRepository.java
│   │
│   ├── 📂 service                       # Lógica de negocio
│   │   ├── AdminService.java
│   │   ├── AdminServiceImpl.java
│   │   ├── BackupScheduler.java          # Backup automático de BD
│   │   ├── BibliotecaService.java
│   │   ├── CartService.java
│   │   ├── FavoriteService.java
│   │   ├── FavoriteServiceImpl.java
│   │   ├── GenreService.java
│   │   ├── JpaUserDetailsService.java
│   │   ├── MovieService.java
│   │   └── OrderService.java
│   │
│   └── NetflixCloneApplication.java        # Clase principal
│
├── 📂 src/main/resources
│   ├── 📂 static
│   │   ├── 📂 images                      # Imágenes de películas
│   │   ├── index.html                      # Frontend de la aplicación
│   │   ├── script.js                       # Lógica JavaScript
│   │   └── styles.css                      # Estilos CSS
│   └── application.properties              # Configuración de Spring Boot
│
├── 📂 data                                 # Base de datos H2 persistente
├── 📂 logs                                 # Logs de la aplicación
├── pom.xml                                 # Configuración de Maven
├── mvnw & mvnw.cmd                         # Maven Wrapper
└── README.md                               
```

---

## 🎯 Funcionalidades Principales

### 👤 Para Usuarios (ROLE_USER)

#### Autenticación y Perfil
- ✅ Registro e inicio de sesión con contraseñas encriptadas (BCrypt)
- ✅ Perfil de usuario editable (nombre, email, contraseña)
- ✅ Eliminación de cuenta con confirmación

#### Catálogo de Películas
- ✅ Explorar películas con portadas, descripciones y ratings
- ✅ Filtrar por género de forma dinámica
- ✅ Buscador de películas
- ✅ Sistema de favoritos (marcar/desmarcar con estrella)
- ✅ Vista de favoritos dedicada

#### Compras y Biblioteca
- ✅ Carrito de compras con dropdown visual
- ✅ Agregar/eliminar películas del carrito
- ✅ Finalizar compra (checkout)
- ✅ Mi Biblioteca - acceso a películas compradas
- ✅ Historial de pedidos

---

### 👨‍💼 Para Administradores (ROLE_ADMIN)

#### Gestión de Contenido
- ✅ Crear, editar y eliminar películas
- ✅ Crear géneros
- ✅ Subir imágenes de portadas

#### Panel de Administración
- ✅ Backup automático de base de datos (cada 15 min)
- ✅ Backup manual mediante endpoint REST

#### Seguridad
- ✅ Protección de endpoints con Spring Security
- ✅ Control de acceso basado en roles
- ✅ Validación de permisos en frontend y backend

---

## ⚙️ Configuración y Tecnologías

### Tecnologías Utilizadas
- Backend: Spring Boot 3.5.6, Spring Data JPA, Spring Security
- Base de datos: H2 Database
- Frontend: HTML5, CSS3, JavaScript (Vanilla)
- Autenticación: HTTP Basic Authentication
- Encriptación: BCrypt
- Lenguaje: Java 17
- Build Tool: Maven

### Configuración (`application.properties`)

spring.application.name=netflix-clone

spring.datasource.url=jdbc:h2:file:./data/pedflixdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.format_sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.jpa.defer-datasource-initialization=true

server.http.port=8080

server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:retrovideo.p12
server.ssl.key-store-password=12341234
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=retrovideo

---

## 🚀 Instalación y Ejecución

## Requisitos Previos
- Java 17
- Maven 3.8
- Git (Clonar repositorio)
- Navegador Web

## Pasos para Ejecutar

1. Clonar el repositorio

   - git clone https://github.com/tu-repo/retrovideo.git
   - cd retrovideo


2. Construir el proyecto
   
   - mvn clean install

3. Ejecutar la aplicación

   - mvn spring-boot:run

4. Acceder a la aplicación

   - Aplicación web: https://localhost:8443
   
   - Consola H2: https://localhost:8443/h2-console
     - JDBC URL: `jdbc:h2:file:./data/pedflixdb`
     - Usuario: `sa`
     - Contraseña: (dejar en blanco)

---

##  Usuarios de Prueba

La aplicación crea automáticamente al Admin al iniciar:

| Usuario | Contraseña | Rol | Descripción |
|---------|------------|-----|-------------|
| `Pedro` |  `1234`    |Admin|Administrador|

---

## 🎨 Características de la Interfaz

- 🎨  Diseño moderno inspirado en Netflix
- 📱  Responsive design adaptable a diferentes dispositivos
- 🌙  Tema oscuro por defecto
- ✨  Animaciones suaves y transiciones
- 🔍  Búsqueda en tiempo real
- 🛒  Carrito desplegable con vista rápida
- ⭐  Sistema de favoritos visual con iconos de estrella
- 📚  Secciones dedicadas (Biblioteca, Favoritos, Perfil)

---

## 🔐 Seguridad

- 🔒 HTTPS obligatorio - Todas las comunicaciones cifradas (TLS/SSL)
- 🔐 Certificado SSL configurado (puerto 8443)
- 🔑 Autenticación HTTP Basic para todos los endpoints
- 🛡️ Control de acceso basado en roles (RBAC)
- �  Contraseñas hasheadas con BCrypt
- ✅ Validación de entrada con Spring Validation
- 🚫 Protección CSRF deshabilitada para APIs REST
- 🌐 CORS configurado para desarrollo

---

## 📦 Endpoints REST

## OrderController:
- POST /api/v1/orders/checkout - Finalizar compra del carrito
- GET /api/v1/orders - Obtener historial de pedidos del usuario

## CartController:
- GET /api/v1/cart - Obtener contenido del carrito
- POST /api/v1/cart/add/{movieId} - Añadir ítem al carrito
- GET /api/v1/cart/contains/{movieId} - Obtiene carrito usuario
- PUT /api/v1/cart//{id}/quantity - Actualizar cantidad en el carrito
- DELETE /api/v1/cart/{id} - Eliminar ítem del carrito

## MovieController:
- GET /api/v1/peliculas - Obtener todas las películas
- GET /api/v1/peliculas/{id} - Obtener película por ID
- POST /api/v1/peliculas - Crear nueva película (Admin)
- PUT /api/v1/peliculas/{id} - Actualizar película (Admin)
- DELETE /api/v1/peliculas/{id} - Eliminar película (Admin)

## GenreController:
- GET /api/v1/generos - Obtener todos los géneros
- POST /api/v1/generos - Obtener género por ID

## FavoriteController:
- GET /api/v1/favoritos - Obtener películas favoritas
- POST /api/v1/favoritos/{movieId} - Añadir a favoritos
- GET /api/v1/favoritos/mis-favoritos - Obtener películas favoritas
- DELETE /api/v1/favoritos/{movieId} - Eliminar de favoritos

## AuthController:
- POST /api/v1/registro - Registrar nuevo usuario
- GET /api/v1/me - Obtener información del usuario actual

## UserProfileController:
- GET /api/v1/perfil/me - Obtener perfil del usuario
- PUT /api/v1/perfil/me - Actualizar perfil

## BibliotecaController:
- GET /api/v1/biblioteca - Obtener biblioteca de películas compradas

## AdminController:
- POST /api/v1/admin/backup - Generar copia de seguridad

## UserController:
- DELETE /api/v1/usuarios - Eliminar usuario (Admin)


---

## 👨‍💻 Autor

**Pedro Sánchez**  
Desarrollador Web Full Stack en formación
Proyecto educativo enfocado en Spring Boot, JPA, Spring Security.
