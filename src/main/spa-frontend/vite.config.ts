import react from '@vitejs/plugin-react-swc'
import { defineConfig } from 'vite'

import { formatjsI18nPlugin } from './scripts/formatjs-vite-plugin.ts'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    formatjsI18nPlugin(),
    react({
      jsxImportSource: '@emotion/react',
    }),
  ],
  server: {
    port: 5173,
    strictPort: true,
    cors: true,
    // Spring Boot serves the HTML at :8090/spa/* and loads modules from this dev server.
    origin: 'http://localhost:8090',
  },
  build: {
    outDir: '../resources/META-INF/resources/static/spa',
    emptyOutDir: true,
    manifest: true,
    rollupOptions: {
      output: {
        // Create predictable entry point filename
        entryFileNames: 'assets/index-[hash].js',
        chunkFileNames: 'assets/[name]-[hash].js',
        assetFileNames: 'assets/[name]-[hash].[ext]',
      },
    },
  },
})
