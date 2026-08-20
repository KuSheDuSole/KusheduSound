// ---------- state ----------
let accessToken = localStorage.getItem('accessToken') || null;
let currentUser = null; // { id, email, username, isAdmin }

// ---------- dom refs ----------
const authScreen = document.getElementById('authScreen');
const tabsNav = document.getElementById('tabs');
const whoamiEl = document.getElementById('whoami');
const refreshBtn = document.getElementById('refreshBtn');
const logoutBtn = document.getElementById('logoutBtn');

const tabPanels = {
  library: document.getElementById('libraryTab'),
  space: document.getElementById('spaceTab'),
  admin: document.getElementById('adminTab'),
};

const tracksEl = document.getElementById('tracks');
const countEl = document.getElementById('trackCount');

// ---------- helpers ----------
function escapeHtml(value) {
  const div = document.createElement('div');
  div.textContent = value ?? '';
  return div.innerHTML;
}

function parseJwt(token) {
  try {
    const payload = token.split('.')[1];
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decodeURIComponent(escape(json)));
  } catch {
    return null;
  }
}

function setAccessToken(token) {
  accessToken = token;
  if (token) {
    localStorage.setItem('accessToken', token);
    const claims = parseJwt(token);
    if (claims) {
      currentUser = {
        id: claims.userId,
        email: claims.sub,
        username: claims.username,
        isAdmin: (claims.authorities || '').includes('ROLE_ADMIN'),
      };
    }
  } else {
    localStorage.removeItem('accessToken');
    currentUser = null;
  }
}

// fetch wrapper: attaches auth header, retries once via /auth/refresh on 401
async function api(path, options = {}) {
  const doFetch = () => fetch(path, {
    ...options,
    credentials: 'include',
    headers: {
      ...(options.headers || {}),
      ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
    },
  });

  let response = await doFetch();

  if (response.status === 401 && accessToken) {
    const refreshed = await tryRefresh();
    if (refreshed) {
      response = await doFetch();
    }
  }

  return response;
}

async function tryRefresh() {
  try {
    const res = await fetch('/auth/refresh', { method: 'POST', credentials: 'include' });
    if (!res.ok) return false;
    const data = await res.json();
    setAccessToken(data.access);
    return true;
  } catch {
    return false;
  }
}

// ---------- auth screen ----------
const showLoginBtn = document.getElementById('showLogin');
const showRegisterBtn = document.getElementById('showRegister');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const loginMsg = document.getElementById('loginMsg');
const registerMsg = document.getElementById('registerMsg');

showLoginBtn.addEventListener('click', () => {
  showLoginBtn.classList.add('active');
  showRegisterBtn.classList.remove('active');
  loginForm.classList.remove('hidden');
  registerForm.classList.add('hidden');
});

showRegisterBtn.addEventListener('click', () => {
  showRegisterBtn.classList.add('active');
  showLoginBtn.classList.remove('active');
  registerForm.classList.remove('hidden');
  loginForm.classList.add('hidden');
});

loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  loginMsg.textContent = '';
  const email = document.getElementById('loginEmail').value;
  const password = document.getElementById('loginPassword').value;

  try {
    const res = await fetch('/auth/login', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) throw new Error(`Не удалось войти (${res.status})`);
    const data = await res.json();
    setAccessToken(data.access);
    enterApp();
  } catch (err) {
    loginMsg.textContent = err.message;
    loginMsg.classList.add('error');
  }
});

registerForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  registerMsg.textContent = '';
  const username = document.getElementById('regUsername').value;
  const email = document.getElementById('regEmail').value;
  const password = document.getElementById('regPassword').value;

  try {
    const res = await fetch('/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password }),
    });
    if (!res.ok) throw new Error(`Не удалось зарегистрироваться (${res.status})`);
    registerMsg.classList.remove('error');
    registerMsg.textContent = 'Готово — теперь войди со своими данными.';
    showLoginBtn.click();
    document.getElementById('loginEmail').value = email;
  } catch (err) {
    registerMsg.textContent = err.message;
    registerMsg.classList.add('error');
  }
});

