// api.js — Camada de API centralizada
const API = {
  getToken: () => localStorage.getItem('token'),
  getRole:  () => localStorage.getItem('role'),
  getUser:  () => localStorage.getItem('username'),
  isAdmin:  () => localStorage.getItem('role') === 'ROLE_ADMIN',

  logout() {
    localStorage.clear();
    window.location.href = (location.pathname.includes('/pages/') ? '../' : '') + 'index.html';
  },

  async _fetch(method, path, body) {
    const base = typeof CONFIG !== 'undefined' ? CONFIG.API_URL : 'http://localhost:8080';
    const res = await fetch(`${base}${path}`, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      ...(body ? { body: JSON.stringify(body) } : {})
    });

    if (res.status === 401) {
      console.warn('Acesso não autorizado');
    }

    if (!res.ok) {
      const err = await res.text().catch(() => res.statusText);
      throw new Error(err || `HTTP ${res.status}`);
    }

    return res.status === 204 ? null : res.json();
  },

  // Auth
  login: (username, password) => API._fetch('POST', '/api/login', { username, password }),

  // Dashboard
  getDashboard: () => API._fetch('GET', '/api/dashboard'),

  // Refeições
  getRefeicoes:    ()       => API._fetch('GET',    '/api/refeicoes'),
  criarRefeicao:   (data)   => API._fetch('POST',   '/api/refeicoes', data),
  atualizarRefeicao: (id, data) => API._fetch('PUT', `/api/refeicoes/${id}`, data),
  deletarRefeicao: (id)     => API._fetch('DELETE', `/api/refeicoes/${id}`),

  // Medição 2H
  registrarMedicao2H: (refId, data) => API._fetch('POST', `/api/refeicoes/${refId}/medicoes2h`, data),

  // Pendências
  getPendencias: () => API._fetch('GET', '/api/pendencias'),

  // Estrelas
  getEstrelas:   () => API._fetch('GET',  '/api/estrelas'),
  addEstrela:    (q) => API._fetch('POST', '/api/estrelas/add',    { quantidade: q }),
  removeEstrela: (q) => API._fetch('POST', '/api/estrelas/remove', { quantidade: q }),

  // Logs
  getLogs: () => API._fetch('GET', '/api/logs'),
};

// Utilidades globais
const UI = {
  toast(msg, type = 'success') {
    let container = document.getElementById('toastContainer');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toastContainer';
      container.className = 'toast-container';
      document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `${type === 'success' ? '✦' : '⚠'} ${msg}`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3500);
  },

  formatDateTime(str) {
    if (!str) return '—';
    const d = new Date(str);
    return d.toLocaleString('pt-BR', { day:'2-digit', month:'2-digit', year:'numeric', hour:'2-digit', minute:'2-digit' });
  },

  formatDate(str) {
    if (!str) return '—';
    return new Date(str).toLocaleDateString('pt-BR');
  },

  statusBadge(status) {
    const map = {
      NORMAL:       '<span class="badge badge-normal">✦ Normal</span>',
      PERIGO_BAIXO: '<span class="badge badge-danger">⬇ Baixa</span>',
      PERIGO_ALTO:  '<span class="badge badge-danger">⬆ Alta</span>',
      SEM_DADO:     '<span class="badge badge-done">— Sem dado</span>',
      PENDENTE:     '<span class="badge badge-pending">⏳ Pendente</span>',
      CONCLUIDO:    '<span class="badge badge-done">✓ Concluído</span>',
    };
    return map[status] || status;
  },

  glicemiaClass(valor) {
    if (!valor && valor !== 0) return '';
    if (valor < 50 || valor > 150) return 'status-danger';
    return 'status-normal';
  },

  // Gera estrelas visuais
  renderStars(count) {
    const filled = Math.min(count, 20);
    return '★'.repeat(filled) + (count > 20 ? ` +${count - 20}` : '');
  },

  requireAuth() {
    return true; // Acesso público liberado
  },

  setupNav() {
    // Preenche info padrão para acesso público
    const nameEl = document.getElementById('sidebarUsername');
    const roleEl = document.getElementById('sidebarRole');
    const avatarEl = document.getElementById('sidebarAvatar');
    if (nameEl) nameEl.textContent = 'Visitante';
    if (roleEl) roleEl.textContent = 'Acesso Público';
    if (avatarEl) avatarEl.textContent = 'V';

    // Sempre mostrar itens admin ou ocultar se preferir (aqui vou mostrar para total transparência)
    document.querySelectorAll('.admin-only').forEach(el => {
      el.style.display = '';
    });

    // Marca item ativo
    const page = window.location.pathname.split('/').pop();
    document.querySelectorAll('.nav-item').forEach(item => {
      if (item.dataset.page === page) item.classList.add('active');
    });

    // Logout removido para acesso público
    const btnLogout = document.getElementById('btnLogout');
    if (btnLogout) btnLogout.style.display = 'none';
  }
};
