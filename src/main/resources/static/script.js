const API_URL = `${window.location.origin}/api/v1`;

// Estado global
let movies = [];
let genres = [];
let isAuthenticated = false;
let authUsername = null;
let authPassword = null;
let currentEditId = null;

// Carrito (solo para USER)
let cart = []; // {id, titulo}

// Favoritos (solo para USER)
let userFavorites = []; // Array de IDs de películas favoritas

// --- Modales ---
function openModal(id) {
  const el = document.getElementById(id);
  el.classList.remove('hidden'); el.classList.add('flex');
  if (id === 'login-modal') document.getElementById('login-message').classList.add('hidden');
  if (id === 'register-modal') document.getElementById('register-message').classList.add('hidden');
}
function closeModal(id) {
  const el = document.getElementById(id);
  el.classList.add('hidden'); el.classList.remove('flex');
  if (id === 'movie-form-section') resetMovieForm();
}

// --- UI según sesión ---
function updateUI(user, logged = false, roles = []) {
  const openLogin = document.getElementById('open-login-btn');
  const openRegister = document.getElementById('open-register-btn');
  const logoutBtn = document.getElementById('logout-btn');
  const userDisplay = document.getElementById('user-display');
  const movieFormSection = document.getElementById('movie-form-section');
  const splash = document.getElementById('splash-screen');
  const main = document.getElementById('main-content');
  const cartContainer = document.getElementById('cart-container');

  isAuthenticated = logged;
  authUsername = user || null;
  window.userRoles = roles || [];

  if (logged) {

    const isAdmin = roles.includes("ROLE_ADMIN");

    // Mostrar menú para USER
    if (!isAdmin) {
      document.getElementById("user-menu-container")?.classList.remove("hidden");
    } else {
      document.getElementById("user-menu-container")?.classList.add("hidden");
    }

    // Mostrar buscador
    document.getElementById("search-bar")?.classList.remove("hidden");

    // Limpiar el buscador al iniciar sesión
    const searchInput = document.getElementById("search-input");
    if (searchInput) searchInput.value = "";

    // Carrito SOLO para USER
    document.getElementById("cart-container")?.classList.toggle("hidden", isAdmin);

    splash.classList.add('hidden');
    main.classList.remove('hidden');
    openLogin.classList.add('hidden');
    openRegister.classList.add('hidden');
    logoutBtn.classList.remove('hidden');
    logoutBtn.classList.toggle('hidden', !isAdmin);

    userDisplay.textContent = `Bienvenid@, ${user}`;
    userDisplay.classList.remove('hidden');

    // ADMIN: mostrar panel admin
    movieFormSection?.classList.toggle('hidden', !isAdmin);
    document.getElementById('admin-controls')?.classList.toggle('hidden', !isAdmin);

  } else {

    // Ocultar menú user
    document.getElementById("user-menu-container")?.classList.add("hidden");

    // Ocultar buscador
    document.getElementById("search-bar")?.classList.add("hidden");

    splash.classList.remove('hidden');
    main.classList.add('hidden');
    openLogin.classList.remove('hidden');
    openRegister.classList.remove('hidden');
    logoutBtn.classList.add('hidden');
    userDisplay.classList.add('hidden');
    movieFormSection?.classList.add('hidden');

    // Ocultar carrito siempre si no hay sesión
    document.getElementById('cart-container')?.classList.add('hidden');
  }

}

function handleLogout() {
  authUsername = null; authPassword = null;
  cart = []; updateCartUI();
  userFavorites = []; // Limpiar favoritos
  updateUI(null, false);
  showCustomMessage('Has cerrado sesión correctamente.', 'success');
}

async function deleteUserAccount() {
  if (!authUsername || !authPassword) {
    showCustomMessage("Debes iniciar sesión.", "error");
    return;
  }

  const confirmDelete = confirm("¿Seguro que deseas eliminar tu cuenta? Esta acción es irreversible.");
  if (!confirmDelete) return;

  try {
    const base64 = btoa(`${authUsername}:${authPassword}`);

    const res = await fetch(`${API_URL}/usuarios/me`, {
      method: "DELETE",
      headers: {
        "Authorization": `Basic ${base64}`
      }
    });

    if (res.ok) {
      showCustomMessage("Cuenta eliminada correctamente", "success");

      // Cerrar sesión por completo
      authUsername = null;
      authPassword = null;
      window.userRoles = [];
      cart = [];
      updateCartUI();
      updateUI(null, false);

    } else {
      showCustomMessage("Error al eliminar la cuenta.", "error");
    }

  } catch (e) {
    showCustomMessage("Error de conexión.", "error");
  }
}

// --- Login / Registro ---
async function handleLogin() {
  const username = document.getElementById('login-username').value;
  const password = document.getElementById('login-password').value;
  const msg = document.getElementById('login-message');
  msg.classList.add('hidden');

  const base64 = btoa(`${username}:${password}`);

  try {
    // 1. Verificar credenciales con un endpoint público
    const res = await fetch(`${API_URL}/peliculas`, {
      headers: { 'Authorization': `Basic ${base64}` }
    });

    if (!res.ok) {
      msg.textContent = res.status === 401
        ? 'Usuario o contraseña incorrectos.'
        : 'Error desconocido al iniciar sesión.';
      msg.classList.remove('hidden');
      return;
    }

    // 2. Guardamos credenciales
    authUsername = username;
    authPassword = password;

    // 3. Obtener roles reales desde el backend
    const meRes = await fetch(`${API_URL}/me`, {
      headers: { 'Authorization': `Basic ${base64}` }
    });

    if (!meRes.ok) {
      msg.textContent = 'No se pudo obtener tu información.';
      msg.classList.remove('hidden');
      return;
    }

    const me = await meRes.json();
    const roles = me.roles.map(r => r.authority);

    // Guardar roles globalmente
    window.userRoles = roles;

    // 4. Actualizar UI con roles reales
    updateUI(username, true, roles);

    closeModal('login-modal');
    await fetchGenres();
    await fetchMovies();

    // 5. Cargar favoritos solo para usuarios (no admin)
    if (!roles.includes('ROLE_ADMIN')) {
      await loadFavorites();
    }

    showCustomMessage(`¡Inicio de sesión exitoso! Bienvenid@, ${username}.`, 'success');

  } catch (err) {
    msg.textContent = 'Error de conexión con el servidor.';
    msg.classList.remove('hidden');
  }
}

