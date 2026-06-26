import path from 'node:path'
import { fileURLToPath } from 'node:url'

import { expect, test } from '@playwright/test'

import { login, navigateToReports } from './helpers/auth'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const reportTemplatePath = path.join(
  __dirname,
  '../fixtures/simple-report.jrxml',
)

test.describe('Reports', () => {
  test('uploads a report template and generates the filled report', async ({
    page,
  }) => {
    const reportName = `E2E Report ${Date.now()}`

    await login(page)
    await navigateToReports(page)

    await page.getByRole('button', { name: 'Novo modelo' }).click()
    await expect(
      page.getByRole('heading', { name: 'Novo modelo de relatório', level: 2 }),
    ).toBeVisible()

    await page.getByLabel('Título do relatório').fill(reportName)
    await page
      .locator('input[type="file"][accept=".jrxml"]')
      .setInputFiles(reportTemplatePath)

    const compileResponse = page.waitForResponse(
      (response) =>
        response.url().includes('/api/v2/report_template') &&
        response.url().includes('compile') &&
        response.request().method() === 'POST' &&
        response.ok(),
    )

    await page.getByRole('button', { name: 'Enviar' }).click()
    await compileResponse

    await expect(page.getByText('Modelo de relatório criado')).toBeVisible()
    await expect(page.getByRole('cell', { name: reportName })).toBeVisible()

    await page
      .locator('tr', { hasText: reportName })
      .getByRole('button', { name: 'Preencher' })
      .click()

    await expect(
      page.getByRole('heading', { name: 'Preencher relatório', level: 2 }),
    ).toBeVisible()
    await expect(
      page.getByText('Este modelo não possui parâmetros'),
    ).toBeVisible()

    const fillResponse = page.waitForResponse(
      (response) =>
        response.url().includes('/api/v2/report_fill') &&
        response.request().method() === 'POST' &&
        response.ok(),
    )

    await page.getByRole('button', { name: 'Gerar relatório' }).click()
    await fillResponse

    await expect(
      page.getByText('Relatório gerado', { exact: true }),
    ).toBeVisible()
    await expect(
      page.getByRole('link', { name: /baixar o relatório gerado/i }),
    ).toBeVisible()
  })
})
