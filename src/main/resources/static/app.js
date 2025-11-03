/**
 * Script para el frontend.
 * Se encarga de la comunicación con la API REST de Spring Boot (http://localhost:8080).
 */

const API_BASE_URL = 'http://localhost:8080/api/v1';

// Variable global para mantener el ID de la película que se está editando.
let currentEditingMovieId = null;

// Ejecuta las funciones iniciales y configura listeners una vez que el DOM está cargado.
document.addEventListener('DOMContentLoaded', () => {
    console.log("Frontend cargado. Intentando conectar con la API...");
    fetchMovies();
    fetchGenres();
    
    // Configuración del listener para el formulario de nueva película
    const form = document.getElementById('new-movie-form');
    if (form) {
        form.addEventListener('submit', handleFormSubmit);
    }
    
    // Listener para el botón de cancelar edición
    const cancelButton = document.getElementById('cancel-edit-btn');
    if (cancelButton) {
        cancelButton.addEventListener('click', resetForm);
    }
});

/**
 * Realiza la petición GET al endpoint de géneros para construir el formulario.
 */
function fetchGenres() {
    const url = `${API_BASE_URL}/generos`;
    
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status}`);
            }
            return response.json();
        })
        .then(genres => {
            console.log("Géneros cargados para el formulario:", genres);
            displayGenreCheckboxes(genres);
        })
        .catch(error => {
            console.error('Error al cargar la lista de géneros:', error);
            document.getElementById('genre-checkboxes').innerHTML = '<p class="error-message">Error al cargar géneros para el formulario.</p>';
        });
}

/**
 * Construye y muestra las casillas de verificación de género en el formulario.
 */
function displayGenreCheckboxes(genres) {
    const container = document.getElementById('genre-checkboxes');
    container.innerHTML = ''; 

    genres.forEach(genre => {
        const checkboxHTML = `
            <div class="checkbox-group">
                <input type="checkbox" id="genre-${genre.id}" name="genero" value="${genre.id}">
                <label for="genre-${genre.id}">${genre.nombre}</label>
            </div>
        `;
        container.innerHTML += checkboxHTML;
    });
}


/**
 * Maneja el evento de envío del formulario. Decide si es un POST (crear) o PUT (actualizar).
 */
function handleFormSubmit(event) {
    event.preventDefault(); 

    const form = event.target;
    const messageElement = document.getElementById('form-message');
    messageElement.className = '';
    messageElement.textContent = '';

    // Recolección y procesamiento de datos
    const titulo = form.titulo.value;
    const sinopsis = form.sinopsis.value;
    const anio = parseInt(form.anio.value); 
    const imagenUrl = form.imagenUrl.value;
    
    const selectedCheckboxes = document.querySelectorAll('#genre-checkboxes input[name="genero"]:checked');
    const generos = Array.from(selectedCheckboxes).map(checkbox => ({ 
        id: parseInt(checkbox.value) 
    }));

    if (generos.length === 0) {
        messageElement.textContent = 'Error: Debes seleccionar al menos un género.';
        messageElement.className = 'error-message';
        return;
    }

    const movieData = {
        titulo: titulo,
        sinopsis: sinopsis,
        anio: anio,
        imagenUrl: imagenUrl || null, 
        generos: generos
    };

    // Lógica clave: Si currentEditingMovieId NO es null, es una ACTUALIZACIÓN (PUT)
    if (currentEditingMovieId) {
        updateMovie(currentEditingMovieId, movieData, form, messageElement);
    } else {
        // Si currentEditingMovieId es null, es una CREACIÓN (POST)
        createMovie(movieData, form, messageElement);
    }
}

/**
 * Función que realiza la petición POST para crear una nueva película.
 */
function createMovie(movieData, form, messageElement) {
    const url = `${API_BASE_URL}/peliculas`;
    
    fetch(url, {
        method: 'POST', 
        headers: {
            'Content-Type': 'application/json' 
        },
        body: JSON.stringify(movieData) 
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || `Error al crear: ${response.status}`); });
        }
        return response.json();
    })
    .then(data => {
        console.log("Película guardada con ID:", data.id);
        
        messageElement.textContent = `¡Película "${data.titulo}" creada con ID: ${data.id}!`;
        messageElement.className = 'success-message'; 
        
        form.reset();
        fetchMovies();
    })
    .catch(error => {
        console.error('Fallo en la creación de la película:', error);
        messageElement.textContent = `Error: ${error.message}`;
        messageElement.className = 'error-message';
    });
}

// --------------------------------------------------------------------------------
// LÓGICA DE EDICIÓN (PUT)
// --------------------------------------------------------------------------------

/**
 * Función que realiza la petición PUT para actualizar una película existente.
 */
function updateMovie(id, movieData, form, messageElement) {
    const url = `${API_BASE_URL}/peliculas/${id}`;
    
    fetch(url, {
        method: 'PUT', // ¡Cambiamos a PUT!
        headers: {
            'Content-Type': 'application/json' 
        },
        body: JSON.stringify(movieData) 
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || `Error al actualizar: ${response.status}`); });
        }
        return response.json(); 
    })
    .then(data => {
        console.log("Película actualizada con ID:", data.id);
        
        messageElement.textContent = `¡Película "${data.titulo}" actualizada con éxito!`;
        messageElement.className = 'success-message'; 
        
        resetForm(); 
        fetchMovies();
    })
    .catch(error => {
        console.error('Fallo en la actualización de la película:', error);
        messageElement.textContent = `Error al actualizar: ${error.message}`;
        messageElement.className = 'error-message';
    });
}

/**
 * Carga los datos de una película específica en el formulario para su edición.
 */
function loadMovieForEditing(id) {
    const url = `${API_BASE_URL}/peliculas/${id}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status}`);
            }
            return response.json();
        })
        .then(movie => {
            // 1. Establecer el ID de edición global
            currentEditingMovieId = movie.id;
            
            // 2. Rellenar los campos del formulario
            const form = document.getElementById('new-movie-form');
            form.titulo.value = movie.titulo;
            form.sinopsis.value = movie.sinopsis;
            form.anio.value = movie.anio;
            form.imagenUrl.value = movie.imagenUrl || '';

            // 3. Marcar los checkboxes de géneros correctos
            document.querySelectorAll('#genre-checkboxes input[name="genero"]').forEach(cb => cb.checked = false);
            
            movie.generos.forEach(genre => {
                const checkbox = document.getElementById(`genre-${genre.id}`);
                if (checkbox) {
                    checkbox.checked = true;
                }
            });

            // 4. Actualizar el botón de envío y título del formulario
            document.querySelector('.form-container h2').textContent = `Editar Película (ID: ${movie.id})`;
            document.querySelector('#new-movie-form button[type="submit"]').textContent = 'Guardar Cambios';
            
            // 5. Mostrar el botón de cancelar edición
            document.getElementById('cancel-edit-btn').style.display = 'inline-block';

        })
        .catch(error => {
            console.error('Error al cargar la película para edición:', error);
            alert(`No se pudo cargar la película para editar. Error: ${error.message}`);
        });
}

/**
 * Resetea el formulario al modo "Crear Nueva Película".
 */
function resetForm() {
    currentEditingMovieId = null;
    const form = document.getElementById('new-movie-form');
    form.reset();
    
    // Volver a establecer el título y el botón de envío
    document.querySelector('.form-container h2').textContent = 'Añadir Nueva Película';
    document.querySelector('#new-movie-form button[type="submit"]').textContent = 'Guardar Película';
    
    // Ocultar el botón de cancelar
    document.getElementById('cancel-edit-btn').style.display = 'none';
    
    // Limpiar mensajes
    document.getElementById('form-message').textContent = '';
    document.getElementById('form-message').className = '';
}


// --------------------------------------------------------------------------------
// Lógica de DELETE
// --------------------------------------------------------------------------------

/**
 * Realiza la petición DELETE para eliminar una película por su ID.
 */
function deleteMovie(id) {
    const url = `${API_BASE_URL}/peliculas/${id}`;
    
    if (!confirm(`¿Estás seguro de que quieres eliminar la película con ID ${id}? Esta acción es irreversible.`)) {
        return; 
    }

    fetch(url, {
        method: 'DELETE', 
    })
    .then(response => {
        if (response.status === 204) { 
            console.log(`Película con ID ${id} eliminada con éxito.`);
            fetchMovies(); 
        } else {
            throw new Error(`Error HTTP: ${response.status}`);
        }
    })
    .catch(error => {
        console.error('Fallo en la eliminación de la película:', error);
        alert(`No se pudo eliminar la película. Error: ${error.message}`);
    });
}


/**
 * Realiza la petición GET al endpoint de películas.
 */
function fetchMovies() {
    const url = `${API_BASE_URL}/peliculas`;
    
    fetch(url) 
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status}`);
            }
            return response.json(); 
        }) 
        .then(data => {
            console.log("Datos recibidos y listos para mostrar:", data.length, "elementos.");
            displayMovies(data);
        })
        .catch(error => {
            console.error('Error al obtener las películas:', error);
            document.getElementById('movie-catalog').innerHTML = `<p class="error-message">Error al cargar el catálogo: ${error.message}</p>`;
        });
}