async function handleRegister() {
  const username = document.getElementById('register-username').value;
  const password = document.getElementById('register-password').value;
  const msg = document.getElementById('register-message');
  msg.classList.add('hidden'); msg.classList.remove('text-red-400', 'text-green-400');

  if (username.length < 3 || password.length < 8) {
    msg.textContent = 'El usuario debe tener al menos 3 caracteres y la contraseña al menos 8.';
    msg.classList.remove('hidden'); msg.classList.add('text-red-400'); return;
  }
  try {
    const res = await fetch(`${API_URL}/registro`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    if (res.status === 201) {
      msg.textContent = `¡Registro exitoso! Ya puedes iniciar sesión con ${username}.`;
      msg.classList.remove('hidden'); msg.classList.add('text-green-400');
      document.getElementById('register-username').value = '';
      document.getElementById('register-password').value = '';
    } else if (res.status === 400) {
      msg.textContent = 'El nombre de usuario ya está en uso.';
      msg.classList.remove('hidden'); msg.classList.add('text-red-400');
    } else {
      msg.textContent = `Error ${res.status} al registrar.`; msg.classList.remove('hidden'); msg.classList.add('text-red-400');
    }
  } catch (e) {
    msg.textContent = 'Error de conexión con el servidor.'; msg.classList.remove('hidden'); msg.classList.add('text-red-400');
  }
}

// --- Form Admin ---
function resetMovieForm() {
  const form = document.getElementById('movie-form');
  form.reset();
  currentEditId = null;
  document.getElementById('form-message').classList.add('hidden');
  document.getElementById('cancel-edit-btn').classList.add('hidden');
  document.getElementById('image-preview').classList.add('hidden');
  document.getElementById('selected-file-name').textContent = '';
  document.getElementById('imagenUrl').value = '';
  form.querySelector('button[type="submit"]').textContent = 'Guardar Película';
}

async function handleMovieFormSubmit(e) {
  e.preventDefault();

  const form = e.target;
  const formData = new FormData(form);
  const fileInput = document.getElementById('imagenFile');
  const genreSelect = document.getElementById('genreId');

  // Validar que se haya seleccionado un género
  if (!genreSelect.value) {
    showCustomMessage('Debes seleccionar al menos un género', 'error');
    return;
  }

  // Crear objeto con los datos del formulario
  const movieData = {
    titulo: form.titulo.value,
    sinopsis: form.sinopsis.value,
    anio: parseInt(form.anio.value),
    rating: parseFloat(form.rating.value),
    imagenUrl: form.imagenUrl.value,
    genreIds: [parseInt(genreSelect.value)]
  };

  // Si hay un archivo seleccionado, lo añadimos al FormData
  if (fileInput.files[0]) {
    const fileName = fileInput.files[0].name;
    movieData.imagenUrl = fileName;
    formData.append('file', fileInput.files[0]);
  }
  const msg = document.getElementById('form-message');
  msg.classList.add('hidden'); msg.classList.remove('text-green-400', 'text-red-400');

  if (!isAuthenticated || !authUsername || !authPassword) {
    msg.textContent = 'Debes iniciar sesión como ADMIN.'; msg.classList.remove('hidden'); msg.classList.add('text-red-400'); return;
  }

  const base64 = btoa(`${authUsername}:${authPassword}`);
  const headers = { 'Content-Type': 'application/json', 'Authorization': `Basic ${base64}` };

  let url = `${API_URL}/peliculas`; let method = 'POST';
  if (currentEditId) { url = `${API_URL}/peliculas/${currentEditId}`; method = 'PUT'; }

  try {
    const res = await fetch(url, { method, headers, body: JSON.stringify(movieData), credentials: 'include' });
    const expected = method === 'POST' ? 201 : 200;
    if (res.status === expected) {
      msg.textContent = `Película "${movieData.titulo}" ${method === 'POST' ? 'añadida' : 'actualizada'} correctamente.`;
      msg.classList.remove('hidden'); msg.classList.add('text-green-400');
      resetMovieForm(); await fetchMovies();
    } else if (res.status === 401 || res.status === 403) {
      msg.textContent = 'No tienes permisos de ADMIN.'; msg.classList.remove('hidden'); msg.classList.add('text-red-400');
    } else {
      const t = await res.text();
      msg.textContent = `Error ${res.status}: ${t.substring(0, 100)}...`; msg.classList.remove('hidden'); msg.classList.add('text-red-400');
    }
  } catch (e) {
    msg.textContent = 'Error de conexión con el servidor.'; msg.classList.remove('hidden'); msg.classList.add('text-red-400');
  }
}

// --- Mensajes superiores (confirmaciones) ---
function showCustomMessage(text, type = 'info') {
  let box = document.getElementById('custom-message-box');
  if (!box) {
    box = document.createElement('div');
    box.id = 'custom-message-box';
    box.className = 'fixed top-0 left-1/2 transform -translate-x-1/2 mt-4 p-4 rounded-lg shadow-xl text-center transition-opacity duration-500 opacity-0 z-50';
    document.body.appendChild(box);
  }
  box.textContent = text;
  box.className = 'fixed top-0 left-1/2 transform -translate-x-1/2 mt-4 p-4 rounded-lg shadow-xl text-center transition-opacity duration-500 z-50 ' + (type === 'success' ? 'bg-green-600 text-white' : 'bg-yellow-500 text-white');
  box.style.opacity = '1';
  setTimeout(() => { box.style.opacity = '0'; }, 3000);
}

// --- Películas y Géneros ---
function createMovieCard(movie) {
  const card = document.createElement('div');
  card.className = 'movie-card bg-gray-900 rounded-lg overflow-hidden relative';
  card.dataset.movieId = movie.id;
  card.style.cursor = "pointer";
  const imageFileName = movie.imagenUrl;
  const imageUrl = imageFileName ? `images/${imageFileName}` : null;
  const placeholder = 'images/placeholder.png';
  const genreName = (movie.generos && movie.generos.length > 0) ? movie.generos[0].nombre : 'Desconocido';

  card.innerHTML = `
    <img src="${imageUrl ? imageUrl : placeholder}"
         alt="${movie.titulo}"
         onerror="this.onerror=null; this.src='${placeholder}';"
         class="movie-card-img w-full h-auto object-cover rounded-t-lg aspect-[2/3]">

    <div class="p-4">
      <h4 class="text-lg font-bold truncate">${movie.titulo}</h4>
      <p class="text-sm text-gray-400">${movie.anio} | ${genreName}</p>
      <p class="text-sm text-yellow-500 mt-1">⭐ ${movie.rating != null ? movie.rating.toFixed(1) : 'N/A'} / 10</p>

      <div class="mt-3 flex items-center justify-between">
        <button
            class="buy-btn bg-red-600 hover:bg-red-700 text-white text-sm font-semibold py-1 px-3 rounded transition hidden"
            data-movie-id="${movie.id}">
          Comprar Película
        </button>
        <div class="admin-buttons space-x-2 hidden"></div>
      </div>
    </div>
  `;

  // Botones admin (Pedro)
  if (window.userRoles && window.userRoles.includes('ROLE_ADMIN')) {
    const adminDiv = card.querySelector('.admin-buttons');
    adminDiv.classList.remove('hidden');

    const editBtn = document.createElement('button');
    editBtn.innerHTML = '✏️';
    editBtn.className = 'bg-blue-600 hover:bg-blue-700 text-white text-xs p-1 rounded';
    editBtn.title = 'Editar';
    editBtn.onclick = (e) => { e.stopPropagation(); populateEditForm(movie); };
    adminDiv.appendChild(editBtn);

    const delBtn = document.createElement('button');
    delBtn.innerHTML = '🗑️';
    delBtn.className = 'bg-red-600 hover:bg-red-700 text-white text-xs p-1 rounded';
    delBtn.title = 'Eliminar';
    delBtn.onclick = (e) => { e.stopPropagation(); handleDeleteMovie(movie.id, movie.titulo); };
    adminDiv.appendChild(delBtn);
  }

  // Botón comprar (solo USER)
  if (isAuthenticated && authUsername && authUsername !== 'Pedro') {
    const buyBtn = card.querySelector('.buy-btn');
    buyBtn.classList.remove('hidden');

    buyBtn.onclick = (e) => {
      e.stopPropagation();
      handleBuy(movie, buyBtn);
    };
  }

  // Estrella de favoritos
  if (isAuthenticated && window.userRoles && !window.userRoles.includes('ROLE_ADMIN')) {
    const isFavorite = userFavorites.includes(movie.id);
    const star = document.createElement('button');
    star.className = 'favorite-star absolute bottom-2 right-2 text-xl sm:text-2xl md:text-3xl hover:scale-110 transition-transform';
    star.textContent = isFavorite ? '★' : '☆';
    star.style.color = '#FFD700'; // amarillo
    star.style.background = 'none';
    star.style.border = 'none';
    star.style.cursor = 'pointer';
    star.style.filter = 'drop-shadow(0 0 2px rgba(0, 0, 0, 0.8))';
    star.style.zIndex = '10';
    star.dataset.movieId = movie.id;

    star.onclick = async (e) => {
      e.stopPropagation(); // evitar abrir modal de detalles
      await toggleFavorite(movie.id, star);
    };

    card.appendChild(star);
  }

  return card;
}

function renderMovies(list) {
  const container = document.getElementById('movies-container');
  const message = document.getElementById('no-movies-message');
  container.innerHTML = '';
  if (!Array.isArray(list)) { message.textContent = "Error al mostrar las películas."; message.classList.remove('hidden'); return; }
  if (list.length === 0) { message.textContent = "No hay películas que coincidan con el filtro actual."; message.classList.remove('hidden'); }
  else {
    message.classList.add('hidden');
    list.forEach(m => m && m.titulo ? container.appendChild(createMovieCard(m)) : null);
  }
}

function filterAndRenderMovies(genreId) {
  if (!Array.isArray(movies)) movies = [];
  let filtered = movies;
  if (genreId !== 'all') {
    filtered = movies.filter(m => m.generos && m.generos.some(g => g.id === parseInt(genreId)));
  }
  document.querySelectorAll('.genre-btn').forEach(btn => btn.classList.replace('bg-red-600', 'bg-gray-700'));
  const active = document.querySelector(`.genre-btn[data-genre-id="${genreId}"]`);
  if (active) active.classList.replace('bg-gray-700', 'bg-red-600');
  renderMovies(filtered);
}

function createGenreButton(genre) {
  const b = document.createElement('button');
  b.textContent = genre.nombre;
  b.className = 'genre-btn bg-gray-700 hover:bg-red-600 text-white font-semibold py-2 px-4 rounded-full transition';
  b.dataset.genreId = genre.id;
  b.onclick = () => filterAndRenderMovies(b.dataset.genreId);
  return b;
}

function handleFetchError(error, type) {
  console.error(`Error al cargar ${type}:`, error);
  let containerId, formMessageId, errorMessage;
  if (type === 'géneros') { containerId = 'genres-container'; formMessageId = 'genre-load-message'; errorMessage = 'Error: No se pudo conectar a la API para cargar los géneros.'; }
  else { containerId = 'movies-container'; formMessageId = 'no-movies-message'; errorMessage = 'Error al conectar con el servidor de películas.'; }

  const container = document.getElementById(containerId);
  if (container) { container.innerHTML = `<span class="text-red-400">${errorMessage} Asegúrate de que el backend esté ejecutándose en http://localhost:8080.</span>`; }
  const formMsg = document.getElementById(formMessageId);
  if (formMsg) {
    formMsg.textContent = type === 'géneros' ? 'Error: No se pudieron cargar los géneros para el formulario.' : errorMessage;
    formMsg.classList.remove('hidden'); formMsg.classList.add('text-red-400');
  }
  if (type === 'géneros') genres = []; if (type === 'películas') movies = [];
  renderMovies([]);
}

async function addGenre() {
  const newGenreInput = document.getElementById('new-genre');
  const genreName = newGenreInput.value.trim();
  const messageElement = document.getElementById('genre-message');

  // Reset message
  messageElement.textContent = '';
  messageElement.className = 'text-sm mt-1 hidden';

  // Validation
  if (!genreName) {
    messageElement.textContent = 'El nombre del género no puede estar vacío';
    messageElement.classList.remove('hidden', 'text-green-400');
    messageElement.classList.add('text-red-400');
    return;
  }

  if (genreName.length > 25) {
    messageElement.textContent = 'El nombre del género no puede tener más de 25 caracteres';
    messageElement.classList.remove('hidden', 'text-green-400');
    messageElement.classList.add('text-red-400');
    return;
  }

  // Check if genre already exists
  const genreExists = genres.some(g => g.nombre.toLowerCase() === genreName.toLowerCase());
  if (genreExists) {
    messageElement.textContent = 'Este género ya existe';
    messageElement.classList.remove('hidden', 'text-green-400');
    messageElement.classList.add('text-yellow-400');
    return;
  }

  try {
    const headers = { 'Content-Type': 'application/json' };
    if (isAuthenticated && authUsername && authPassword) {
      const base64 = btoa(`${authUsername}:${authPassword}`);
      headers['Authorization'] = `Basic ${base64}`;
    }

    const response = await fetch(`${API_URL}/generos`, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify({ nombre: genreName })
    });

    if (!response.ok) {
      throw new Error(`Error ${response.status}: ${response.statusText}`);
    }

    const newGenre = await response.json();

    // Add to local genres array
    genres.push(newGenre);

    // Update genre select dropdown
    const select = document.querySelector('#genre-select-container select');
    if (select) {
      const option = document.createElement('option');
      option.value = newGenre.id;
      option.textContent = newGenre.nombre;
      select.appendChild(option);
      select.value = newGenre.id; // Select the newly added genre
    }

    // Update genres filter buttons
    const genresContainer = document.getElementById('genres-container');
    if (genresContainer) {
      genresContainer.appendChild(createGenreButton(newGenre));
    }

    // Show success message
    messageElement.textContent = `Género "${genreName}" añadido correctamente`;
    messageElement.classList.remove('hidden', 'text-red-400', 'text-yellow-400');
    messageElement.classList.add('text-green-400');

    // Clear input
    newGenreInput.value = '';

  } catch (error) {
    console.error('Error adding genre:', error);
    messageElement.textContent = 'Error al añadir el género. Por favor, inténtalo de nuevo.';
    messageElement.classList.remove('hidden', 'text-green-400');
    messageElement.classList.add('text-red-400');
  }
}

