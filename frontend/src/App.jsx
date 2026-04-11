import React, { useState } from 'react'
import CreateTourForm from './components/CreateTourForm'
import TourList from './components/TourList'
import { useTours } from './hooks/useTours'

export default function App() {
  const { tours, loading, error, locationFilter, setLocationFilter, loadTours, submitTour } = useTours()
  const [showForm, setShowForm] = useState(false)

  function handleSearch(e) {
    e.preventDefault()
    loadTours(locationFilter)
  }

  return (
    <div className="min-h-screen">
      {/* ── Hero Header ──────────────────────────────────────────────────── */}
      <header className="relative overflow-hidden border-b border-ocean-800/50 pb-10 pt-12">
        {/* Background waves */}
        <svg className="absolute bottom-0 left-0 w-full opacity-10 wave-slow" viewBox="0 0 1440 80" preserveAspectRatio="none">
          <path d="M0,40 C360,80 720,0 1080,40 C1260,60 1350,50 1440,40 L1440,80 L0,80 Z" fill="currentColor" className="text-ocean-400" />
        </svg>
        <svg className="absolute bottom-0 left-0 w-full opacity-6 wave-med" viewBox="0 0 1440 60" preserveAspectRatio="none">
          <path d="M0,30 C480,60 960,0 1440,30 L1440,60 L0,60 Z" fill="currentColor" className="text-ocean-300" />
        </svg>

        <div className="relative max-w-6xl mx-auto px-4 sm:px-6">
          <div className="flex items-center justify-between">
            <div>
              <div className="flex items-center gap-2 mb-2">
                <span className="tag text-sand-400 border-sand-700/40 bg-sand-900/20">
                  🚤 Tours locales
                </span>
              </div>
              <h1 className="font-display text-4xl sm:text-5xl text-ocean-50 leading-tight">
                Lancha Tours
              </h1>
              <p className="text-ocean-400 mt-2 font-body text-sm sm:text-base max-w-md">
                Guías locales de la comunidad comparten sus rutas en lancha. Sin agencias, directo con quien conoce el territorio.
              </p>
            </div>

            {/* Service badge */}
            <div className="hidden md:flex flex-col items-end gap-1.5">
              <div className="tag text-ocean-400">
                <span className="w-1.5 h-1.5 rounded-full bg-green-400 inline-block animate-pulse" />
                tours-service :8081
              </div>
              <span className="text-xs text-ocean-600 font-mono">Micronaut + H2</span>
            </div>
          </div>
        </div>
      </header>

      {/* ── Main ──────────────────────────────────────────────────────────── */}
      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-10 space-y-10">

        {/* Search + toggle form */}
        <div className="flex flex-col sm:flex-row gap-3">
          <form onSubmit={handleSearch} className="flex gap-2 flex-1">
            <input
              className="input-field flex-1"
              placeholder="Filtrar por ubicación…"
              value={locationFilter}
              onChange={e => setLocationFilter(e.target.value)}
            />
            <button type="submit" className="btn-secondary shrink-0">Buscar</button>
            {locationFilter && (
              <button type="button" className="btn-secondary shrink-0"
                onClick={() => { setLocationFilter(''); loadTours('') }}>
                ✕
              </button>
            )}
          </form>
          <button
            className={showForm ? 'btn-secondary shrink-0' : 'btn-primary shrink-0'}
            onClick={() => setShowForm(v => !v)}
          >
            {showForm ? 'Cancelar' : '+ Publicar tour'}
          </button>
        </div>

        {/* Create form */}
        {showForm && (
          <CreateTourForm onSubmit={async (data) => {
            await submitTour(data)
            setShowForm(false)
          }} />
        )}

        {/* Tour count */}
        <div className="flex items-center justify-between">
          <h2 className="font-display text-xl text-ocean-200">
            Tours disponibles
            {!loading && (
              <span className="ml-2 font-mono text-sm text-ocean-500 font-normal">
                ({tours.length})
              </span>
            )}
          </h2>
          <button
            className="text-xs text-ocean-500 hover:text-ocean-300 transition-colors font-mono"
            onClick={() => loadTours(locationFilter)}
          >
            ↻ actualizar
          </button>
        </div>

        {/* List */}
        <TourList tours={tours} loading={loading} error={error} onRetry={() => loadTours(locationFilter)} />
      </main>

      {/* ── Footer ────────────────────────────────────────────────────────── */}
      <footer className="border-t border-ocean-800/40 mt-16 py-8 text-center">
        <p className="text-xs text-ocean-700 font-mono">
          lancha-tours-monorepo · Micronaut 4 · React 18 · Tailwind 3
        </p>
      </footer>
    </div>
  )
}
