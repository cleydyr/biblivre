import { expect, test, type Locator, type Page, type Response } from '@playwright/test'

import { login, navigateToCirculationUsers } from './helpers/auth'

type CirculationUserSearchParameters = {
  mode: string
  created_start: string
  created_end: string
}

function isCirculationUserAction(action: string) {
  return (response: Response) => {
    if (response.request().method() !== 'POST' || !response.ok()) {
      return false
    }

    const postData = response.request().postData() ?? ''

    return (
      postData.includes('module=circulation.user') &&
      postData.includes(`action=${action}`)
    )
  }
}

function parseCirculationUserSearchParameters(
  postData: string,
): CirculationUserSearchParameters | null {
  const searchParameters = new URLSearchParams(postData).get('search_parameters')

  if (!searchParameters) {
    return null
  }

  return JSON.parse(searchParameters) as CirculationUserSearchParameters
}

async function listAllUsers(page: Page) {
  const searchResponse = page.waitForResponse(
    isCirculationUserAction('search'),
  )

  const listAllButton = page.getByRole('button', {
    name: 'Listar todos os usuários',
  })

  if (await listAllButton.isVisible()) {
    await listAllButton.click()
  } else {
    await page.getByRole('button', { name: 'Pesquisar' }).click()
  }

  await searchResponse
}

async function searchUsers(page: Page, query: string) {
  await page
    .getByPlaceholder('Pesquisar por nome, email, telefone, etc.')
    .fill(query)

  const searchResponse = page.waitForResponse(
    isCirculationUserAction('search'),
  )

  await page.getByRole('button', { name: 'Pesquisar' }).click()
  await searchResponse
}

async function fillCreatedStartDate(page: Page, date: string) {
  const createdStartInput = page
    .locator('.euiFormRow')
    .filter({ hasText: 'Cadastrado entre' })
    .getByRole('textbox')
    .first()

  await createdStartInput.click()
  await createdStartInput.fill(date)
  await createdStartInput.press('Tab')

  return createdStartInput
}

async function confirmModal(page: Page) {
  const modal = page.getByRole('alertdialog')
  await modal.getByRole('button', { name: 'Sim', exact: true }).click()
}

async function clickRowAction(
  page: Page,
  row: Locator,
  actionName: string,
) {
  await row.getByRole('button', { name: /^All actions/ }).click()
  await page.getByRole('menuitem', { name: actionName, exact: true }).click()
}

test.describe('Circulation users', () => {
  test('creates, reads, updates, and deletes a user', async ({ page }) => {
    const timestamp = Date.now()
    const userName = `E2E User ${timestamp}`
    const updatedUserName = `${userName} Updated`
    const userEmail = `e2e-${timestamp}@example.com`

    await login(page)
    await navigateToCirculationUsers(page)
    await listAllUsers(page)

    await page.getByRole('button', { name: 'Novo usuário' }).click()
    await expect(
      page.getByRole('heading', { name: 'Novo usuário', level: 2 }),
    ).toBeVisible()

    await page.getByLabel('Nome').fill(userName)
    await page.getByLabel('Email').fill(userEmail)

    const createResponse = page.waitForResponse(
      isCirculationUserAction('save'),
    )

    await page.getByRole('button', { name: 'Salvar', exact: true }).click()
    await createResponse

    const userRow = page.locator('tr', { hasText: userName })
    await expect(userRow).toBeVisible()

    await expect(
      page.getByRole('heading', { name: 'Detalhes do usuário', level: 2 }),
    ).toBeVisible()
    await expect(page.getByLabel('Detalhes do usuário')).toContainText(userName)
    await page.keyboard.press('Escape')

    await searchUsers(page, userName)
    await expect(userRow).toBeVisible()

    await clickRowAction(page, userRow, 'Editar')
    await expect(
      page.getByRole('heading', { name: 'Editar', level: 2 }),
    ).toBeVisible()

    await page.getByLabel('Nome').fill(updatedUserName)

    const updateResponse = page.waitForResponse(
      isCirculationUserAction('save'),
    )

    await page.getByRole('button', { name: 'Salvar', exact: true }).click()
    await updateResponse

    await page.keyboard.press('Escape')

    const updatedRow = page.locator('tr', { hasText: updatedUserName })
    await expect(updatedRow).toBeVisible()

    await clickRowAction(page, updatedRow, 'Desativar')

    await expect(
      page.getByRole('alertdialog', {
        name: 'Marcar usuário como "inativo"',
      }),
    ).toBeVisible()

    const deactivateResponse = page.waitForResponse(
      isCirculationUserAction('delete'),
    )

    await confirmModal(page)
    await deactivateResponse

    await expect(updatedRow.getByText('Inativo')).toBeVisible()

    await clickRowAction(page, updatedRow, 'Excluir')

    await expect(
      page.getByRole('alertdialog', { name: 'Excluir usuário' }),
    ).toBeVisible()

    const deleteResponse = page.waitForResponse(
      isCirculationUserAction('delete'),
    )

    await confirmModal(page)
    await deleteResponse

    await expect(updatedRow).not.toBeVisible()
  })

  test('searches users with advanced search and created_start filter', async ({
    page,
  }) => {
    const createdStartDate = '01/01/2020'

    await login(page)
    await navigateToCirculationUsers(page)

    await page.getByRole('switch', { name: 'Pesquisa avançada' }).click()
    await expect(page.getByText('Cadastrado entre')).toBeVisible()

    const createdStartInput = await fillCreatedStartDate(page, createdStartDate)
    await expect(createdStartInput).toHaveValue(createdStartDate)

    const searchResponse = page.waitForResponse(
      isCirculationUserAction('search'),
    )

    await page.getByRole('button', { name: 'Pesquisar' }).click()
    const response = await searchResponse

    const searchParameters = parseCirculationUserSearchParameters(
      response.request().postData() ?? '',
    )

    expect(searchParameters).not.toBeNull()
    expect(searchParameters?.mode).toBe('advanced')
    expect(searchParameters?.created_start).toMatch(
      /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/,
    )
    expect(searchParameters?.created_end).toBe('')

    const responseBody = await response.json()
    expect(responseBody.success).toBe(true)
    expect(responseBody.search.record_count).toBeGreaterThan(0)

    await expect(
      page.getByText('Utilize os controles acima para buscar usuários'),
    ).not.toBeVisible()
    await expect(
      page.getByText('Resultados da busca de usuários'),
    ).toBeVisible()
    await expect(page.locator('tbody tr').first()).toBeVisible()
  })
})