async function fetchGenres() {
  try {
    let headers = {};
    if (isAuthenticated && authUsername && authPassword) {
      const base64 = btoa(`${authUsername}:${authPassword}`); headers = { 'Authorization': `Basic ${base64}` };
    }
    const res = await fetch(`${API_URL}/generos`, { headers });
    if (!res.ok) throw new Error(`Error ${res.status}`);
    genres = await res.json();

    const cont = document.getElementById('genres-container');
    if (cont) {
      cont.innerHTML = '';
      const allBtn = document.createElement('button');
      allBtn.textContent = 'Todos';
      allBtn.className = 'genre-btn bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded-full transition';
      allBtn.dataset.genreId = 'all';
      allBtn.onclick = () => filterAndRenderMovies('all');
      cont.appendChild(allBtn);

      if (Array.isArray(genres)) {
        genres.forEach(g => { if (g && g.nombre) cont.appendChild(createGenreButton(g)); });
      } else { genres = []; }
    }

    const selectContainer = document.getElementById('genre-select-container');
    const selectMsg = document.getElementById('genre-load-message');
    if (selectContainer) {
      let html = `<select id="genreId" name="genreId" required class="w-full p-2 rounded bg-gray-700 border border-gray-600 text-white">`;
      if (Array.isArray(genres)) { genres.forEach(g => { if (g && g.nombre) html += `<option value="${g.id}">${g.nombre}</option>`; }); }
      html += '</select>';
      selectContainer.innerHTML = html;
      if (selectMsg) selectMsg.classList.add('hidden');
    }
  } catch (e) {
    console.error('Error fetching genres:', e);
    handleFetchError(e, 'géneros');
  }
}

