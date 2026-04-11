import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

/** Misma regla en dev y preview: el navegador llama al mismo origen y Vite reenvía a Micronaut. */
const toursApiProxy = {
  '/api/tours': {
    target: 'http://localhost:8080',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api\/tours/, '/tours'),
  },
}

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: toursApiProxy,
  },
  // Sin esto, `npm run build` + `vite preview` sirve el front pero /api/tours no existe → "no conectan".
  preview: {
    proxy: toursApiProxy,
  },
})
