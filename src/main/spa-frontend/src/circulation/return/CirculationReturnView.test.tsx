import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import CirculationReturnView from './CirculationReturnView'
import { openHoldingLendingBag } from './testFixtures'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'

import type { LastReturnState, ReturnSlipItem } from './useCirculationReturn'

const defaultProps = () => ({
  query: '',
  setQuery: vi.fn(),
  results: null as HoldingLendingBag[] | null,
  slip: [] as ReturnSlipItem[],
  lastReturn: null as LastReturnState | null,
  searchInputRef: { current: null },
  isBusy: false,
  undoStillAvailable: false,
  adjustFineId: null as number | null,
  setAdjustFineId: vi.fn(),
  adjustValue: '',
  setAdjustValue: vi.fn(),
  onSearch: vi.fn(),
  onReturn: vi.fn(),
  onUndo: vi.fn(),
  onPayFine: vi.fn(),
  onAdjustFine: vi.fn(),
  onPrintSlip: vi.fn(),
  onClearSlip: vi.fn(),
})

const renderView = (
  overrides: Partial<ReturnType<typeof defaultProps>> = {},
) => {
  const props = { ...defaultProps(), ...overrides }

  return {
    props,
    ...render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <CirculationReturnView {...props} />
      </IntlProvider>,
    ),
  }
}

describe('CirculationReturnView', () => {
  it('submits search when Enter is pressed in the search field', async () => {
    const user = userEvent.setup()
    const { props } = renderView({ query: 'T-10' })

    await user.type(screen.getByRole('searchbox'), '{Enter}')

    expect(props.onSearch).toHaveBeenCalled()
  })

  it('composes search results and the Return slip', () => {
    renderView({
      results: [openHoldingLendingBag()],
      slip: [
        {
          lendingId: 55,
          bag: openHoldingLendingBag(),
          returnedAt: Date.now(),
        },
      ],
    })

    expect(
      screen.getByRole('button', { name: /Devolver/i }),
    ).toBeInTheDocument()
    expect(
      screen.getByRole('heading', { name: /Comprovante da visita/i }),
    ).toBeInTheDocument()
  })
})