async function fetchMovies() {
  try {
    let headers = {};
    if (isAuthenticated && authUsername && authPassword) {
      const base64 = btoa(`${authUsername}:${authPassword}`); headers = { 'Authorization': `Basic ${base64}` };
    }
    const res = await fetch(`${API_URL}/peliculas`, { headers });
    if (!res.ok) throw new Error(`Error ${res.status}`);
    movies = await res.json();
    if (!Array.isArray(movies)) movies = [];
    filterAndRenderMovies('all');
  } catch (e) { handleFetchError(e, 'películas'); }
}

// --- Admin: editar / borrar ---
function populateEditForm(movie) {
  const form = document.getElementById('movie-form');
  form.titulo.value = movie.titulo; form.anio.value = movie.anio; form.sinopsis.value = movie.sinopsis; form.rating.value = movie.rating;
  form.imagenUrl.value = movie.imagenUrl || '';
  form.genreId.value = movie.generos && movie.generos.length > 0 ? movie.generos[0].id : '';
  currentEditId = movie.id;
  document.getElementById('cancel-edit-btn').classList.remove('hidden');
  form.querySelector('button[type="submit"]').textContent = 'Actualizar Película';
  document.getElementById('movie-form-section').scrollIntoView({ behavior: 'smooth' });
}

async function handleDeleteMovie(id, title) {
  if (!confirm(`¿Eliminar "${title}"?`)) return;
  if (!isAuthenticated || !authUsername || !authPassword) { showCustomMessage('Debes iniciar sesión como ADMIN.', 'error'); return; }
  const base64 = btoa(`${authUsername}:${authPassword}`);
  try {
    const res = await fetch(`${API_URL}/peliculas/${id}`, {
      method: 'DELETE', headers:
        { 'Authorization': `Basic ${base64}` }, credentials: 'include'
    });
    if (res.status === 204) { showCustomMessage(`"${title}" eliminada.`, 'success'); await fetchMovies(); }
    else if (res.status === 401 || res.status === 403) { showCustomMessage('No tienes permisos de ADMIN.', 'error'); }
    else { showCustomMessage(`Error ${res.status} al eliminar.`, 'error'); }
  } catch (e) { showCustomMessage('Error de conexión.', 'error'); }
}

// ========== CARRITO ==========
// Añadir película al carrito y actualizar botón
async function handleBuy(movie, btn) {
  const base64 = btoa(`${authUsername}:${authPassword}`);

  const res = await fetch(`${API_URL}/cart/add/${movie.id}`, {
    method: "POST",
    headers: { "Authorization": `Basic ${base64}` }
  });

  if (res.ok) {
    btn.textContent = "Añadida ✅";
    btn.disabled = true;
    await loadCartFromBackend();
    showCustomMessage("Película añadida al carrito", "success");
  } else {
    showCustomMessage("Error al añadir al carrito", "error");
  }
}

// Actualiza badge, listado desplegable y visibilidad
function updateCartUI() {
  const cartItemsList = document.getElementById('cart-items-list');
  const cartTotal = document.getElementById('cart-total');
  const cartBadge = document.getElementById('cart-badge');

  // Actualizar contador del carrito
  const itemCount = cart.reduce((total, item) => total + (item.quantity || 1), 0);
  cartBadge.textContent = itemCount;
  cartBadge.style.display = itemCount > 0 ? 'inline-block' : 'none';

  if (cart.length === 0) {
    cartItemsList.innerHTML = '<p class="text-gray-400 py-4 text-center">Tu carrito está vacío</p>';
    cartTotal.textContent = '0.00€';
    return;
  }

  let total = 0;
  cartItemsList.innerHTML = cart.map(item => {
    const itemTotal = (item.price || 5.99) * (item.quantity || 1);
    total += itemTotal;

    return `
        <div class="flex justify-between items-center p-2 hover:bg-gray-700 rounded">
            <div class="flex-1">
                <p class="text-white">${item.movie?.titulo || 'Película'}</p>
                <p class="text-gray-400 text-sm">${(item.price || 5.99).toFixed(2)}€ c/u</p>
            </div>
                <div class="flex items-center">
                    <!-- Grupo de botones de cantidad -->
                    <div class="flex items-center space-x-1">
                        <button onclick="updateCartItemQuantity(${item.id}, ${(item.quantity || 1) - 1})"
                                class="px-2 py-1 bg-gray-600 rounded hover:bg-gray-500">-</button>
                        <span class="w-8 text-center">${item.quantity || 1}</span>
                        <button onclick="updateCartItemQuantity(${item.id}, ${(item.quantity || 1) + 1})"
                                class="px-2 py-1 bg-gray-600 rounded hover:bg-gray-500">+</button>
                    </div>
                    <!-- Botón de eliminar con margen izquierdo -->
                    <button onclick="removeFromCart(${item.id})"
                            class="ml-3 px-3 py-1 bg-red-600 text-white rounded hover:bg-red-500">
                        ×
                    </button>
                </div>
        </div>
    `;
  }).join('');

  cartTotal.textContent = `${total.toFixed(2)}€`;
}

