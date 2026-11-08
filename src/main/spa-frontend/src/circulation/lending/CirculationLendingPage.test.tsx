import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import * as circulationApi from '../../api-helpers/circulation'
import * as lendingApi from '../../api-helpers/lending'
import { ToastProvider } from '../../toasts/ToastProvider'
import {
  holdingFixture,
  openHoldingLendingBag,
  unpaidFine,
} from '../return/testFixtures'

import CirculationLendingPage from '.'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'
import type {
  LendingHoldingSearchResponse,
  LendingReader,
  LendingUserSearchResponse,
} from '../../api-helpers/lending/response-types'

vi.mock('../../api-helpers/lending', () => ({
  searchReadersForLending: vi.fn(),
  searchHoldingsForReturn: vi.fn(),
  lendHolding: vi.fn(),
  renewLending: vi.fn(),
  returnLendingImmediate: vi.fn(),
  printLendingReceipt: vi.fn(),
  undoLendingReturn: vi.fn(),
  payLendingFine: vi.fn(),
  adjustLendingFine: vi.fn(),
}))

vi.mock('../../api-helpers/circulation', async (importOriginal) => ({
  ...(await importOriginal<typeof circulationApi>()),
  getCirculationUserTabData: vi.fn(),
}))

vi.mock('../../api-helpers/user-type/hooks', () => ({
  useUserType: (id: number | undefined) => ({
    data:
      id === undefined
        ? undefined
        : { id, name: 'Padrão', lendingLimit: 2, lendingTimeLimit: 7 },
  }),
}))

const api = vi.mocked(lendingApi)
const tabData = vi.mocked(circulationApi.getCirculationUserTabData)

const paged = <T,>(data: T[]) => ({
  success: true as const,
  search: {
    id: 1,
    record_count: data.length,
    record_limit: 100,
    records_per_page: 25,
    page: 1,
    page_count: 1,
    time: 0,
    data,
  },
})

const readerRow = (): LendingReader => {
  const bag = openHoldingLendingBag()

  return {
    id: bag.user!.id,
    user: bag.user!,
    lendingInfo: [{ ...bag, user: undefined }],
  }
}

const availableBag = (id = 20, accession = `T-${id}`): HoldingLendingBag => ({
  ...openHoldingLendingBag(),
  id,
  holding: holdingFixture({ id, accession_number: accession }),
  biblio: { ...openHoldingLendingBag().biblio!, title: `Livro ${id}` },
  user: undefined,
  lending: undefined,
})

const lentBag = (bag: HoldingLendingBag, lendingId = 900) => ({
  ...bag,
  user: readerRow().user,
  lending: {
    ...openHoldingLendingBag().lending!,
    id: lendingId,
    holdingId: bag.holding.id,
    expectedReturnDate: openHoldingLendingBag().lending!.expectedReturnDate,
  },
})

const renderPage = () =>
  render(
    <QueryClientProvider
      client={
        new QueryClient({ defaultOptions: { queries: { retry: false } } })
      }
    >
      <IntlProvider locale='pt-BR' messages={{}}>
        <ToastProvider>
          <CirculationLendingPage />
        </ToastProvider>
      </IntlProvider>
    </QueryClientProvider>,
  )

const selectReader = async (user: ReturnType<typeof userEvent.setup>) => {
  await user.type(
    screen.getByRole('searchbox', { name: 'Buscar leitor' }),
    'joão{Enter}',
  )
  await user.click(
    await screen.findByRole('button', { name: 'Selecionar João' }),
  )
}

const searchHoldings = async (
  user: ReturnType<typeof userEvent.setup>,
  query: string,
) => {
  await user.type(
    screen.getByRole('searchbox', { name: 'Buscar exemplar' }),
    `${query}{Enter}`,
  )
}

const holdingCard = async (title: string) => {
  const titleElement = await screen.findByText(title)
  const card = titleElement.closest('.euiPanel')

  if (!(card instanceof HTMLElement)) {
    throw new Error(`card for ${title} not found`)
  }

  return card
}