// ---------- app shell ----------
function enterApp() {
  authScreen.classList.add('hidden');
  tabsNav.classList.remove('hidden');
  refreshBtn.classList.remove('hidden');
  logoutBtn.classList.remove('hidden');
  whoamiEl.classList.remove('hidden');
  whoamiEl.textContent = `${currentUser.username}${currentUser.isAdmin ? ' · admin' : ''}`;

  document.querySelectorAll('.admin-only').forEach(el => {
    el.classList.toggle('hidden', !currentUser.isAdmin);
  });

  switchTab('library');
  loadTracks();
}

function leaveApp() {
  setAccessToken(null);
  authScreen.classList.remove('hidden');
  tabsNav.classList.add('hidden');
  refreshBtn.classList.add('hidden');
  logoutBtn.classList.add('hidden');
  whoamiEl.classList.add('hidden');
  Object.values(tabPanels).forEach(panel => panel.classList.add('hidden'));
}

function switchTab(tab) {
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.tab === tab);
  });
  Object.entries(tabPanels).forEach(([name, panel]) => {
    panel.classList.toggle('hidden', name !== tab);
  });
  if (tab === 'space') loadSpace();
  if (tab === 'admin' && currentUser?.isAdmin) loadAdmin();
}

document.querySelectorAll('.tab-btn').forEach(btn => {
  btn.addEventListener('click', () => switchTab(btn.dataset.tab));
});

logoutBtn.addEventListener('click', async () => {
  try { await api('/auth/logout', { method: 'POST' }); } catch { /* сервер недоступен — всё равно выходим локально */ }
  leaveApp();
});

// ---------- library ----------
function trackCardHtml(track, { showFavourite = true } = {}) {
  const streamUrl = `/tracks/${track.id}/stream?access_token=${encodeURIComponent(accessToken)}`;
  return `
    <article class="track-card" data-track-id="${track.id}">
      <div class="track-head">
        <div>
          <p class="track-title">${escapeHtml(track.title)}</p>
          <p class="track-artist">${escapeHtml(track.artistName ?? 'Неизвестный артист')}</p>
        </div>
        ${showFavourite ? `<button class="fav-btn" data-track-id="${track.id}" title="В избранное" type="button">♡</button>` : ''}
      </div>
      <audio controls preload="metadata" src="${streamUrl}"></audio>
    </article>
  `;
}

async function loadTracks() {
  tracksEl.innerHTML = '<p class="state-msg">Загрузка…</p>';
  countEl.textContent = '—';

  try {
    const response = await api('/tracks');
    if (!response.ok) throw new Error(`Сервер ответил ${response.status}`);
    const tracks = await response.json();

    if (!tracks.length) {
      tracksEl.innerHTML = '<p class="state-msg">Треков пока нет — загрузи что-нибудь в разделе «Админ».</p>';
      countEl.textContent = '0 треков';
      return;
    }

    countEl.textContent = `${tracks.length} ${tracks.length === 1 ? 'трек' : 'треков'}`;
    tracksEl.innerHTML = tracks.map(t => trackCardHtml(t)).join('');

    tracksEl.querySelectorAll('.fav-btn').forEach(btn => {
      btn.addEventListener('click', () => addFavourite(btn.dataset.trackId));
    });
  } catch (err) {
    tracksEl.innerHTML = `<p class="state-msg error">Не удалось загрузить треки: ${escapeHtml(err.message)}</p>`;
    countEl.textContent = '—';
  }
}

refreshBtn.addEventListener('click', () => {
  loadTracks();
  if (!tabPanels.space.classList.contains('hidden')) loadSpace();
  if (!tabPanels.admin.classList.contains('hidden')) loadAdmin();
});

// ---------- my space ----------
const favouritesEl = document.getElementById('favourites');
const playlistsEl = document.getElementById('playlists');
const createPlaylistForm = document.getElementById('createPlaylistForm');

