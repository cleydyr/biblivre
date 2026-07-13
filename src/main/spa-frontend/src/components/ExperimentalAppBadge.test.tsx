import { render, screen } from '@testing-library/react'
import { IntlProvider } from 'react-intl'
import { describe, expect, it } from 'vitest'

import ExperimentalAppBadge from './ExperimentalAppBadge'

function renderBadge() {
  return render(
    <IntlProvider locale='pt-BR' messages={{}}>
      <ExperimentalAppBadge />
    </IntlProvider>,
  )
}

describe('ExperimentalAppBadge', () => {
  it('renders the experimental label', () => {
    renderBadge()

    expect(screen.getByText('Experimental')).toBeInTheDocument()
  })

  it('exposes the description for assistive technologies', () => {
    renderBadge()

    expect(
      screen.getByLabelText(
        'Nova interface web do Biblivre, ainda em desenvolvimento ativo. Nem todas as funcionalidades da versão clássica estão disponíveis, e recursos podem mudar entre versões.',
      ),
    ).toBeInTheDocument()
  })
})
