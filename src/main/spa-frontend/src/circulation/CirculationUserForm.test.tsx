import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import { userFieldsFixture } from '../api-helpers/user-fields/fixtures'

import CirculationUserForm from './CirculationUserForm'
import { createEmptyUserFormValues } from './circulationUserFormLogic'

const userTypesFixture = [
  { id: 1, name: 'Padrão' },
  { id: 2, name: 'Professor' },
]

const renderForm = (
  overrides: Partial<React.ComponentProps<typeof CirculationUserForm>> = {},
) => {
  const onChange = vi.fn()
  const onPhotoSelect = vi.fn()

  render(
    <IntlProvider locale='pt-BR' messages={{}}>
      <CirculationUserForm
        fieldErrors={{}}
        fields={userFieldsFixture}
        mode='create'
        userTypes={userTypesFixture}
        values={createEmptyUserFormValues(1)}
        onChange={onChange}
        onPhotoSelect={onPhotoSelect}
        {...overrides}
      />
    </IntlProvider>,
  )

  return { onChange, onPhotoSelect }
}

describe('CirculationUserForm', () => {
  it('renders core and dynamic fields', () => {
    renderForm()

    expect(screen.getByLabelText('Nome')).toBeInTheDocument()
    expect(screen.getByLabelText('Tipo de usuário')).toBeInTheDocument()
    expect(screen.getByLabelText('Situação')).toBeInTheDocument()
    expect(screen.getByLabelText('Email')).toBeInTheDocument()
    expect(screen.getByLabelText('Gênero')).toBeInTheDocument()
    expect(screen.getByLabelText('Data de Nascimento')).toBeInTheDocument()
  })

  it('shows enrollment only in edit mode', () => {
    const { rerender } = render(
      <IntlProvider locale='pt-BR' messages={{}}>
        <CirculationUserForm
          fieldErrors={{}}
          fields={userFieldsFixture}
          mode='create'
          userTypes={userTypesFixture}
          values={createEmptyUserFormValues(1)}
          onChange={vi.fn()}
          onPhotoSelect={vi.fn()}
        />
      </IntlProvider>,
    )

    expect(screen.queryByLabelText('Matrícula')).not.toBeInTheDocument()

    rerender(
      <IntlProvider locale='pt-BR' messages={{}}>
        <CirculationUserForm
          fieldErrors={{}}
          fields={userFieldsFixture}
          mode='edit'
          userTypes={userTypesFixture}
          values={{
            ...createEmptyUserFormValues(1),
            id: 10,
            enrollment: '00010',
          }}
          onChange={vi.fn()}
          onPhotoSelect={vi.fn()}
        />
      </IntlProvider>,
    )

    expect(screen.getByLabelText('Matrícula')).toBeInTheDocument()
  })

  it('updates the name field', async () => {
    const user = userEvent.setup()
    const { onChange } = renderForm()

    await user.type(screen.getByLabelText('Nome'), 'Ana')

    expect(onChange).toHaveBeenCalled()
  })

  it('shows validation errors', () => {
    renderForm({
      fieldErrors: {
        name: 'field.error.required',
        email: 'field.error.required',
      },
    })

    expect(
      screen.getAllByText('O preenchimendo deste campo é obrigatório').length,
    ).toBeGreaterThan(0)
  })
})
