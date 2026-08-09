import { render, screen } from '@testing-library/react'
import { IntlProvider } from 'react-intl'
import { describe, expect, it } from 'vitest'

import HoldingLendingSummary from './HoldingLendingSummary'
import { openHoldingLendingBag } from './testFixtures'

describe('HoldingLendingSummary', () => {
  it('shows accession, user, and biblio fields for a Lending bag', () => {
    render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <HoldingLendingSummary holdingLendingBag={openHoldingLendingBag()} />
      </IntlProvider>,
    )

    expect(screen.getByText('T-10')).toBeInTheDocument()
    expect(screen.getByText('João')).toBeInTheDocument()
    expect(screen.getByText('A Hora da Estrela')).toBeInTheDocument()
    expect(screen.getByText('Clarice Lispector')).toBeInTheDocument()
  })
})