async function updateCartItemQuantity(itemId, newQuantity) {
  if (newQuantity < 1) return;

  const base64 = btoa(`${authUsername}:${authPassword}`);
  try {
    const res = await fetch(`${API_URL}/cart/${itemId}/quantity?quantity=${newQuantity}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Basic ${base64}`,
        'Content-Type': 'application/json'
      }
    });

    if (res.ok) {
      await loadCartFromBackend();
    } else {
      const error = await res.text();
      showCustomMessage(error || 'Error al actualizar la cantidad', 'error');
    }
  } catch (e) {
    console.error('Error al actualizar cantidad:', e);
    showCustomMessage('Error de conexión', 'error');
  }
}

function syncBuyButtonsWithCart() {
  // Si el carrito viene del backend: item.movie.id
  const idsEnCarrito = new Set(
    cart.map(item => item.movie ? item.movie.id : item.id) // por si aún usas formato antiguo
  );

  document.querySelectorAll('.buy-btn').forEach(btn => {
    const movieId = Number(btn.dataset.movieId);

    if (idsEnCarrito.has(movieId)) {
      btn.textContent = 'Añadida ✅';
      btn.disabled = true;
    } else {
      btn.textContent = 'Comprar Película';
      btn.disabled = false;
    }
  });
}


// Eliminar del carrito por índice y refrescar badge/lista
async function removeFromCart(cartItemId) {
  const base64 = btoa(`${authUsername}:${authPassword}`);

  const res = await fetch(`${API_URL}/cart/${cartItemId}`, {
    method: "DELETE",
    headers: { "Authorization": `Basic ${base64}` }
  });

  if (res.ok) {
    await loadCartFromBackend(); // actualiza carrito REAL
    showCustomMessage("Artículo eliminado del carrito", "success");
  } else {
    showCustomMessage("Error al eliminar del carrito", "error");
  }
}

