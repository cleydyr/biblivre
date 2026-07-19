import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import LastReturnPanel from './LastReturnPanel'
import {
  nextReservationFixture,
  openHoldingLendingBag,
  unpaidFine,
} from './testFixtures'

describe('LastReturnPanel', () => {
  it('exposes Fine quick actions, undo, and next Reservation', async () => {
    const user = userEvent.setup()
    const onPayFine = vi.fn()
    const onUndo = vi.fn()
    const setAdjustFineId = vi.fn()
    const setAdjustValue = vi.fn()
    const returned = openHoldingLendingBag()
    returned.lendingFine = unpaidFine()

    render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <LastReturnPanel
          undoStillAvailable
          adjustFineId={null}
          adjustValue=''
          isBusy={false}
          lastReturn={{
            holdingLendingBag: returned,
            undoAvailableUntil: Date.now() + 60_000,
            nextReservation: nextReservationFixture(),
          }}
          setAdjustFineId={setAdjustFineId}
          setAdjustValue={setAdjustValue}
          onAdjustFine={vi.fn()}
          onPayFine={onPayFine}
          onUndo={onUndo}
        />
      </IntlProvider>,
    )

    expect(
      screen.getByText(/Multa gerada automaticamente/i),
    ).toBeInTheDocument()
    expect(
      screen.getByText(/Próximo na fila: Maria Reserva/i),
    ).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: /^Pagar$/i }))
    expect(onPayFine).toHaveBeenCalledWith(9)

    await user.click(screen.getByRole('button', { name: /Dispensar/i }))
    expect(onPayFine).toHaveBeenCalledWith(9, true)

    await user.click(screen.getByRole('button', { name: /Ajustar/i }))
    expect(setAdjustFineId).toHaveBeenCalledWith(9)
    expect(setAdjustValue).toHaveBeenCalledWith('2.5')

    await user.click(screen.getByRole('button', { name: /Desfazer/i }))
    expect(onUndo).toHaveBeenCalled()
  })

  it('shows the Fine adjust field when adjustFineId matches', async () => {
    const user = userEvent.setup()
    const onAdjustFine = vi.fn()
    const returned = openHoldingLendingBag()
    returned.lendingFine = unpaidFine()

    render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <LastReturnPanel
          adjustFineId={9}
          adjustValue='1.5'
          isBusy={false}
          lastReturn={{ holdingLendingBag: returned }}
          setAdjustFineId={vi.fn()}
          setAdjustValue={vi.fn()}
          undoStillAvailable={false}
          onAdjustFine={onAdjustFine}
          onPayFine={vi.fn()}
          onUndo={vi.fn()}
        />
      </IntlProvider>,
    )

    await user.click(screen.getByRole('button', { name: /Salvar valor/i }))
    expect(onAdjustFine).toHaveBeenCalled()
  })
})
