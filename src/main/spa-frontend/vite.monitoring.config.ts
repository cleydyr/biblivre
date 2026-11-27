import { defineConfig } from 'vite'

/**
 * Builds the error monitoring script for the legacy JSP pages as a classic
 * script (they have no bundler). Loaded by WEB-INF/jsp/taglib/layout/head/start.jsp.
 */
export default defineConfig({
  define: {
    'process.env.NODE_ENV': JSON.stringify('production'),
  },
  publicDir: false,
  build: {
    outDir: '../resources/META-INF/resources/static/scripts',
    emptyOutDir: false,
    target: 'es2018',
    minify: true,
    lib: {
      entry: 'src/monitoring/legacy-entry.ts',
      formats: ['iife'],
      name: 'BiblivreMonitoringBundle',
      fileName: () => 'biblivre.monitoring.js',
    },
  },
})
