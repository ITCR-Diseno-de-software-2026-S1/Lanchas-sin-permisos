/**
 * toursService.js
 * Siempre mismo origen que Vite: /api/tours → proxy a Micronaut (vite.config.js).
 * Evita CORS: llamar a :8081 desde :3000 falla en el navegador como TypeError ("Failed to fetch").
 */
const BASE_URL = '/api/tours'

/** Primer arranque del JAR (Hibernate/H2) puede tardar en PCs lentos o con antivirus escaneando el JAR. */
const REQUEST_MS = 60000

async function fetchWithTimeout(url, options = {}) {
  const ctrl = new AbortController()
  const t = setTimeout(() => ctrl.abort(), REQUEST_MS)
  try {
    return await fetch(url, { ...options, signal: ctrl.signal })
  } catch (e) {
    if (e.name === 'AbortError') {
      throw new Error(
        'La petición tardó demasiado. Arranca el backend (puerto 8081): en tours-service ejecuta mvnw.cmd package -DskipTests y luego java -jar target\\tours-service-1.0.0-all.jar'
      )
    }
    if (e instanceof TypeError) {
      throw new Error(
        'No se pudo llegar a la API (proxy /api/tours → :8081). Arranca tours-service en 8081 y usa npm run dev o npm run preview.'
      )
    }
    throw e
  } finally {
    clearTimeout(t)
  }
}

/**
 * GET /tours[?location=<keyword>]
 * @param {string} location  optional filter
 * @returns {Promise<Array>}
 */
export async function fetchTours(location = '') {
  const url = location ? `${BASE_URL}?location=${encodeURIComponent(location)}` : BASE_URL
  const res = await fetchWithTimeout(url, { headers: { Accept: 'application/json' } })
  if (!res.ok) throw new Error(`Error al cargar tours: ${res.status}`)
  return res.json()
}

/**
 * POST /tours
 * @param {{name, location, price, description, guideName, availableSpots}} data
 * @returns {Promise<Object>} created tour
 */
export async function createTour(data) {
  const res = await fetchWithTimeout(BASE_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    body: JSON.stringify(data),
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({}))
    throw new Error(err.message || `Error al crear tour: ${res.status}`)
  }
  return res.json()
}
