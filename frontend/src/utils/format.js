export function ratingLabel(rating) {
  const value = Number(rating);
  return Number.isFinite(value) && value > 0 ? value.toFixed(1) : '—';
}

export function truncate(value = '', limit = 120) {
  return value.length > limit ? `${value.slice(0, limit).trimEnd()}…` : value;
}
