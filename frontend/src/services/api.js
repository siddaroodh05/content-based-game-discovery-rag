const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/$/, '');

async function request(path, options = {}) {
  let response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers: { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...options.headers },
    });
  } catch {
    throw new Error('Could not reach GameSense. Check that the backend is running and the API URL is correct.');
  }
  if (!response.ok) {
    const detail = await response.text();
    throw new Error(detail || `Request failed (${response.status}).`);
  }
  return response.json();
}

const post = (path, data) => request(path, { method: 'POST', body: JSON.stringify(data) });

export const api = {
  games: (page = 0) => request(`/games/cards?page=${page}`),
  gameMetadata: (parentAsin) => post('/games/metadata', { ParentAsin: parentAsin }),
  reviews: (userId, page = 0) => post(`/games/user/reviews?page=${page}`, { userId }),
  login: (userId) => post('/user/login', { userId }),
  customSearch: (query) => post('/games/query', { query }),
  recommendations: (userId, mode) => post(`/gamesense/games/${mode}_recommendation`, { userId }),
};

export function unwrapPage(payload) {
  if (Array.isArray(payload)) return { items: payload, totalPages: 1, totalElements: payload.length };
  return {
    items: payload?.content || [],
    totalPages: payload?.totalPages || 0,
    totalElements: payload?.totalElements || 0,
  };
}
