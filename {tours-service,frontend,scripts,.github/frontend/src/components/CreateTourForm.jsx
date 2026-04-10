import React, { useState } from 'react'

const EMPTY = {
  name: '', location: '', price: '', guideName: '',
  description: '', availableSpots: '',
}

export default function CreateTourForm({ onSubmit }) {
  const [form, setForm]       = useState(EMPTY)
  const [busy, setBusy]       = useState(false)
  const [success, setSuccess] = useState(false)
  const [err, setErr]         = useState(null)

  function handle(e) {
    setForm(f => ({ ...f, [e.target.name]: e.target.value }))
  }

  async function submit(e) {
    e.preventDefault()
    setBusy(true); setErr(null); setSuccess(false)
    try {
      await onSubmit(form)
      setSuccess(true)
      setForm(EMPTY)
      setTimeout(() => setSuccess(false), 3000)
    } catch (ex) {
      setErr(ex.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="card-glass p-6">
      <div className="flex items-center gap-3 mb-6">
        <div className="w-8 h-8 rounded-lg bg-ocean-600/40 flex items-center justify-center">
          <svg className="w-4 h-4 text-ocean-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
        </div>
        <h2 className="font-display text-xl text-ocean-50">Publicar Tour</h2>
      </div>

      <form onSubmit={submit} className="space-y-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-medium text-ocean-400 mb-1.5 uppercase tracking-wide">
              Nombre del tour *
            </label>
            <input className="input-field" name="name" value={form.name}
              onChange={handle} placeholder="Ej: Tour Manglares del Tempisque" required />
          </div>
          <div>
            <label className="block text-xs font-medium text-ocean-400 mb-1.5 uppercase tracking-wide">
              Ubicación *
            </label>
            <input className="input-field" name="location" value={form.location}
              onChange={handle} placeholder="Ej: Puntarenas" required />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-medium text-ocean-400 mb-1.5 uppercase tracking-wide">
              Precio (USD) *
            </label>
            <input className="input-field" name="price" type="number" min="0.01"
              step="0.01" value={form.price} onChange={handle}
              placeholder="35.00" required />
          </div>
          <div>
            <label className="block text-xs font-medium text-ocean-400 mb-1.5 uppercase tracking-wide">
              Cupos disponibles
            </label>
            <input className="input-field" name="availableSpots" type="number"
              min="1" value={form.availableSpots} onChange={handle} placeholder="10" />
          </div>
        </div>

        <div>
          <label className="block text-xs font-medium text-ocean-400 mb-1.5 uppercase tracking-wide">
            Nombre del guía *
          </label>
          <input className="input-field" name="guideName" value={form.guideName}
            onChange={handle} placeholder="Ej: Don Carlos Vargas" required />
        </div>

        <div>
          <label className="block text-xs font-medium text-ocean-400 mb-1.5 uppercase tracking-wide">
            Descripción
          </label>
          <textarea className="input-field resize-none" name="description"
            value={form.description} onChange={handle} rows={3}
            placeholder="Describe el recorrido, qué verán, horarios..." />
        </div>

        {err && (
          <p className="text-sm text-coral-400 bg-coral-600/10 border border-coral-600/30 rounded-lg px-4 py-2.5">
            {err}
          </p>
        )}

        {success && (
          <p className="text-sm text-ocean-300 bg-ocean-700/30 border border-ocean-600/40 rounded-lg px-4 py-2.5">
            ✓ Tour publicado exitosamente
          </p>
        )}

        <button type="submit" className="btn-primary w-full" disabled={busy}>
          {busy ? 'Publicando...' : 'Publicar tour'}
        </button>
      </form>
    </div>
  )
}