// Finalizar compra -> backend /orders/checkout
async function checkout() {
  if (cart.length === 0) {
    showCustomMessage('El carrito está vacío', 'info');
    return;
  }

  try {
    const base64 = btoa(`${authUsername}:${authPassword}`);
    const res = await fetch(`${API_URL}/orders/checkout`, {
      method: 'POST',
      headers: {
        'Authorization': `Basic ${base64}`,
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    });

    const result = await res.json();

    if (res.ok) {
      // Vaciar el carrito solo después de una compra exitosa
      cart = [];
      updateCartUI();
      // Recargar el carrito desde el backend para asegurar consistencia
      await loadCartFromBackend();
      // Re-activar botones de compra en las tarjetas
      document.querySelectorAll('.buy-btn').forEach(b => {
        b.textContent = 'Comprar Película';
        b.disabled = false;
      });
      showCustomMessage(result.message || 'Compra realizada con éxito', 'success');
    } else {
      showCustomMessage(result || 'Error al procesar la compra', 'error');
    }
  } catch (e) {
    console.error('Error en checkout:', e);
    showCustomMessage('Error de conexión: ' + e.message, 'error');
  }
}

async function loadCartFromBackend() {
  const base64 = btoa(`${authUsername}:${authPassword}`);

  const res = await fetch(`${API_URL}/cart`, {
    headers: { "Authorization": `Basic ${base64}` }
  });

  if (res.ok) {
    cart = await res.json();   // carrito real
    updateCartUI();
    syncBuyButtonsWithCart();  // ⬅ sincroniza botones
  }
}

// Toggle del desplegable del carrito
document.addEventListener('click', (ev) => {
  const cartContainer = document.getElementById('cart-container');
  const dd = document.getElementById('cart-dropdown');
  const btn = document.getElementById('cart-btn');
  if (btn && btn.contains(ev.target)) {
    dd.style.display = (dd.style.display === 'block' ? 'none' : 'block');
  } else if (cartContainer && !cartContainer.contains(ev.target)) {
    dd.style.display = 'none';
  }
});

// Event Listeners
document.addEventListener('click', (e) => {
  if (e.target.matches('.movie-card-img')) {
    const card = e.target.closest('.movie-card');
    if (card && card.dataset.movieId) {
      showMovieDetails(card.dataset.movieId);
    }
  }
});

// Backup button click handler
document.getElementById('backup-btn').addEventListener('click', handleBackup);

// --- Backup Functionality ---
async function handleBackup() {
  const backupBtn = document.getElementById('backup-btn');
  const backupSpinner = document.getElementById('backup-spinner');
  const originalText = backupBtn.innerHTML;

  if (!isAuthenticated || !authUsername || !authPassword) {
    showCustomMessage('Debes iniciar sesión como administrador para realizar copias de seguridad.', 'error');
    return;
  }

  try {
    // Disable button and show spinner
    backupBtn.disabled = true;
    backupSpinner.classList.remove('hidden');
    backupBtn.querySelector('span').textContent = 'Procesando...';

    const base64 = btoa(`${authUsername}:${authPassword}`);
    const response = await fetch(`${API_URL}/admin/backup`, {
      method: 'POST',
      headers: {
        'Authorization': `Basic ${base64}`
      }
    });

    if (!response.ok) {
      throw new Error(`Error ${response.status}: ${response.statusText}`);
    }

    // Get filename from Content-Disposition header
    const contentDisposition = response.headers.get('Content-Disposition');
    const filename = contentDisposition
      ? contentDisposition.split('filename=')[1].replace(/"/g, '')
      : `backup_${new Date().toISOString().split('T')[0]}.zip`;

    // Create blob from response and trigger download
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();

    // Cleanup
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);

    showCustomMessage('Copia de seguridad generada con éxito', 'success');
  } catch (error) {
    console.error('Backup error:', error);
    showCustomMessage(`Error al generar la copia de seguridad: ${error.message}`, 'error');
  } finally {
    // Re-enable button and restore original state
    backupBtn.disabled = false;
    backupSpinner.classList.add('hidden');
    backupBtn.querySelector('span').textContent = 'Generar Copia de Seguridad';
  }
}

// Función para actualizar la vista previa de la imagen
function updateImagePreview(input) {
  const file = input.files[0];
  const previewImg = document.getElementById('image-preview-img');
  const previewContainer = document.getElementById('image-preview');
  const fileNameSpan = document.getElementById('selected-file-name');

  if (file) {
    const reader = new FileReader();

    reader.onload = function (e) {
      previewImg.src = e.target.result;
      previewContainer.classList.remove('hidden');
    };

    reader.readAsDataURL(file);

    // Mostrar solo el nombre del archivo, no la ruta completa
    const fileName = file.name;
    fileNameSpan.textContent = fileName;

    // Actualizar el campo oculto con el nombre del archivo
    document.getElementById('imagenUrl').value = fileName;
  } else {
    previewContainer.classList.add('hidden');
    fileNameSpan.textContent = '';
    document.getElementById('imagenUrl').value = '';
  }
}

// Validar año de la película
function validateYear(input) {
  const year = input.value;
  const errorElement = document.getElementById('year-error');

  if ((year.length !== 4)) {
    input.classList.add('border-red-500');
    errorElement.classList.remove('hidden');
    input.setCustomValidity('El año debe tener entre 3 y 4 dígitos');
  } else {
    input.classList.remove('border-red-500');
    errorElement.classList.add('hidden');
    input.setCustomValidity('');
  }
}

async function showMovieDetails(movieId) {
  try {
    const base64 = (isAuthenticated && authUsername && authPassword)
      ? btoa(`${authUsername}:${authPassword}`)
      : null;

    const res = await fetch(`${API_URL}/peliculas/${movieId}`, {
      headers: base64 ? { 'Authorization': `Basic ${base64}` } : {}
    });

    if (!res.ok) {
      showCustomMessage('No se pudieron cargar los detalles.', 'error');
      return;
    }

    const movie = await res.json();

    // Rellenar contenido
    document.getElementById('details-title').textContent = movie.titulo;
    const generos = (movie.generos || []).map(g => g.nombre).join(", ") || "Sin género";
    document.getElementById("details-year-genre").textContent =
      `${movie.anio} | ${generos}`;
    document.getElementById('details-rating').textContent =
      movie.rating != null ? `⭐ ${movie.rating} / 10` : '⭐ Sin valoración';
    document.getElementById('details-sinopsis').textContent = movie.sinopsis || 'Sinopsis no disponible.';

    const modal = document.getElementById('movie-details-modal');
    const content = document.getElementById('movie-details-content');
    if (!modal || !content) return;

    // Mostrar modal + animación suave
    modal.classList.remove('hidden');
    content.style.opacity = '0';
    content.style.transform = 'scale(0.9)';

    requestAnimationFrame(() => {
      content.style.opacity = '1';
      content.style.transform = 'scale(1)';
    });

  } catch (e) {
    console.error(e);
    showCustomMessage('Error cargando la película.', 'error');
  }
}

function closeMovieDetails() {
  const modal = document.getElementById('movie-details-modal');
  const content = document.getElementById('movie-details-content');
  if (!modal || !content) return;

  content.style.opacity = '0';
  content.style.transform = 'scale(0.9)';

  setTimeout(() => {
    modal.classList.add('hidden');
  }, 200);
}

// --- Inicialización ---
document.addEventListener('DOMContentLoaded', () => {

  //  AÑADIR NUEVOS GÉNEROS
  const addGenreBtn = document.getElementById('add-genre-btn');
  const newGenreInput = document.getElementById('new-genre');

  if (addGenreBtn) {
    addGenreBtn.addEventListener('click', addGenre);
  }

  if (newGenreInput) {
    newGenreInput.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        addGenre();
      }
    });
  }

  // Inicializar la UI (no logueado)
  updateUI(null, false);

  //  MANEJO LOGIN / REGISTER
  const openLoginBtn = document.getElementById('open-login-btn');
  const openRegisterBtn = document.getElementById('open-register-btn');
  const loginForm = document.getElementById('login-form');
  const registerForm = document.getElementById('register-form');

  if (openLoginBtn) {
    openLoginBtn.addEventListener('click', () => openModal('login-modal'));
  }

  if (openRegisterBtn) {
    openRegisterBtn.addEventListener('click', () => openModal('register-modal'));
  }

  if (loginForm) {
    loginForm.addEventListener('submit', function (e) {
      e.preventDefault();
      handleLogin();
    });
  }

  if (registerForm) {
    registerForm.addEventListener('submit', function (e) {
      e.preventDefault();
      handleRegister();
    });
  }

  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', handleLogout);
  }

  //  FORMULARIO DE PELÍCULAS
  const movieForm = document.getElementById('movie-form');
  if (movieForm) {
    movieForm.addEventListener('submit', handleMovieFormSubmit);
    const cancelEditBtn = document.getElementById('cancel-edit-btn');
    if (cancelEditBtn) {
      cancelEditBtn.addEventListener('click', resetMovieForm);
    }
  }

  //  CERRAR MODAL DE PELÍCULA
  const movieModal = document.getElementById('movie-details-modal');
  if (movieModal) {
    movieModal.addEventListener('click', (e) => {
      const content = document.getElementById('movie-details-content');
      if (content && !content.contains(e.target)) {
        closeMovieDetails();
      }
    });
  }

  //     BUSCADOR GLOBAL
  const searchInput = document.getElementById("search-input");
  const searchBtn = document.getElementById("search-btn");

  function searchMovie() {
    const termRaw = searchInput.value;
    const term = termRaw.trim().toLowerCase();
    if (!term) return;

    if (!Array.isArray(movies) || movies.length === 0) {
      showCustomMessage("No hay películas cargadas.", "error");
      return;
    }

    // Buscar título EXACTO (ignorando mayúsculas/minúsculas)
    const movie = movies.find(m =>
      m.titulo &&
      m.titulo.trim().toLowerCase() === term
    );

    if (!movie) {
      showCustomMessage("No se encontró ninguna película con ese título exacto.", "error");
      return;
    }

    const card = document.querySelector(`[data-movie-id="${movie.id}"]`);
    if (card) {
      card.scrollIntoView({ behavior: "smooth", block: "center" });

      card.classList.add("ring-4", "ring-red-600");
      setTimeout(() => {
        card.classList.remove("ring-4", "ring-red-600");
      }, 1500);
    }
  }


  // Click en botón
  if (searchBtn) {
    searchBtn.addEventListener("click", searchMovie);
  }

  // Enter en la caja de texto
  if (searchInput) {
    searchInput.addEventListener("keypress", e => {
      if (e.key === "Enter") {
        e.preventDefault();
        searchMovie();
      }
    });
  }

});
// --- MENÚ DE USUARIO ---
const userMenuBtn = document.getElementById("user-menu-btn");
const userMenuDropdown = document.getElementById("user-menu-dropdown");
const userMenuContainer = document.getElementById("user-menu-container");

// Toggle menú
if (userMenuBtn) {
  userMenuBtn.addEventListener("click", () => {
    userMenuDropdown.classList.toggle("hidden");
  });
}

// Cerrar al hacer clic fuera
document.addEventListener("click", (e) => {
  if (
    userMenuContainer &&
    !userMenuContainer.contains(e.target)
  ) {
    userMenuDropdown.classList.add("hidden");
  }
});

async function openUserProfile() {
  try {
    const base64 = btoa(`${authUsername}:${authPassword}`);

    const res = await fetch(`${API_URL}/perfil/me`, {
      headers: { "Authorization": `Basic ${base64}` }
    });

    if (!res.ok) {
      showCustomMessage("Error cargando perfil", "error");
      return;
    }

    const data = await res.json();

    // Inputs
    const usernameEl = document.getElementById("profile-username");
    const emailEl = document.getElementById("profile-email");
    const addressEl = document.getElementById("profile-address");
    const postalEl = document.getElementById("profile-postal");
    const birthYearEl = document.getElementById("profile-birthyear");
    const memberEl = document.getElementById("profile-member");

    if (usernameEl) usernameEl.value = data.username ?? "";
    if (emailEl) emailEl.value = data.email ?? "";
    if (addressEl) addressEl.value = data.address ?? "";
    if (postalEl) postalEl.value = data.postalCode ?? "";
    if (birthYearEl) birthYearEl.value = data.birthYear ?? "";
    if (memberEl) memberEl.value = data.memberSince ?? "—";

    // Stats
    const statPurch = document.getElementById("stat-purchases");
    const statFavs = document.getElementById("stat-favorites");
    const statMovies = document.getElementById("stat-movies");

    if (statPurch) statPurch.textContent = data.totalPurchases ?? 0;
    if (statFavs) statFavs.textContent = data.totalFavorites ?? 0;
    if (statMovies) statMovies.textContent = data.totalMovies ?? 0;

    // Mostrar modal
    const modal = document.getElementById("profile-modal");
    if (modal) modal.classList.remove("hidden");

  } catch (err) {
    console.error("openUserProfile error:", err);
    showCustomMessage("Error inesperado al abrir perfil", "error");
  }
}

