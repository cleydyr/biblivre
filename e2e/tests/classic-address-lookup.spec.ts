import { expect, test, type Page, type Response } from '@playwright/test'

import { login, navigateToClassicCirculationUsers } from './helpers/auth'

type ClassicCirculationInput = {
  schema: string
  addressLookupEnabled: boolean
}

function isAddressLookupResponse(response: Response) {
  return (
    response.request().method() === 'GET' &&
    response.url().includes('/api/v2/circulation/address_lookup/')
  )
}

async function readClassicCirculationInput(page: Page) {
  return page.evaluate(() => {
    const circulationInput = (
      window as unknown as { CirculationInput?: ClassicCirculationInput }
    ).CirculationInput

    if (!circulationInput) {
      return null
    }

    return {
      schema: circulationInput.schema,
      addressLookupEnabled: circulationInput.addressLookupEnabled,
    }
  })
}

test.describe('Classic circulation address lookup', () => {
  test('sends X-Biblivre-Schema so CEP lookup is authorized', async ({
    page,
  }) => {
    await login(page)
    await navigateToClassicCirculationUsers(page)

    const circulationInput = await readClassicCirculationInput(page)

    expect(circulationInput).not.toBeNull()
    expect(circulationInput?.addressLookupEnabled).toBe(true)
    expect(circulationInput?.schema).toBeTruthy()

    await page.locator('a.new_record_button').click()
    await expect(page.locator('input[name="address_zip"]')).toBeVisible()

    await page.locator('input[name="address_zip"]').fill('01310100')

    const lookupResponsePromise = page.waitForResponse(isAddressLookupResponse)

    await page.locator('a.address_lookup_button').click()

    const lookupResponse = await lookupResponsePromise
    const schemaHeader =
      lookupResponse.request().headers()['x-biblivre-schema']

    expect(schemaHeader).toBe(circulationInput?.schema)
    expect(lookupResponse.status()).not.toBe(403)
  })
})
