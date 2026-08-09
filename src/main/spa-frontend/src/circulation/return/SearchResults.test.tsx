import { render, screen } from '@testing-library/react'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import SearchResults from './SearchResults'
import { openHoldingLendingBag } from './testFixtures'

describe('SearchResults', () => {
  it('shows an empty state when there are no results', () => {
    render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <SearchResults isBusy={false} results={[]} onReturn={vi.fn()} />
      </IntlProvider>,
    )

    expect(screen.getByText(/Nenhum exemplar encontrado/i)).toBeInTheDocument()
  })

  it('renders a card per Holding result', () => {
    render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <SearchResults
          isBusy={false}
          results={[openHoldingLendingBag()]}
          onReturn={vi.fn()}
        />
      </IntlProvider>,
    )

    expect(
      screen.getByRole('button', { name: /Devolver/i }),
    ).toBeInTheDocument()
    expect(screen.getByText('T-10')).toBeInTheDocument()
  })
})
