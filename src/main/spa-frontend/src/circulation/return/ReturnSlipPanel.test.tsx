import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import ReturnSlipPanel from './ReturnSlipPanel'
import { openHoldingLendingBag } from './testFixtures'

describe('ReturnSlipPanel', () => {
  it('disables print/clear when the slip is empty', () => {
    render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <ReturnSlipPanel
          isBusy={false}
          slip={[]}
          onClearSlip={vi.fn()}
          onPrintSlip={vi.fn()}
        />
      </IntlProvider>,
    )

    expect(screen.getByRole('button', { name: /Imprimir/i })).toBeDisabled()
    expect(screen.getByRole('button', { name: /Limpar/i })).toBeDisabled()
    expect(
      screen.getByText(/As devoluções desta visita aparecem aqui/i),
    ).toBeInTheDocument()
  })

  it('lists closed Returns and wires print/clear actions', async () => {
    const user = userEvent.setup()
    const onPrintSlip = vi.fn()
    const onClearSlip = vi.fn()

    render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <ReturnSlipPanel
          isBusy={false}
          slip={[
            {
              lendingId: 55,
              bag: openHoldingLendingBag(),
              returnedAt: Date.now(),
            },
          ]}
          onClearSlip={onClearSlip}
          onPrintSlip={onPrintSlip}
        />
      </IntlProvider>,
    )

    expect(screen.getByText(/T-10/)).toBeInTheDocument()
    expect(screen.getByText(/A Hora da Estrela/)).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: /Imprimir/i }))
    expect(onPrintSlip).toHaveBeenCalled()

    await user.click(screen.getByRole('button', { name: /Limpar/i }))
    expect(onClearSlip).toHaveBeenCalled()
  })
})
