// config.js — Configuração da URL da API
const CONFIG = {
  // Em produção, troque para a URL do seu backend no Render/Railway
  API_URL: window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
    ? 'http://localhost:8080'
    : 'https://projeto-glicemia.onrender.com'  // ← Substituir após deploy
};
// Forcing update v2
