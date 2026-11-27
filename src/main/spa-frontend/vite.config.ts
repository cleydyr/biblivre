import { sentryVitePlugin } from '@sentry/vite-plugin'
import react from '@vitejs/plugin-react-swc'
import { defineConfig } from 'vite'

import { formatjsI18nPlugin } from './scripts/formatjs-vite-plugin.ts'

// Source maps are uploaded to GlitchTip (Sentry API) only when a token is set,
// e.g. in the release pipeline; they are deleted afterwards so they are never served.
// See docs/client-error-monitoring.md.
const uploadSourceMaps = Boolean(process.env.SENTRY_AUTH_TOKEN)

// https://vite.dev/config/
export default defineConfig(({ command }) => ({
  plugins: [
    formatjsI18nPlugin(),
    react({
      jsxImportSource: '@emotion/react',
    }),
    uploadSourceMaps &&
      sentryVitePlugin({
        url: process.env.SENTRY_URL,
        org: process.env.SENTRY_ORG,
        project: process.env.SENTRY_PROJECT,
        authToken: process.env.SENTRY_AUTH_TOKEN,
        telemetry: false,
        // Must match the release the server reports (ClientMonitoringService).
        release: { name: process.env.BIBLIVRE_RELEASE, inject: false },
        sourcemaps: { filesToDeleteAfterUpload: ['**/*.map'] },
      }),
  ],
  // Relative base so hashed chunks resolve next to the script URL under any
  // servlet context path the container assigns at deploy time.
  base: command === 'build' ? './' : '/',
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
    sourcemap: uploadSourceMaps ? 'hidden' : false,
    rollupOptions: {
      output: {
        // Create predictable entry point filename
        entryFileNames: 'assets/index-[hash].js',
        chunkFileNames: 'assets/[name]-[hash].js',
        assetFileNames: 'assets/[name]-[hash].[ext]',
      },
    },
  },
}))
