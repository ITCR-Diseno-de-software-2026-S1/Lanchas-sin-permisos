import { useState, useCallback, useEffect } from 'react'
import { fetchTours, createTour } from '../services/toursService'

/**
 * useTours — encapsulates all state & async logic for tours.
 * Components stay pure / presentation-only.
 */
export function useTours() {
  const [tours, setTours]         = useState([])
  const [loading, setLoading]     = useState(false)
  const [error, setError]         = useState(null)
  const [locationFilter, setLocationFilter] = useState('')

  const loadTours = useCallback(async (loc = locationFilter) => {
    setLoading(true)
    setError(null)
    try {
      const data = await fetchTours(loc)
      setTours(data)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [locationFilter])

  useEffect(() => { loadTours('') }, []) // initial load

  const submitTour = useCallback(async (formData) => {
    const tour = await createTour({
      ...formData,
      price: parseFloat(formData.price),
      availableSpots: formData.availableSpots ? parseInt(formData.availableSpots) : null,
    })
    setTours(prev => [tour, ...prev])
    return tour
  }, [])

  return {
    tours,
    loading,
    error,
    locationFilter,
    setLocationFilter,
    loadTours,
    submitTour,
  }
}
