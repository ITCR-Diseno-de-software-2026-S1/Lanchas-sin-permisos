/**
 * toursService.js
 * En desarrollo (npm run dev) el navegador llama directo a Micronaut: evita cuelgues del proxy Vite en Windows.
 * En preview/build se usa /api/tours y el proxy de Vite (vite.config.js).
 */
const BASE_URL = import.meta.env.DEV
  ? 'http://127.0.0.1:8081/tours'
  : '/api/tours'

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
        'No hay conexión con http://127.0.0.1:8081. Abre otra terminal, levanta tours-service y recarga esta página.'
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
