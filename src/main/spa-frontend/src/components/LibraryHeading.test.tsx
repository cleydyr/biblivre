import { render, screen } from '@testing-library/react'
import { IntlProvider } from 'react-intl'
import { describe, expect, it } from 'vitest'

import LibraryHeading from './LibraryHeading'

import type { ComponentProps } from 'react'

function renderHeading(props: ComponentProps<typeof LibraryHeading>) {
  return render(
    <IntlProvider locale='pt-BR' messages={{}}>
      <LibraryHeading {...props} />
    </IntlProvider>,
  )
}

describe('LibraryHeading', () => {
  it('renders the library name', () => {
    renderHeading({ name: 'Biblioteca Central' })

    expect(screen.getByText('Biblioteca Central')).toBeInTheDocument()
  })

  it('renders the subtitle when provided', () => {
    renderHeading({
      name: 'Biblioteca Central',
      subtitle: 'Rede Comunitária',
    })

    expect(screen.getByText('Rede Comunitária')).toBeInTheDocument()
  })

  it('omits empty subtitles', () => {
    renderHeading({
      name: 'Biblioteca Central',
      subtitle: '   ',
    })

    expect(screen.queryByTitle('   ')).not.toBeInTheDocument()
  })

  it('exposes the full subtitle on hover via title attribute', () => {
    renderHeading({
      name: 'Biblioteca Central',
      subtitle: 'Um subtítulo longo que será truncado visualmente no cabeçalho',
    })

    expect(
      screen.getByTitle(
        'Um subtítulo longo que será truncado visualmente no cabeçalho',
      ),
    ).toBeInTheDocument()
  })
})
