import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import { FIELDS } from '../api-helpers/search/constants'

import AdvancedBibliographicSearchControls from './AdvancedBibliographicSearchControls'

import type { ComponentProps } from 'react'

const renderComponent = (
  props: Partial<ComponentProps<typeof AdvancedBibliographicSearchControls>>
) => {
  const defaultProps: ComponentProps<
    typeof AdvancedBibliographicSearchControls
  > = {
    onQuerySubmited: vi.fn(),
    isLoading: false,
    ...props,
  }

  return render(
    <IntlProvider locale='en' messages={{}}>
      <AdvancedBibliographicSearchControls {...defaultProps} />
    </IntlProvider>
  )
}

describe('AdvancedBibliographicSearchControls', () => {
  it('renders with initial search field', () => {
    renderComponent({})

    expect(screen.getByRole('textbox', { name: /termo/i })).toBeInTheDocument()
    expect(screen.getByLabelText('Termo')).toHaveValue('')
    expect(screen.getByRole('combobox', { name: 'Campo' })).toHaveValue(
      FIELDS.ANY
    )
  })

  it('does not show remove button for the first field', () => {
    renderComponent({})

    expect(
      screen.queryByRole('button', { name: /remover termo/i })
    ).not.toBeInTheDocument()
  })

  it('adds a new search field when add button is clicked', async () => {
    renderComponent({})

    await addQueryTermField()

    const fields = screen.getAllByRole('textbox', { name: /termo/i })
    expect(fields).toHaveLength(2)

    expect(
      screen.getByRole('button', { name: /remover termo/i })
    ).toBeInTheDocument()
  })

  it('removes a search field when remove button is clicked', async () => {
    const user = userEvent.setup()
    renderComponent({})

    // Add a second field
    await addQueryTermField()

    expect(screen.getAllByRole('textbox', { name: /termo/i })).toHaveLength(2)

    // Remove the second field
    const removeButton = screen.getByRole('button', { name: /remover termo/i })
    await user.click(removeButton)

    await waitFor(() => {
      expect(screen.getAllByRole('textbox', { name: /termo/i })).toHaveLength(1)
    })
  })

  it('does not remove the last remaining field', async () => {
    const user = userEvent.setup()
    renderComponent({})

    // Add a second field
    await addQueryTermField()

    // Remove the second field
    const removeButton = screen.getByRole('button', { name: /remover termo/i })
    await user.click(removeButton)

    // Try to remove the first field (should not be possible)
    expect(
      screen.queryByRole('button', { name: /remover termo/i })
    ).not.toBeInTheDocument()
    expect(screen.getAllByRole('textbox', { name: /termo/i })).toHaveLength(1)
  })

  it('updates field values when changed', async () => {
    const user = userEvent.setup()
    renderComponent({})

    await addQueryTermField()

    const queryInput = screen.getAllByLabelText('Termo')[1]
    const operatorSelect = screen.getByRole('combobox', { name: 'Operador' })
    const fieldSelect = screen.getAllByRole('combobox', { name: 'Campo' })[1]

    await user.type(queryInput, 'test query')
    await user.selectOptions(operatorSelect, 'OR')
    await user.selectOptions(fieldSelect, FIELDS.AUTHOR)

    expect(queryInput).toHaveValue('test query')
    expect(operatorSelect).toHaveValue('OR')
    expect(fieldSelect).toHaveValue(FIELDS.AUTHOR)
  })

  it('calls onQuerySubmited with correct data when search button is clicked', async () => {
    const user = userEvent.setup()
    const onQuerySubmited = vi.fn()
    renderComponent({ onQuerySubmited })

    // Fill in the first field
    const queryInput = screen.getByLabelText('Termo')
    await user.type(queryInput, 'test query')

    // Add a second field
    await addQueryTermField()

    // Fill in the second field
    const queryInputs = screen.getAllByLabelText('Termo')
    const operatorSelects = screen.getAllByRole('combobox', {
      name: 'Operador',
    })
    expect(operatorSelects).toHaveLength(1)

    const fieldSelects = screen.getAllByRole('combobox', { name: 'Campo' })

    await user.type(queryInputs[1], 'second query')
    await user.selectOptions(operatorSelects[0], 'OR')
    await user.selectOptions(fieldSelects[1], FIELDS.TITLE)

    // Submit the search
    const searchButton = screen.getByRole('button', { name: /pesquisar/i })
    await user.click(searchButton)

    expect(onQuerySubmited).toHaveBeenCalledWith([
      {
        query: 'test query',
        operator: 'AND',
        field: FIELDS.ANY,
      },
      {
        query: 'second query',
        operator: 'OR',
        field: FIELDS.TITLE,
      },
    ])
  })

  it('shows loading state on search button', () => {
    renderComponent({ isLoading: true })

    const searchButton = screen.getByRole('button', { name: /pesquisar/i })
    expect(searchButton).toBeDisabled()
  })

  it('generates unique termFieldIds for each field', async () => {
    renderComponent({})

    // Add multiple fields
    await addQueryTermField(2)

    const fields = screen.getAllByLabelText('Termo')
    expect(fields).toHaveLength(3)

    // Check that all fields have different IDs
    const fieldIds = fields.map((field) => field.getAttribute('id'))
    const uniqueIds = new Set(fieldIds)
    expect(uniqueIds.size).toBe(3)
  })

  it('maintains field state when adding and removing fields', async () => {
    const user = userEvent.setup()
    renderComponent({})

    // Fill in the first field
    const queryInput = screen.getByLabelText('Termo')
    await user.type(queryInput, 'persistent query')

    // Add a second and a third field
    await addQueryTermField(2)

    // Remove the second field (middle one)
    const removeButtons = screen.getAllByLabelText('Remover termo')
    await user.click(removeButtons[0]) // Remove the first remove button (second field)

    // Check that the first field still has its value
    const remainingQueryInputs = screen.getAllByLabelText('Termo')
    expect(remainingQueryInputs[0]).toHaveValue('persistent query')
    expect(remainingQueryInputs).toHaveLength(2)
  })

  it('sends an undefined query by default', async () => {
    const user = userEvent.setup()

    const onQuerySubmited = vi.fn()

    renderComponent({ onQuerySubmited })

    // Submit the search
    const searchButton = screen.getByRole('button', { name: /pesquisar/i })
    await user.click(searchButton)

    expect(onQuerySubmited).toHaveBeenCalledWith(undefined)
  })
})

async function addQueryTermField(quantity = 1) {
  const user = userEvent.setup()

  const addButton = screen.getByRole('button', { name: /adicionar campo/i })

  for (let i = 0; i < quantity; i++) {
    await user.click(addButton)
  }
}
