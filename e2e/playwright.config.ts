import { defineConfig, devices } from '@playwright/test'

const baseURL =
  process.env.BIBLIVRE_BASE_URL ?? 'http://localhost:8090'

const showBrowser =
  process.env.HEADED === '1' ||
  process.env.HEADED === 'true' ||
  process.env.SHOW_BROWSER === '1' ||
  process.env.SHOW_BROWSER === 'true'

export default defineConfig({
  testDir: './tests',
  fullyParallel: false,
  forbidOnly: Boolean(process.env.CI),
  retries: process.env.CI ? 2 : 0,
  workers: 1,
  timeout: 120_000,
  expect: {
    timeout: 30_000,
  },
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL,
    headless: !showBrowser,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    locale: 'pt-BR',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
})
