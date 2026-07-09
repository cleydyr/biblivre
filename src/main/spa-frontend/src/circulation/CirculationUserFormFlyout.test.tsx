import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import { ToastProvider } from '../toasts/ToastProvider'

import CirculationUserFormFlyout from './CirculationUserFormFlyout'

import type { User } from '../api-helpers/circulation/response-types'

const editUserFixture: User = {
  id: 10,
  name: 'Maria Silva',
  type: 1,
  type_name: 'Padrão',
  loginId: 0,
  enrollment: '00010',
  status: 'active',
  createdBy: 1,
  modifiedBy: 1,
  created: '2024-01-01T10:00:00.000Z' as User['created'],
  modified: '2024-01-02T10:00:00.000Z' as User['modified'],
  fields: {
    email: 'maria@example.com',
    gender: '',
    phone_cel: '',
    phone_home: '',
    phone_work: '',
    phone_work_extension: '',
    id_rg: '',
    id_cpf: '',
    address: '',
    address_number: '',
    address_complement: '',
    address_zip: '',
    address_city: '',
    address_state: '',
    birthday: '' as User['fields']['birthday'],
    obs: '',
  },
}

vi.mock('../api-helpers/user-fields/hooks', () => ({
  useUserFields: () => ({
    data: [
      {
        key: 'email',
        type: 'string',
        required: true,
        maxLength: 0,
        sortOrder: 1,
      },
    ],
    isLoading: false,
  }),
}))

vi.mock('../api-helpers/user-type/hooks', () => ({
  useUserTypes: () => ({
    data: [{ id: 1, name: 'Padrão' }],
    isLoading: false,
  }),
}))

vi.mock('../api-helpers/circulation/hooks', () => ({
  useSaveCirculationUserMutation: () => ({
    mutate: vi.fn(),
    isPending: false,
  }),
}))

const renderFlyout = (
  props: Partial<React.ComponentProps<typeof CirculationUserFormFlyout>> = {},
) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
    },
  })

  return render(
    <QueryClientProvider client={queryClient}>
      <IntlProvider locale='pt-BR' messages={{}}>
        <ToastProvider>
          <CirculationUserFormFlyout
            mode='create'
            onClose={vi.fn()}
            onSaved={vi.fn()}
            {...props}
          />
        </ToastProvider>
      </IntlProvider>
    </QueryClientProvider>,
  )
}

describe('CirculationUserFormFlyout', () => {
  it('renders create mode actions', () => {
    renderFlyout()

    expect(
      screen.getByRole('heading', { name: /Novo usuário/i }),
    ).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Salvar/i })).toBeInTheDocument()
    expect(
      screen.queryByRole('button', { name: /Salvar como Novo/i }),
    ).not.toBeInTheDocument()
  })

  it('renders edit mode actions', () => {
    renderFlyout({ mode: 'edit', user: editUserFixture })

    expect(
      screen.getByRole('button', { name: /Salvar como Novo/i }),
    ).toBeInTheDocument()
  })

  it('asks for confirmation when canceling a dirty form', async () => {
    const user = userEvent.setup()
    const onClose = vi.fn()

    renderFlyout({ onClose })

    await user.type(screen.getByLabelText('Nome'), 'Changed')
    await user.click(screen.getByRole('button', { name: /Cancelar/i }))

    expect(
      screen.getByText(/Você deseja cancelar a edição deste usuário/i),
    ).toBeInTheDocument()
    expect(onClose).not.toHaveBeenCalled()
  })
})
