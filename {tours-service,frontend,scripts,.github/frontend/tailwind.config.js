/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        display: ['"Playfair Display"', 'Georgia', 'serif'],
        body: ['"DM Sans"', 'sans-serif'],
        mono: ['"DM Mono"', 'monospace'],
      },
      colors: {
        ocean: {
          950: '#020c14',
          900: '#041824',
          800: '#072d40',
          700: '#0a4360',
          600: '#0d5a80',
          500: '#1078a8',
          400: '#2196c4',
          300: '#4ab4d8',
          200: '#80cfe8',
          100: '#b8e5f5',
          50:  '#e8f7fd',
        },
        sand: {
          900: '#5c4a2a',
          800: '#7a6236',
          700: '#9a7c43',
          600: '#b89552',
          500: '#d4ac5e',
          400: '#e8c478',
          300: '#f0d49a',
          200: '#f5e3bc',
          100: '#faf1dd',
          50:  '#fdfaf3',
        },
        coral: {
          600: '#c44a2a',
          500: '#e05c36',
          400: '#f07050',
        }
      },
    },
  },
  plugins: [],
}