describe('CirculationLendingPage', () => {
  beforeEach(() => {
    api.searchReadersForLending.mockResolvedValue(
      paged([readerRow()]) as LendingUserSearchResponse,
    )
    tabData.mockResolvedValue({
      success: true,
      data: { data: [unpaidFine()] },
    } as Awaited<ReturnType<typeof circulationApi.getCirculationUserTabData>>)
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  it('searches readers on Enter and shows the selected reader', async () => {
    const user = userEvent.setup()
    renderPage()

    await selectReader(user)

    expect(api.searchReadersForLending).toHaveBeenCalledWith({
      query: 'joão',
      page: 1,
    })
    expect(
      screen.getByRole('heading', { name: 'João', level: 3 }),
    ).toBeInTheDocument()
    expect(screen.getByText('1 de 2')).toBeInTheDocument()
    expect(await screen.findByText(/2,50/)).toBeInTheDocument()
  })

  it('lends a holding and updates the reader lendings', async () => {
    const user = userEvent.setup()
    const bag = availableBag(20, 'T-20')
    api.searchHoldingsForReturn.mockResolvedValue(
      paged([bag]) as LendingHoldingSearchResponse,
    )
    api.lendHolding.mockResolvedValue({
      success: true,
      message: 'Empréstimo realizado com sucesso',
      data: lentBag(bag),
    })
    renderPage()

    await selectReader(user)
    await searchHoldings(user, 'livro')
    await user.click(
      within(await holdingCard('Livro 20')).getByRole('button', {
        name: 'Emprestar',
      }),
    )

    expect(api.lendHolding).toHaveBeenCalledWith({ holdingId: 20, userId: 3 })
    expect(
      await screen.findByText('Empréstimo realizado com sucesso'),
    ).toBeInTheDocument()
    expect(screen.getByText('2 de 2')).toBeInTheDocument()
    expect(
      screen.getByText('Limite de 2 empréstimos atingido'),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('button', { name: 'Imprimir recibo (1)' }),
    ).toBeEnabled()
  })

  it('shows the server refusal and keeps the holding available', async () => {
    const user = userEvent.setup()
    const bag = availableBag(20)
    api.searchHoldingsForReturn.mockResolvedValue(
      paged([bag]) as LendingHoldingSearchResponse,
    )
    api.lendHolding.mockResolvedValue({
      success: false,
      message_level: 'warning',
      message: 'O exemplar não está disponível',
    })
    renderPage()

    await selectReader(user)
    await searchHoldings(user, 'livro')
    await user.click(
      within(await holdingCard('Livro 20')).getByRole('button', {
        name: 'Emprestar',
      }),
    )

    expect(
      await screen.findByText('O exemplar não está disponível'),
    ).toBeInTheDocument()
    expect(screen.getByText('1 de 2')).toBeInTheDocument()
    expect(
      within(await holdingCard('Livro 20')).getByText('Disponível'),
    ).toBeInTheDocument()
  })

  it('lends right away when a scanned accession number matches one holding', async () => {
    const user = userEvent.setup()
    const bag = availableBag(21, 'T-21')
    api.searchHoldingsForReturn.mockResolvedValue(
      paged([bag]) as LendingHoldingSearchResponse,
    )
    api.lendHolding.mockResolvedValue({ success: true, data: lentBag(bag) })
    renderPage()

    await selectReader(user)
    await searchHoldings(user, 'T-21')

    await waitFor(() => {
      expect(api.lendHolding).toHaveBeenCalledWith({
        holdingId: 21,
        userId: 3,
      })
    })
  })

  it('prints the receipt with the session lendings', async () => {
    const user = userEvent.setup()
    const bag = availableBag(20, 'T-20')
    const printWindow = {
      document: { write: vi.fn(), close: vi.fn() },
      focus: vi.fn(),
      print: vi.fn(),
    }
    vi.spyOn(window, 'open').mockReturnValue(printWindow as unknown as Window)
    api.searchHoldingsForReturn.mockResolvedValue(
      paged([bag]) as LendingHoldingSearchResponse,
    )
    api.lendHolding.mockResolvedValue({
      success: true,
      data: lentBag(bag, 901),
    })
    api.printLendingReceipt.mockResolvedValue({
      success: true,
      receipt: '<p>recibo</p>',
    })
    renderPage()

    await selectReader(user)
    await searchHoldings(user, 'T-20')
    await user.click(
      await screen.findByRole('button', { name: 'Imprimir recibo (1)' }),
    )

    expect(api.printLendingReceipt).toHaveBeenCalledWith([901])
    await waitFor(() => {
      expect(printWindow.print).toHaveBeenCalled()
    })
    expect(printWindow.document.write).toHaveBeenCalledWith('<p>recibo</p>')
  })

  it('disables renewal of late lendings', async () => {
    const user = userEvent.setup()
    const row = readerRow()
    api.searchReadersForLending.mockResolvedValue(
      paged([
        {
          ...row,
          lendingInfo: row.lendingInfo?.map((bag) => ({
            ...bag,
            lending: { ...bag.lending!, daysLate: 4 },
          })),
        },
      ]) as LendingUserSearchResponse,
    )
    renderPage()

    await selectReader(user)

    expect(screen.getByText('4 dias de atraso')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Renovar' })).toBeDisabled()
  })
})
