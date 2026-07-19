import { render, screen } from '@testing-library/react'
import { IntlProvider } from 'react-intl'
import { describe, expect, it } from 'vitest'

import NextReservationNotice from './NextReservationNotice'
import { nextReservationFixture } from './testFixtures'

describe('NextReservationNotice', () => {
  it('names the next User in the Reservation queue', () => {
    render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <NextReservationNotice reservation={nextReservationFixture()} />
      </IntlProvider>,
    )

    expect(screen.getByText(/Título reservado/i)).toBeInTheDocument()
    expect(
      screen.getByText(/Próximo na fila: Maria Reserva/i),
    ).toBeInTheDocument()
  })
})