/**
 * Toma el array de objetos 'movies' y construye el HTML para mostrarlos, incluyendo la imagen y los botones de acción.
 */
function displayMovies(movies) {
    const catalog = document.getElementById('movie-catalog');
    catalog.innerHTML = ''; 

    movies.forEach(movie => {
        const genreNames = movie.generos.map(genre => genre.nombre).join(', ');

        // La URL de la imagen si existe, o un placeholder (necesitas un archivo placeholder.png)
        const imageUrl = movie.imagenUrl || 'placeholder.png'; 

        const movieCardHTML = `
            <div class="movie-card">
                <img src="${imageUrl}" 
                     alt="Portada de ${movie.titulo}" 
                     class="movie-poster" 
                     onerror="this.onerror=null; this.src='placeholder.png';">
                
                <div class="card-content">
                    <h3>${movie.titulo} (${movie.anio})</h3>
                    <p><strong>ID:</strong> ${movie.id}</p>
                    <p><strong>Sinopsis:</strong> ${movie.sinopsis ? movie.sinopsis.substring(0, 80) + '...' : 'Sin sinopsis'}</p>
                    <p class="genres"><strong>Géneros:</strong> ${genreNames}</p>
                </div>
                
                <div class="card-actions">
                    <button class="edit-btn" data-movie-id="${movie.id}">Editar</button>
                    <button class="delete-btn" data-movie-id="${movie.id}">Eliminar</button>
                </div>
            </div>
        `;

        catalog.innerHTML += movieCardHTML;
    });

    // Añadir listeners para los botones de ELIMINAR
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            const movieId = this.getAttribute('data-movie-id');
            deleteMovie(movieId);
        });
    });
    
    // Añadir listeners para los botones de EDITAR
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            const movieId = this.getAttribute('data-movie-id');
            loadMovieForEditing(movieId);
        });
    });
}