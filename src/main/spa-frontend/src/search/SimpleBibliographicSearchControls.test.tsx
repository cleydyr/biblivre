import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import SimpleBibliographicSearchControls from './SimpleBibliographicSearchControls'

import type { SearchQuery } from '../api-helpers/search/types'

const mockMessages = {
  'search.bibliographic.search_all': 'Listar todos',
  'search.bibliographic.search': 'Pesquisar',
  'search.bibliographic.search_label': 'Termos da pesquisa simples',
  'search.bibliographic.search_placeholder': 'Preencha os termos da pesquisa',
}

const renderComponent = (props: {
  onQuerySubmited?: (query?: SearchQuery) => void
  isLoading?: boolean
}) => {
  const defaultProps = {
    onQuerySubmited: vi.fn(),
    isLoading: false,
    ...props,
  }

  return render(
    <IntlProvider locale='en' messages={mockMessages}>
      <SimpleBibliographicSearchControls {...defaultProps} />
    </IntlProvider>
  )
}

describe('SimpleBibliographicSearchControls', () => {
  describe('Initial State', () => {
    it('renders search input and button', () => {
      renderComponent({})

      expect(screen.getByRole('searchbox')).toBeInTheDocument()
      expect(
        screen.getByRole('button', { name: /Listar todos/i })
      ).toBeInTheDocument()
    })

    it('shows correct placeholder text', () => {
      renderComponent({})

      const searchInput = screen.getByRole('searchbox')
      expect(searchInput).toHaveAttribute(
        'placeholder',
        'Preencha os termos da pesquisa'
      )
    })

    it('shows correct aria-label', () => {
      renderComponent({})

      const searchInput = screen.getByRole('searchbox')
      expect(searchInput).toHaveAttribute(
        'aria-label',
        'Termos da pesquisa simples'
      )
    })

    it('starts with "Pesquisar" button text (empty input)', () => {
      renderComponent({})

      expect(
        screen.getByRole('button', { name: /Listar todos/i })
      ).toBeInTheDocument()
      expect(
        screen.queryByRole('button', { name: /Pesquisar/i })
      ).not.toBeInTheDocument()
    })
  })

  describe('Search Input Behavior', () => {
    it('changes button text to "Listar todos" when input is cleared', async () => {
      const user = userEvent.setup()
      renderComponent({})

      const searchInput = screen.getByRole('searchbox')

      // Type something first
      await user.type(searchInput, 'test')

      // Button should still show "Pesquisar"
      expect(
        screen.getByRole('button', { name: /pesquisar/i })
      ).toBeInTheDocument()

      // Clear the input
      await user.clear(searchInput)

      await waitFor(() => {
        expect(
          screen.getByRole('button', { name: /listar todos/i })
        ).toBeInTheDocument()
        expect(
          screen.queryByRole('button', { name: /pesquisar/i })
        ).not.toBeInTheDocument()
      })
    })
  })

  describe('Search Submission', () => {
    it('calls onQuerySubmited with undefined when button clicked with empty input', async () => {
      const user = userEvent.setup()
      const onQuerySubmited = vi.fn()
      renderComponent({ onQuerySubmited })

      // Clear input to get "Listar todos" button
      const searchInput = screen.getByRole('searchbox')
      await user.clear(searchInput)

      expect(
        screen.getByRole('button', { name: /Listar todos/i })
      ).toBeInTheDocument()

      const button = screen.getByRole('button', { name: /listar todos/i })
      await user.click(button)

      expect(onQuerySubmited).toHaveBeenCalledWith(undefined)
    })

    it('calls onQuerySubmited with query when button clicked with text input', async () => {
      const user = userEvent.setup()
      const onQuerySubmited = vi.fn()
      renderComponent({ onQuerySubmited })

      const searchInput = screen.getByRole('searchbox')
      await user.type(searchInput, 'test query')

      const button = screen.getByRole('button', { name: /pesquisar/i })
      await user.click(button)

      expect(onQuerySubmited).toHaveBeenCalledWith({
        query: 'test query',
      })
    })

    it('calls onQuerySubmited when Enter is pressed in search field', async () => {
      const user = userEvent.setup()
      const onQuerySubmited = vi.fn()
      renderComponent({ onQuerySubmited })

      const searchInput = screen.getByRole('searchbox')
      await user.type(searchInput, 'search term')
      await user.keyboard('{Enter}')

      expect(onQuerySubmited).toHaveBeenCalledWith({
        query: 'search term',
      })
    })

    it('does not call onQuerySubmited when Enter is pressed with empty field', async () => {
      const user = userEvent.setup()
      const onQuerySubmited = vi.fn()
      renderComponent({ onQuerySubmited })

      const searchInput = screen.getByRole('searchbox')
      await user.click(searchInput)
      await user.keyboard('{Enter}')

      // Should not call onQuerySubmited for empty search
      expect(onQuerySubmited).not.toHaveBeenCalled()
    })
  })

  describe('Loading State', () => {
    it('shows loading state on button when isLoading is true', () => {
      renderComponent({ isLoading: true })

      const button = screen.getByRole('button', { name: /Listar todos/i })
      expect(button).toBeDisabled()
    })

    it('does not show loading state when isLoading is false', () => {
      renderComponent({ isLoading: false })

      const button = screen.getByRole('button', { name: /Listar todos/i })
      expect(button).not.toBeDisabled()
    })
  })

  describe('State Management', () => {
    it('maintains internal query state correctly', async () => {
      const user = userEvent.setup()
      const onQuerySubmited = vi.fn()
      renderComponent({ onQuerySubmited })

      const searchInput = screen.getByRole('searchbox')

      // Type initial query
      await user.type(searchInput, 'first query')

      // Click button to submit
      const button = screen.getByRole('button', { name: /pesquisar/i })
      await user.click(button)

      expect(onQuerySubmited).toHaveBeenCalledWith({
        query: 'first query',
      })

      // Clear and type new query
      await user.clear(searchInput)
      await user.type(searchInput, 'second query')

      // Click button again
      await user.click(button)

      expect(onQuerySubmited).toHaveBeenLastCalledWith({
        query: 'second query',
      })
    })
  })

  describe('Edge Cases', () => {
    it('handles whitespace-only input correctly', async () => {
      const user = userEvent.setup()
      const onQuerySubmited = vi.fn()
      renderComponent({ onQuerySubmited })

      const searchInput = screen.getByRole('searchbox')
      await user.type(searchInput, '   ')

      // Should still show "Pesquisar" button
      const button = screen.getByRole('button', { name: /pesquisar/i })
      await user.click(button)

      expect(onQuerySubmited).toHaveBeenCalledWith({
        query: '   ',
      })
    })

    it('handles basic special characters in search query', async () => {
      const user = userEvent.setup()
      const onQuerySubmited = vi.fn()
      renderComponent({ onQuerySubmited })

      const searchInput = screen.getByRole('searchbox')
      const specialQuery = 'test@#$%^&*()_+'
      await user.type(searchInput, specialQuery)

      const button = screen.getByRole('button', { name: /pesquisar/i })
      await user.click(button)

      expect(onQuerySubmited).toHaveBeenCalledWith({
        query: specialQuery,
      })
    })
  })

  describe('Accessibility', () => {
    it('has proper ARIA labels', () => {
      renderComponent({})

      const searchInput = screen.getByRole('searchbox')
      expect(searchInput).toHaveAttribute(
        'aria-label',
        'Termos da pesquisa simples'
      )
    })

    it('maintains focus management correctly', async () => {
      const user = userEvent.setup()
      renderComponent({})

      const searchInput = screen.getByRole('searchbox')
      await user.click(searchInput)

      expect(searchInput).toHaveFocus()
    })
  })
})
