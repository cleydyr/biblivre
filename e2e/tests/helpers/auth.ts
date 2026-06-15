import { expect, type Page } from '@playwright/test'

const DEFAULT_USERNAME = process.env.BIBLIVRE_USERNAME ?? 'admin'
const DEFAULT_PASSWORD = process.env.BIBLIVRE_PASSWORD ?? 'abracadabra'
const SCHEMA_STORAGE_KEY = 'biblivre.selectedSchema'

export async function login(page: Page): Promise<void> {
  await page.goto('/spa/login')

  await page.getByRole('textbox', { name: 'Usuário' }).fill(DEFAULT_USERNAME)
  await page.locator('input[name="password"]').fill(DEFAULT_PASSWORD)
  await page
    .getByRole('button', { name: 'Entrar', exact: true })
    .click()

  await expect(page).toHaveURL(/\/spa\/search/)

  await page.waitForFunction(
    (storageKey) => localStorage.getItem(storageKey) !== null,
    SCHEMA_STORAGE_KEY,
  )
}

export async function navigateToReports(page: Page): Promise<void> {
  const administrationNav = page.getByRole('button', { name: 'Administração' })

  if (await administrationNav.isVisible()) {
    await administrationNav.click()
  }

  const reportsNav = page.getByRole('button', {
    name: 'Relatórios personalizados',
  })

  await expect(reportsNav).toBeEnabled()
  await reportsNav.click()

  await expect(page).toHaveURL(/\/spa\/reports/)
  await expect(
    page.getByRole('heading', { name: 'Relatórios personalizados', level: 1 }),
  ).toBeVisible()
}
