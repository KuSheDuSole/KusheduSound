const tracksEl = document.getElementById('tracks');
const countEl = document.getElementById('trackCount');
const refreshBtn = document.getElementById('refreshBtn');

function escapeHtml(value) {
  const div = document.createElement('div');
  div.textContent = value ?? '';
  return div.innerHTML;
}

function renderTracks(tracks) {
  if (!tracks.length) {
    tracksEl.innerHTML = '<p class="state-msg">Треков пока нет — загрузи что-нибудь через API.</p>';
    countEl.textContent = '0 треков';
    return;
  }

  countEl.textContent = `${tracks.length} ${tracks.length === 1 ? 'трек' : 'треков'}`;

  tracksEl.innerHTML = tracks.map((track, i) => `
    <article class="track-card">
      <div class="track-head">
        <div>
          <p class="track-title">${escapeHtml(track.title)}</p>
          <p class="track-artist">${escapeHtml(track.artist?.name ?? 'Неизвестный артист')}</p>
        </div>
        <span class="track-index">${String(i + 1).padStart(2, '0')}</span>
      </div>
      <audio controls preload="metadata" src="/tracks/${track.id}/stream"></audio>
    </article>
  `).join('');
}

async function loadTracks() {
  tracksEl.innerHTML = '<p class="state-msg">Загрузка…</p>';
  countEl.textContent = '—';

  try {
    const response = await fetch('/tracks');
    if (!response.ok) {
      throw new Error(`Сервер ответил ${response.status}`);
    }
    const tracks = await response.json();
    renderTracks(tracks);
  } catch (err) {
    tracksEl.innerHTML = `<p class="state-msg error">Не удалось загрузить треки: ${escapeHtml(err.message)}</p>`;
    countEl.textContent = '—';
  }
}

refreshBtn.addEventListener('click', loadTracks);
loadTracks();
