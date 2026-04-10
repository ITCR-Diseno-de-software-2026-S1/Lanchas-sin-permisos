/**
 * toursService.js
 * All HTTP calls to the Tours microservice (port 8080) go through this module.
 * The Vite proxy rewrites /api/tours → http://localhost:8080/tours in dev.
 */

const BASE_URL = '/api/tours'

/**
 * GET /tours[?location=<keyword>]
 * @param {string} location  optional filter
 * @returns {Promise<Array>}
 */
export async function fetchTours(location = '') {
  const url = location ? `${BASE_URL}?location=${encodeURIComponent(location)}` : BASE_URL
  const res = await fetch(url, { headers: { Accept: 'application/json' } })
  if (!res.ok) throw new Error(`Error al cargar tours: ${res.status}`)
  return res.json()
}

/**
 * POST /tours
 * @param {{name, location, price, description, guideName, availableSpots}} data
 * @returns {Promise<Object>} created tour
 */
export async function createTour(data) {
  const res = await fetch(BASE_URL, {
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
