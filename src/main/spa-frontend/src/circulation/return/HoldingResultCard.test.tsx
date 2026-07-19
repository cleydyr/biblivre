import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import HoldingResultCard from './HoldingResultCard'
import { closedBag, openHoldingLendingBag } from './testFixtures'

const renderCard = (
  bag: ReturnType<typeof openHoldingLendingBag>,
  onReturn = vi.fn(),
  isBusy = false,
) =>
  render(
    <IntlProvider locale='pt-BR' messages={{}}>
      <HoldingResultCard
        holdingLendingBag={bag}
        isBusy={isBusy}
        onReturn={onReturn}
      />
    </IntlProvider>,
  )

describe('HoldingResultCard', () => {
  it('offers Return for an open Lending', async () => {
    const user = userEvent.setup()
    const onReturn = vi.fn()
    renderCard(openHoldingLendingBag(), onReturn)

    await user.click(screen.getByRole('button', { name: /Devolver/i }))

    expect(onReturn).toHaveBeenCalledWith(55)
  })

  it('shows not-on-loan hint when the Lending is already closed', () => {
    renderCard(closedBag())

    expect(
      screen.getByText(/Exemplar não está emprestado/i),
    ).toBeInTheDocument()
    expect(screen.getByText(/Última devolução: João/i)).toBeInTheDocument()
    expect(
      screen.queryByRole('button', { name: /Devolver/i }),
    ).not.toBeInTheDocument()
  })
})
