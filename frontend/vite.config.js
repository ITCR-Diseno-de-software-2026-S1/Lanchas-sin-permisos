import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

/** Misma regla en dev y preview: el navegador llama al mismo origen y Vite reenvía a Micronaut. */
const toursApiProxy = {
  '/api/tours': {
    // 127.0.0.1 evita colgues en Windows cuando "localhost" resuelve a ::1 y el JVM escucha solo en IPv4
    target: 'http://127.0.0.1:8081',
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
