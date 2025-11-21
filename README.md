# 🎬 Netflix Clone (Spring Boot + H2 + Spring Security)

Proyecto desarrollado con **Spring Boot** que simula una aplicación tipo **Netflix**, con gestión de usuarios, películas y géneros.  
Incluye autenticación, carga inicial de datos y base de datos en memoria **H2** para desarrollo y pruebas.

---

## 🧱 Estructura del Proyecto

netflix-backend/
│
├── 📂 src
│   ├── 📂 main
│   │   ├── 📂 java/com/pedrosanchez/netflix_clone
│   │   │   ├── 📂 config
│   │   │   │   ├── DatabaseInitializer.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   │
│   │   │   ├── 📂 controller
│   │   │   │   ├── AdminController.java (*)
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CartController.java (*)
│   │   │   │   ├── GenreController.java
│   │   │   │   ├── MovieController.java
│   │   │   │   └── OrderController.java (*)
│   │   │   │
│   │   │   ├── 📂 dto (*)
│   │   │   │   ├── MovieRequestDTO.java
│   │   │   │   └── UserRegisterDTO.java
│   │   │   │
│   │   │   ├── 📂 exception (*)
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── NotFoundException.java
│   │   │   │
│   │   │   ├── 📂 model
│   │   │   │   ├── CartItem.java (*)
│   │   │   │   ├── Genre.java
│   │   │   │   ├── Movie.java
│   │   │   │   ├── Order.java (*)
│   │   │   │   └── User.java
│   │   │   │
│   │   │   ├── 📂 repository
│   │   │   │   ├── CartItemRepository.java (*)
│   │   │   │   ├── GenreRepository.java
│   │   │   │   ├── MovieRepository.java
│   │   │   │   ├── OrderRepository.java (*)
│   │   │   │   └── UserRepository.java
│   │   │   │
│   │   │   ├── 📂 service
│   │   │   │   ├── AdminService.java (*)
│   │   │   │   ├── AdminServiceImpl.java (*)
│   │   │   │   ├── BackupScheduler.java (*)
│   │   │   │   ├── CartService.java (*)
│   │   │   │   ├── GenreService.java
│   │   │   │   ├── JpaUserDetailsService.java
│   │   │   │   ├── MovieService.java
│   │   │   │   └── OrderService.java (*)
│   │   │   │
│   │   │   └── NetflixCloneApplication.java
│   │   │
│   │   └── 📂 resources
│   │       ├── 📂 static
│   │       │   ├── 📂 images (películas y placeholder)
│   │       │   └── index.html
│   │       └── application.properties

(*) = Funcionalidades añadidas al proyecto base
│   │
│   └── 📂 test/java/com/pedrosanchez/netflix_clone
│       └── NetflixCloneApplicationTests.java
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── HELP.md
├── README.md
├── .gitignore
└── .gitattributes


---

## 🎯 Funcionalidades del Proyecto

### Gestión de Usuarios y Autenticación
- ✅ Registro de nuevos usuarios con validación
- ✅ Autenticación mediante HTTP Basic
- ✅ Sistema de roles (USER / ADMIN)
- ✅ Contraseñas encriptadas con BCrypt

### Gestión de Películas
- ✅ Listar todas las películas con sus géneros
- ✅ Buscar películas por ID
- ✅ Crear, editar y eliminar películas (solo ADMIN)
- ✅ Valoraciones (rating) para cada película

### Carrito de Compras
- ✅ Añadir películas al carrito
- ✅ Ver contenido del carrito
- ✅ Eliminar películas del carrito
- ✅ Actualizar cantidades
- ✅ Verificar si una película está en el carrito

### Sistema de Pedidos
- ✅ Finalizar compra (checkout)
- ✅ Historial de pedidos del usuario
- ✅ Información detallada de cada pedido

### Funcionalidades de Administración
- ✅ Backup automático de base de datos (cada 15 minutos)
- ✅ Backup manual mediante endpoint REST
- ✅ Protección de endpoints administrativos con rol ADMIN

### Gestión de Errores
- ✅ Manejo global de excepciones
- ✅ Respuestas HTTP consistentes
- ✅ Mensajes de error personalizados

---

## ⚙️ Configuración del Proyecto

El archivo `application.properties` define los parámetros principales:

```properties
spring.application.name=netflix-clone
server.port=8080

# Base de datos H2 en archivo (persistente)
spring.datasource.url=jdbc:h2:file:./data/pedflixdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Consola web de H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Configuración JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true
```


🚀 Ejecución del Proyecto

1. Clona el repositorio o descarga el proyecto.

2. Abre una terminal en la raíz del proyecto.

3. Ejecuta el comando:

./mvnw spring-boot:run


o en Windows:

mvnw.cmd spring-boot:run


4. Accede a la aplicación en:
👉 http://localhost:8080

5. (Opcional) Accede a la consola H2 en:
👉 http://localhost:8080/h2-console

Usa la URL: jdbc:h2:file:./data/pedflixdb


👤 Autor

Pedro Sánchez
 Desarrollador Web Full Stack en formación
 Proyecto educativo con fines de aprendizaje y práctica de Spring Boot, JPA, y Seguridad.

🧩 Tecnologías Utilizadas

Java 17

Spring Boot 3.5.6

Spring Data JPA

Spring Security

H2 Database (en memoria)

Maven