async function loadSpace() {
  loadFavourites();
  loadPlaylists();
}

async function loadFavourites() {
  favouritesEl.innerHTML = '<p class="state-msg">Загрузка…</p>';
  try {
    const res = await api(`/users/${currentUser.id}/favourites`);
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    const tracks = await res.json();

    if (!tracks.length) {
      favouritesEl.innerHTML = '<p class="state-msg">Пока пусто — добавь треки из библиотеки.</p>';
      return;
    }

    favouritesEl.innerHTML = tracks.map(t => `
      <div class="mini-row">
        <div>
          <p class="mini-title">${escapeHtml(t.title)}</p>
          <p class="mini-sub">${escapeHtml(t.artistName ?? '—')}</p>
        </div>
        <button class="btn-ghost btn-xs" data-remove-fav="${t.id}" type="button">Убрать</button>
      </div>
    `).join('');

    favouritesEl.querySelectorAll('[data-remove-fav]').forEach(btn => {
      btn.addEventListener('click', () => removeFavourite(btn.dataset.removeFav));
    });
  } catch (err) {
    favouritesEl.innerHTML = `<p class="state-msg error">${escapeHtml(err.message)}</p>`;
  }
}

async function addFavourite(trackId) {
  try {
    const res = await api(`/users/${currentUser.id}/favourites/${trackId}`, { method: 'POST' });
    if (!res.ok && res.status !== 409) throw new Error(`Сервер ответил ${res.status}`);
    if (!tabPanels.space.classList.contains('hidden')) loadFavourites();
  } catch (err) {
    alert(`Не удалось добавить в избранное: ${err.message}`);
  }
}

async function removeFavourite(trackId) {
  try {
    const res = await api(`/users/${currentUser.id}/favourites/${trackId}`, { method: 'DELETE' });
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    loadFavourites();
  } catch (err) {
    alert(`Не удалось убрать из избранного: ${err.message}`);
  }
}

async function loadPlaylists() {
  playlistsEl.innerHTML = '<p class="state-msg">Загрузка…</p>';
  try {
    const res = await api('/playlists/mine');
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    const playlists = await res.json();

    if (!playlists.length) {
      playlistsEl.innerHTML = '<p class="state-msg">Плейлистов пока нет — создай первый выше.</p>';
      return;
    }

    playlistsEl.innerHTML = playlists.map(p => `
      <div class="mini-row">
        <div>
          <p class="mini-title">${escapeHtml(p.title)}</p>
          <p class="mini-sub">создан ${new Date(p.createdAt).toLocaleDateString('ru-RU')}</p>
        </div>
        <button class="btn-ghost btn-xs" data-remove-playlist="${p.id}" type="button">Удалить</button>
      </div>
    `).join('');

    playlistsEl.querySelectorAll('[data-remove-playlist]').forEach(btn => {
      btn.addEventListener('click', () => removePlaylist(btn.dataset.removePlaylist));
    });
  } catch (err) {
    playlistsEl.innerHTML = `<p class="state-msg error">${escapeHtml(err.message)}</p>`;
  }
}

createPlaylistForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const titleInput = document.getElementById('playlistTitle');
  try {
    const res = await api('/playlists', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: titleInput.value }),
    });
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    titleInput.value = '';
    loadPlaylists();
  } catch (err) {
    alert(`Не удалось создать плейлист: ${err.message}`);
  }
});

