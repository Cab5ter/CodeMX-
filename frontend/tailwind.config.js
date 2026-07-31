export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      screens: {
        // Teléfonos angostos (iPhone SE ≈ 375px) contra el resto. Por debajo de este
        // ancho las rejillas de dos columnas se apilan.
        xs: '420px'
      }
    }
  },
  plugins: []
}
