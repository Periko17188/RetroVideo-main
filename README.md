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
│   │   │   │   ├── DataLoader.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── Seeder.java
│   │   │   │   └── WebConfig.java
│   │   │   │
│   │   │   ├── 📂 controller
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── GenreController.java
│   │   │   │   └── MovieController.java
│   │   │   │
│   │   │   ├── 📂 model
│   │   │   │   ├── Genre.java
│   │   │   │   ├── Movie.java
│   │   │   │   └── User.java
│   │   │   │
│   │   │   ├── 📂 repository
│   │   │   │   ├── GenreRepository.java
│   │   │   │   ├── MovieRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   │
│   │   │   ├── 📂 service
│   │   │   │   ├── GenreService.java
│   │   │   │   ├── JpaUserDetailsService.java
│   │   │   │   └── MovieService.java
│   │   │   │
│   │   │   └── NetflixCloneApplication.java
│   │   │
│   │   └── 📂 resources
│   │       ├── 📂 static
│   │       ├── 📂 templates
│   │       └── application.properties
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

## ⚙️ Configuración del Proyecto

El archivo `application.properties` define los parámetros principales:

```properties
spring.application.name=netflix-clone
server.port=8080

# Base de datos en memoria H2
spring.datasource.url=jdbc:h2:mem:netflixdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=Pedro
spring.datasource.password=

# Consola web de H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Configuración JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true


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

Usa la URL: jdbc:h2:mem:netflixdb


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