async function removePlaylist(id) {
  if (!confirm('Удалить плейлист?')) return;
  try {
    const res = await api(`/playlists/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    loadPlaylists();
  } catch (err) {
    alert(`Не удалось удалить плейлист: ${err.message}`);
  }
}

// ---------- admin ----------
const artistSelects = () => [document.getElementById('albumArtistId'), document.getElementById('trackArtistId')];
const albumSelect = document.getElementById('trackAlbumId');
const usersListEl = document.getElementById('usersList');

async function loadAdmin() {
  loadArtistOptions();
  loadAlbumOptions();
  loadUsers();
}

async function loadArtistOptions() {
  try {
    const res = await api('/artists');
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    const artists = await res.json();
    const optionsHtml = artists.map(a => `<option value="${a.id}">${escapeHtml(a.name)}</option>`).join('');
    artistSelects().forEach(select => {
      select.innerHTML = optionsHtml || '<option value="">Нет артистов</option>';
    });
  } catch (err) {
    artistSelects().forEach(select => { select.innerHTML = '<option value="">Ошибка загрузки</option>'; });
  }
}

async function loadAlbumOptions() {
  try {
    const res = await api('/albums');
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    const albums = await res.json();
    albumSelect.innerHTML = '<option value="">Без альбома</option>' +
      albums.map(a => `<option value="${a.id}">${escapeHtml(a.title)}</option>`).join('');
  } catch {
    albumSelect.innerHTML = '<option value="">Ошибка загрузки</option>';
  }
}

async function loadUsers() {
  usersListEl.innerHTML = '<p class="state-msg">Загрузка…</p>';
  try {
    const res = await api('/users');
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    const users = await res.json();
    usersListEl.innerHTML = users.map(u => `
      <div class="mini-row">
        <div>
          <p class="mini-title">${escapeHtml(u.username)}</p>
          <p class="mini-sub">${escapeHtml(u.email)}</p>
        </div>
        <span class="mono-tag">#${u.id}</span>
      </div>
    `).join('') || '<p class="state-msg">Пользователей нет.</p>';
  } catch (err) {
    usersListEl.innerHTML = `<p class="state-msg error">${escapeHtml(err.message)}</p>`;
  }
}

document.getElementById('createArtistForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const msg = document.getElementById('artistMsg');
  msg.textContent = '';
  try {
    const res = await api('/artists', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: document.getElementById('artistName').value,
        bio: document.getElementById('artistBio').value || null,
      }),
    });
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    document.getElementById('createArtistForm').reset();
    msg.textContent = 'Артист создан.';
    msg.classList.remove('error');
    loadArtistOptions();
  } catch (err) {
    msg.textContent = err.message;
    msg.classList.add('error');
  }
});

document.getElementById('createAlbumForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const msg = document.getElementById('albumMsg');
  msg.textContent = '';
  try {
    const res = await api('/albums', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: document.getElementById('albumTitle').value,
        artistId: Number(document.getElementById('albumArtistId').value),
        releaseDate: document.getElementById('albumReleaseDate').value || null,
      }),
    });
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    document.getElementById('createAlbumForm').reset();
    msg.textContent = 'Альбом создан.';
    msg.classList.remove('error');
    loadAlbumOptions();
  } catch (err) {
    msg.textContent = err.message;
    msg.classList.add('error');
  }
});

document.getElementById('uploadTrackForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const msg = document.getElementById('trackMsg');
  msg.textContent = '';

  const file = document.getElementById('trackFile').files[0];
  if (!file) return;

  const data = {
    title: document.getElementById('trackTitle').value,
    artistId: Number(document.getElementById('trackArtistId').value),
    albumId: document.getElementById('trackAlbumId').value ? Number(document.getElementById('trackAlbumId').value) : null,
  };

  const formData = new FormData();
  formData.append('file', file);
  formData.append('data', JSON.stringify(data));

  try {
    const res = await api('/tracks', { method: 'POST', body: formData });
    if (!res.ok) throw new Error(`Сервер ответил ${res.status}`);
    document.getElementById('uploadTrackForm').reset();
    msg.textContent = 'Трек загружен.';
    msg.classList.remove('error');
    loadTracks();
  } catch (err) {
    msg.textContent = err.message;
    msg.classList.add('error');
  }
});

// ---------- boot ----------
(async function boot() {
  if (accessToken) {
    setAccessToken(accessToken); // re-derive currentUser from stored token
    // проверяем что токен ещё жив (или обновляем через refresh-cookie)
    const res = await api('/tracks');
    if (res.ok || (await tryRefresh())) {
      enterApp();
      return;
    }
  }
  leaveApp();
})();
