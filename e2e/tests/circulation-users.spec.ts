import { expect, test, type Locator, type Page, type Response } from '@playwright/test'

import { login, navigateToCirculationUsers } from './helpers/auth'

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
})
