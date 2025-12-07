package com.pedrosanchez.netflix_clone.config;

import com.pedrosanchez.netflix_clone.model.Genre;
import com.pedrosanchez.netflix_clone.model.Movie;
import com.pedrosanchez.netflix_clone.model.User;
import com.pedrosanchez.netflix_clone.repository.GenreRepository;
import com.pedrosanchez.netflix_clone.repository.MovieRepository;
import com.pedrosanchez.netflix_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Clase que inicializa la base de datos con datos de prueba
// Primero carga el usuario admin, luego los géneros y películas
@Component
@Order(1)
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("--- INICIANDO CARGA DE DATOS INICIALES ---");

        // Crear usuario administrador
        cargarUsuarioAdmin();

        // Cargar géneros y películas (solo si no existen)
        cargarGenerosYPeliculas();

        System.out.println("--- CARGA DE DATOS FINALIZADA ---");
    }

    // Crea o actualiza el usuario administrador del sistema
    private void cargarUsuarioAdmin() {
        final String rawPassword = "12341234";
        final String encodedPassword = passwordEncoder.encode(rawPassword);

        userRepository.findByUsername("Pedro").ifPresentOrElse(user -> {
            // Actualizar contraseña
            user.setPassword(encodedPassword);

            // Actualizar roles
            user.getRoles().clear();
            user.getRoles().add("ROLE_ADMIN");

            userRepository.save(user);
            System.out.println("Usuario admin actualizado: Pedro");

        }, () -> {
            // Crear nuevo administrador
            User admin = new User("Pedro", encodedPassword);
            admin.getRoles().add("ROLE_ADMIN");

            userRepository.save(admin);
            System.out.println("Usuario admin creado: Pedro");
        });
    }

    // Carga los géneros y películas en la base de datos
    // Solo se ejecuta si no hay datos previamente cargados
    private void cargarGenerosYPeliculas() {

        // Si ya hay datos, no los volvemos a cargar
        if (movieRepository.count() > 0 && genreRepository.count() > 0) {
            System.out.println("Géneros y películas ya existentes, omitiendo carga inicial.");
            return;
        }

        System.out.println("Cargando géneros...");

        // CREAR Y GUARDAR GÉNEROS
        Genre accion = new Genre("Acción");
        Genre aventura = new Genre("Aventura");
        Genre comedia = new Genre("Comedia");
        Genre drama = new Genre("Drama");
        Genre terror = new Genre("Terror");
        Genre cienciaFiccion = new Genre("Ciencia Ficción");
        Genre fantasia = new Genre("Fantasía");
        Genre suspense = new Genre("Suspense / Thriller");
        Genre romance = new Genre("Romance");
        Genre musical = new Genre("Musical");
        Genre belica = new Genre("Bélica");
        Genre western = new Genre("Western");
        Genre animacion = new Genre("Animación");
        Genre documental = new Genre("Documental");
        Genre crimen = new Genre("Crimen / Policíaca");
        Genre misterio = new Genre("Misterio");

        // Guarda todos los géneros en la base de datos
        genreRepository.saveAll(Arrays.asList(
                accion, aventura, comedia, drama, terror,
                cienciaFiccion, fantasia, suspense, romance,
                musical, belica, western, animacion, documental,
                crimen, misterio));

        System.out.println("Géneros guardados correctamente.");
        System.out.println("Cargando películas...");

        // CREAR PELÍCULAS CON TODOS SUS DATOS
        Movie interstellar = new Movie(
                "Interstellar",
                "Un equipo de exploradores viaja a través de un agujero de gusano en busca de un nuevo hogar para la humanidad.",
                2014,
                "interstellar.jpg",
                8.7);
        interstellar.setGeneros(new HashSet<>(Set.of(drama, cienciaFiccion)));

        Movie deadpool = new Movie(
                "Deadpool",
                "Un exoperativo de las fuerzas especiales convertido en mercenario busca venganza tras un experimento que lo desfigura.",
                2016,
                "deadpool.jpg",
                8.0);
        deadpool.setGeneros(new HashSet<>(Set.of(accion, comedia)));

        Movie elSenorDeLosAnillos = new Movie(
                "El Señor de los Anillos: La Comunidad del Anillo",
                "Un hobbit emprende un viaje para destruir un anillo que puede condenar al mundo.",
                2001,
                "el_senor_de_los_anillos.jpg",
                9.0);
        elSenorDeLosAnillos.setGeneros(new HashSet<>(Set.of(fantasia, aventura)));

        Movie elConjuro = new Movie(
                "El Conjuro",
                "Basada en hechos reales, una familia experimenta sucesos paranormales en su casa.",
                2013,
                "el_conjuro.jpg",
                7.5);
        elConjuro.setGeneros(new HashSet<>(Set.of(terror, misterio)));

        Movie laLaLand = new Movie(
                "La La Land",
                "Una aspirante a actriz y un músico de jazz luchan por su amor mientras persiguen sus sueños en Los Ángeles.",
                2016,
                "la_la_land.jpg",
                8.0);
        laLaLand.setGeneros(new HashSet<>(Set.of(romance, drama, musical)));

        Movie toyStory = new Movie(
                "Toy Story",
                "Los juguetes cobran vida cuando los humanos no miran, y viven divertidas aventuras.",
                1995,
                "toy_story.jpg",
                8.3);
        toyStory.setGeneros(new HashSet<>(Set.of(animacion, aventura, comedia)));

        Movie salvarAlSoldadoRyan = new Movie(
                "Salvar al soldado Ryan",
                "Un grupo de soldados debe rescatar a un compañero perdido tras el Día D.",
                1998,
                "salvar_al_soldado_ryan.jpg",
                8.6);
        salvarAlSoldadoRyan.setGeneros(new HashSet<>(Set.of(belica, drama)));

        Movie elPadrino = new Movie(
                "El Padrino",
                "La historia de la familia Corleone y su imperio criminal en Estados Unidos.",
                1972,
                "el_padrino.jpg",
                9.2);
        elPadrino.setGeneros(new HashSet<>(Set.of(crimen, drama)));

        Movie matrix = new Movie(
                "Matrix",
                "Un hacker descubre la verdad sobre la realidad y lidera una rebelión contra las máquinas.",
                1999,
                "matrix.jpg",
                8.7);
        matrix.setGeneros(new HashSet<>(Set.of(cienciaFiccion, accion)));

        Movie grease = new Movie(
                "Grease",
                "Un amor de verano entre dos jóvenes se pone a prueba en el instituto.",
                1978,
                "grease.jpg",
                7.2);
        grease.setGeneros(new HashSet<>(Set.of(musical, romance)));

        Movie planetaTierra = new Movie(
                "Planeta Tierra",
                "Un documental impresionante sobre la vida salvaje y los paisajes del planeta.",
                2006,
                "planeta_tierra.jpg",
                9.3);
        planetaTierra.setGeneros(new HashSet<>(Set.of(documental)));

        Movie django = new Movie(
                "Django Desencadenado",
                "Un esclavo liberado se convierte en cazarrecompensas para rescatar a su esposa.",
                2012,
                "django.jpg",
                8.5);
        django.setGeneros(new HashSet<>(Set.of(western, accion, drama)));

        Movie nottingHill = new Movie(
                "Notting Hill",
                "Un librero inglés se enamora de una famosa actriz estadounidense.",
                1999,
                "notting_hill.jpg",
                7.2);
        nottingHill.setGeneros(new HashSet<>(Set.of(comedia, romance)));

        Movie seven = new Movie(
                "Seven",
                "Dos detectives investigan una serie de asesinatos inspirados en los siete pecados capitales.",
                1995,
                "seven.jpg",
                8.6);
        seven.setGeneros(new HashSet<>(Set.of(misterio, suspense, crimen)));

        Movie elViajeDeChihiro = new Movie(
                "El Viaje de Chihiro",
                "Una niña entra en un mundo mágico lleno de espíritus, donde debe encontrar la forma de salvar a sus padres.",
                2001,
                "el_viaje_de_chihiro.jpg",
                8.6);
        elViajeDeChihiro.setGeneros(new HashSet<>(Set.of(fantasia, animacion, aventura)));

        // Guarda todas las películas en la base de datos
        movieRepository.saveAll(Arrays.asList(
                interstellar, deadpool, elSenorDeLosAnillos, elConjuro, laLaLand,
                toyStory, salvarAlSoldadoRyan, elPadrino, matrix, grease,
                planetaTierra, django, nottingHill, seven, elViajeDeChihiro));

        System.out.println("Películas guardadas correctamente.");
    }
}
