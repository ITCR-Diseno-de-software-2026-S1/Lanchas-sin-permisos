import React from 'react'

const LOCATION_COLORS = [
  'from-ocean-800 to-ocean-700',
  'from-ocean-900 to-ocean-800',
  'from-ocean-700 to-ocean-600',
]

function hash(str) {
  let h = 0
  for (let i = 0; i < str.length; i++) h = (h * 31 + str.charCodeAt(i)) | 0
  return Math.abs(h)
}

export default function TourCard({ tour, index }) {
  const grad = LOCATION_COLORS[hash(tour.location) % LOCATION_COLORS.length]

  return (
    <article
      className="card-glass overflow-hidden fade-up hover:border-ocean-500/50 transition-all duration-300 hover:-translate-y-1 hover:shadow-xl hover:shadow-ocean-900/50"
      style={{ animationDelay: `${index * 60}ms` }}
    >
      {/* Color band */}
      <div className={`h-1.5 w-full bg-gradient-to-r ${grad}`} />

      <div className="p-6">
        {/* Header */}
        <div className="flex items-start justify-between gap-3 mb-3">
          <h3 className="font-display text-lg text-ocean-50 leading-snug">{tour.name}</h3>
          <span className="shrink-0 font-mono font-medium text-sand-400 text-base">
            ${Number(tour.price).toFixed(2)}
          </span>
        </div>

        {/* Meta */}
        <div className="flex flex-wrap gap-2 mb-4">
          <span className="tag">
            <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M17.657 16.657L13.414 20.9a2 2 0 01-2.828 0l-4.243-4.243a8 8 0 1111.314 0z" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            {tour.location}
          </span>
          {tour.availableSpots != null && (
            <span className="tag text-sand-400 border-sand-700/40 bg-sand-900/30">
              <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                  d="M17 20h5v-2a4 4 0 00-5.447-3.724M9 20H4v-2a4 4 0 015.447-3.724M15 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              {tour.availableSpots} cupos
            </span>
          )}
        </div>

        {tour.description && (
          <p className="text-sm text-ocean-400 leading-relaxed mb-4 line-clamp-2">
            {tour.description}
          </p>
        )}

        {/* Footer */}
        <div className="flex items-center justify-between pt-3 border-t border-ocean-700/30">
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded-full bg-ocean-600 flex items-center justify-center text-xs font-medium text-white">
              {tour.guideName?.charAt(0)?.toUpperCase()}
            </div>
            <span className="text-xs text-ocean-400">{tour.guideName}</span>
          </div>
          <span className="text-xs text-ocean-600 font-mono">
            {tour.createdAt
              ? new Date(tour.createdAt).toLocaleDateString('es-CR', { day: '2-digit', month: 'short' })
              : '—'}
          </span>
        </div>
      </div>
    </article>
  )
}