function hideUserSections() {
  document.getElementById("user-library-section")?.classList.add("hidden");
  document.getElementById("user-favorites-section")?.classList.add("hidden");
  document.getElementById("profile-modal")?.classList.add("hidden");
}

async function openUserLibrary() {
  const base64 = btoa(`${authUsername}:${authPassword}`);

  const res = await fetch(`${API_URL}/biblioteca`, {
    headers: { "Authorization": `Basic ${base64}` }
  });

  if (!res.ok) {
    showCustomMessage("Error al cargar tu biblioteca", "error");
    return;
  }

  const items = await res.json();

  const section = document.getElementById("user-library-section");
  const container = document.getElementById("library-container");
  const msg = document.getElementById("library-empty-msg");

  // Reset
  container.innerHTML = "";

  if (items.length === 0) {
    msg.classList.remove("hidden");
  } else {
    msg.classList.add("hidden");
  }

  // Pintar carátulas
  items.forEach(item => {
    const card = document.createElement("div");
    card.classList = "bg-gray-900 rounded-lg overflow-hidden shadow-md";

    const img = item.urlImagen
      ? `images/${item.urlImagen}`
      : "images/placeholder.png";

    card.innerHTML = `
      <img src="${img}" class="w-full object-cover aspect-[2/3]" />
      <div class="p-3">
        <h4 class="font-bold truncate">${item.tituloPelicula}</h4>
        <p class="text-sm text-gray-400">Comprada: ${item.cantidadComprada} veces</p>
        <p class="text-xs text-gray-500">Última compra: ${item.ultimaFechaCompra.split("T")[0]}</p>
      </div>
    `;
    container.appendChild(card);
  });

  // Mostrar sección de biblioteca
  document.getElementById("main-content").scrollIntoView({
    behavior: "smooth",
    block: "start"
  });

  section.classList.remove("hidden");

  // Ocultar catálogo mientras tanto
  document.getElementById("movies-container").parentElement.classList.add("hidden");
}


