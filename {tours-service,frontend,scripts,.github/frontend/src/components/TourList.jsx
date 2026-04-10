import React from 'react'
import TourCard from './TourCard'

export default function TourList({ tours, loading, error, onRetry }) {
  if (loading) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {Array.from({ length: 3 }).map((_, i) => (
          <div key={i} className="card-glass h-48 animate-pulse">
            <div className="h-1.5 bg-ocean-700/60 rounded-t-2xl" />
            <div className="p-6 space-y-3">
              <div className="h-4 bg-ocean-700/50 rounded w-3/4" />
              <div className="h-3 bg-ocean-800/60 rounded w-1/2" />
              <div className="h-3 bg-ocean-800/60 rounded w-5/6" />
            </div>
          </div>
        ))}
      </div>
    )
  }

  if (error) {
    return (
      <div className="text-center py-16">
        <p className="text-coral-400 mb-4 text-sm">{error}</p>
        <button className="btn-secondary text-sm" onClick={onRetry}>Reintentar</button>
      </div>
    )
  }

  if (tours.length === 0) {
    return (
      <div className="text-center py-20">
        <div className="w-16 h-16 rounded-2xl bg-ocean-800/60 flex items-center justify-center mx-auto mb-4">
          <svg className="w-8 h-8 text-ocean-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
              d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
          </svg>
        </div>
        <p className="text-ocean-500 font-body text-sm">No hay tours disponibles aún.</p>
        <p className="text-ocean-600 text-xs mt-1">¡Sé el primero en publicar uno!</p>
      </div>
    )
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      {tours.map((tour, i) => (
        <TourCard key={tour.id} tour={tour} index={i} />
      ))}
    </div>
  )
}