async function saveProfile() {
  const email = document.getElementById("profile-email");
  const address = document.getElementById("profile-address");
  const postal = document.getElementById("profile-postal");
  const birthYear = document.getElementById("profile-birthyear");
  const memberSince = document.getElementById("profile-member");

  // Reset visual errors
  [email, address, postal, birthYear].forEach(el => {
    el.classList.remove("border-red-500");
    el.setCustomValidity("");
  });

  // Validación de email antes de enviar
  const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/;
  if (!emailRegex.test(email.value)) {
    email.classList.add("border-red-500");
    email.setCustomValidity("Formato de email inválido (ej: usuario@correo.com)");
    email.reportValidity();
    return;
  }

  const payload = {
    email: email.value,
    address: address.value,
    postalCode: postal.value,
    birthYear: parseInt(birthYear.value),
    memberSince: memberSince.value
  };

  const base64 = btoa(`${authUsername}:${authPassword}`);

  const res = await fetch(`${API_URL}/perfil/me`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Basic ${base64}`
    },
    body: JSON.stringify(payload)
  });

  if (res.ok) {
    showCustomMessage("Perfil actualizado correctamente", "success");

    closeProfileModal();

    // Volver arriba a las carátulas
    document.getElementById("main-content").scrollIntoView({
      behavior: "smooth",
      block: "start"
    });

    return;
  }

  // Si hay errores → mostrarlos como tooltips bonitos del input
  const errors = await res.json();

  if (errors.email) {
    email.classList.add("border-red-500");
    email.setCustomValidity(errors.email);
    email.reportValidity();
  }

  if (errors.birthYear) {
    birthYear.classList.add("border-red-500");
    birthYear.setCustomValidity(errors.birthYear);
    birthYear.reportValidity();
  }

  if (errors.postalCode) {
    postal.classList.add("border-red-500");
    postal.setCustomValidity(errors.postalCode);
    postal.reportValidity();
  }

  if (errors.address) {
    address.classList.add("border-red-500");
    address.setCustomValidity(errors.address);
    address.reportValidity();
  }
}

function closeProfileModal() {
  const modal = document.getElementById("profile-modal");
  if (modal) modal.classList.add("hidden");
}

document.addEventListener("click", (e) => {
  const modal = document.getElementById("profile-modal");
  const content = document.getElementById("profile-modal-content");

  if (!modal || modal.classList.contains("hidden")) return;

  if (content && !content.contains(e.target)) {
    closeProfileModal();
  }
});

// Cerrar modal al hacer clic fuera del contenido
document.addEventListener("click", (e) => {
  const modal = document.getElementById("profile-modal");
  const content = document.getElementById("profile-modal-content");

  if (!modal) return;
  if (modal.classList.contains("hidden")) return;

  // Si haces clic en el fondo (modal) pero NO en el contenido
  if (modal === e.target) {
    closeProfileModal();
  }
});

async function openUserLibrary() {
  if (!authUsername || !authPassword) {
    showCustomMessage("Debes iniciar sesión como usuario.", "error");
    return;
  }

  hideUserSections();

  const base64 = btoa(`${authUsername}:${authPassword}`);

  try {
    const res = await fetch(`${API_URL}/biblioteca`, {
      headers: { "Authorization": `Basic ${base64}` }
    });

    if (!res.ok) {
      showCustomMessage("Error al cargar tu biblioteca", "error");
      return;
    }

    const items = await res.json();
    console.log("Mi Biblioteca:", items);

    // --- Mostrar sección Biblioteca ---
    const librarySection = document.getElementById("user-library-section");
    const libraryList = document.getElementById("library-container");
    const catalog = document.getElementById("movies-container")?.parentElement;

    librarySection.classList.remove("hidden");
    if (catalog) catalog.classList.add("hidden");

    // --- Renderizar contenido ---
    libraryList.innerHTML = "";

    if (items.length === 0) {
      libraryList.innerHTML = `
        <p class="text-center text-gray-400 col-span-full">
          Todavía no has comprado ninguna película.
        </p>
      `;
      return;
    }

    items.forEach(item => {
      libraryList.innerHTML += `
        <div class="bg-gray-900 p-4 rounded-lg shadow-md hover:shadow-xl transition">
          <img src="images/${item.imagenUrl || 'placeholder.png'}"
               class="w-full h-auto rounded mb-3"
               onerror="this.src='images/placeholder.png'">

          <h3 class="font-bold text-white">${item.titulo}</h3>

          <p class="text-sm text-gray-400 mt-1">
            Compradas: <span class="text-green-400 font-bold">${item.cantidadComprada}</span>
          </p>

          <p class="text-xs text-gray-500 mt-1">
            Última compra: ${item.ultimaFechaCompra
          ? item.ultimaFechaCompra.substring(0, 10)
          : "Desconocida"}
          </p>
        </div>
      `;
    });

  } catch (err) {
    console.error(err);
    showCustomMessage("Error de conexión al cargar la biblioteca", "error");
  }
}

function closeUserLibrary() {
  hideUserSections();

  const catalogSection = document.getElementById("movies-container")?.parentElement;
  if (catalogSection) catalogSection.classList.remove("hidden");

  window.scrollTo({ top: 0, behavior: "smooth" });
}


// ========== FAVORITOS ==========

// Cargar IDs de favoritos del usuario
async function loadFavorites() {
  if (!isAuthenticated || !authUsername || !authPassword) return;

  const base64 = btoa(`${authUsername}:${authPassword}`);
  try {
    const res = await fetch(`${API_URL}/favoritos`, {
      headers: { 'Authorization': `Basic ${base64}` }
    });

    if (res.ok) {
      userFavorites = await res.json(); // array de IDs
      updateStarsInCatalog(); // Actualizar estrellas existentes
    }
  } catch (e) {
    console.error('Error loading favorites:', e);
  }
}

// Toggle favorito (añadir/eliminar)
async function toggleFavorite(movieId, starElement) {
  const base64 = btoa(`${authUsername}:${authPassword}`);

  try {
    const res = await fetch(`${API_URL}/favoritos/${movieId}`, {
      method: 'POST',
      headers: { 'Authorization': `Basic ${base64}` }
    });

    if (res.ok) {
      const isFavorite = userFavorites.includes(movieId);

      if (isFavorite) {
        // Eliminar de favoritos
        userFavorites = userFavorites.filter(id => id !== movieId);
        starElement.textContent = '☆';
        showCustomMessage('Eliminado de favoritos', 'success');
      } else {
        // Añadir a favoritos
        userFavorites.push(movieId);
        starElement.textContent = '★';
        showCustomMessage('Añadido a favoritos', 'success');
      }
    } else {
      showCustomMessage('Error al actualizar favoritos', 'error');
    }
  } catch (e) {
    console.error('Error toggling favorite:', e);
    showCustomMessage('Error de conexión', 'error');
  }
}

// Abrir sección "Mis Favoritos"
async function openUserFavorites() {
  if (!authUsername || !authPassword) {
    showCustomMessage('Debes iniciar sesión como usuario.', 'error');
    return;
  }

  hideUserSections();

  const base64 = btoa(`${authUsername}:${authPassword}`);

  try {
    const res = await fetch(`${API_URL}/favoritos/mis-favoritos`, {
      headers: { 'Authorization': `Basic ${base64}` }
    });

    if (!res.ok) {
      showCustomMessage('Error al cargar tus favoritos', 'error');
      return;
    }

    const items = await res.json();

    // Mostrar sección de favoritos
    const favoritesSection = document.getElementById('user-favorites-section');
    const favoritesList = document.getElementById('favorites-container');
    const emptyMsg = document.getElementById('favorites-empty-msg');
    const catalog = document.getElementById('movies-container')?.parentElement;

    favoritesSection.classList.remove('hidden');
    if (catalog) catalog.classList.add('hidden');

    // Renderizar contenido
    favoritesList.innerHTML = '';

    if (items.length === 0) {
      emptyMsg.classList.remove('hidden');
      return;
    }

    emptyMsg.classList.add('hidden');

    items.forEach(item => {
      const card = document.createElement('div');
      card.className = 'bg-gray-900 rounded-lg overflow-hidden shadow-md hover:shadow-xl transition relative';

      const img = item.imagenUrl
        ? `images/${item.imagenUrl}`
        : 'images/placeholder.png';

      card.innerHTML = `
        <img src="${img}"
             class="w-full object-cover aspect-[2/3] cursor-pointer"
             onclick="showMovieDetails(${item.id})"
             onerror="this.src='images/placeholder.png'">
        <div class="p-3">
          <h4 class="font-bold truncate">${item.titulo}</h4>
          <p class="text-sm text-gray-400">⭐ ${item.rating || 'N/A'} | ${item.anio}</p>
          <p class="text-xs text-gray-500">${(item.generos || []).join(', ')}</p>
        </div>
      `;

      // Añadir estrella que elimina de favoritos
      const star = document.createElement('button');
      star.className = 'favorite-star absolute bottom-2 right-2 text-2xl hover:scale-110 transition-transform';
      star.textContent = '★';
      star.style.color = '#FFD700';
      star.style.background = 'none';
      star.style.border = 'none';
      star.style.cursor = 'pointer';
      star.style.filter = 'drop-shadow(0 0 2px rgba(0, 0, 0, 0.8))';
      star.style.zIndex = '10';
      star.onclick = async (e) => {
        e.stopPropagation();
        await removeFavoriteFromView(item.id, card);
      };

      card.appendChild(star);
      favoritesList.appendChild(card);
    });

  } catch (err) {
    console.error(err);
    showCustomMessage('Error de conexión al cargar favoritos', 'error');
  }
}

// Eliminar favorito desde la vista "Mis Favoritos"
async function removeFavoriteFromView(movieId, cardElement) {
  const base64 = btoa(`${authUsername}:${authPassword}`);

  try {
    const res = await fetch(`${API_URL}/favoritos/${movieId}`, {
      method: 'POST',
      headers: { 'Authorization': `Basic ${base64}` }
    });

    if (res.ok) {
      // Eliminar del array local
      userFavorites = userFavorites.filter(id => id !== movieId);

      // Eliminar visualmente
      cardElement.remove();

      // Si no quedan favoritos, mostrar mensaje
      const container = document.getElementById('favorites-container');
      if (container && container.children.length === 0) {
        const emptyMsg = document.getElementById('favorites-empty-msg');
        if (emptyMsg) emptyMsg.classList.remove('hidden');
      }

      showCustomMessage('Eliminado de favoritos', 'success');
    }
  } catch (e) {
    console.error('Error removing favorite:', e);
    showCustomMessage('Error de conexión', 'error');
  }
}

// Cerrar sección "Mis Favoritos"
function closeUserFavorites() {
  hideUserSections();

  const catalogSection = document.getElementById("movies-container")?.parentElement;
  if (catalogSection) catalogSection.classList.remove("hidden");

  updateStarsInCatalog();

  window.scrollTo({ top: 0, behavior: "smooth" });
}

// Actualizar estrellas en el catálogo
function updateStarsInCatalog() {
  document.querySelectorAll('.favorite-star').forEach(star => {
    const movieId = Number(star.dataset.movieId);
    const isFavorite = userFavorites.includes(movieId);
    star.textContent = isFavorite ? '★' : '☆';
  });
}
