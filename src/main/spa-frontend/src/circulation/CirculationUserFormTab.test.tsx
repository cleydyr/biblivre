import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import { IntlProvider } from 'react-intl'
import { describe, expect, it, vi } from 'vitest'

import { userFieldsFixture } from '../api-helpers/user-fields/fixtures'

import CirculationUserFormTab from './CirculationUserFormTab'

import type { User } from '../api-helpers/circulation/response-types'

const sampleUser: User = {
  id: 42,
  name: 'Maria Silva',
  type: 1,
  type_name: 'Padrão',
  loginId: 0,
  enrollment: '00042',
  status: 'active',
  createdBy: 1,
  modifiedBy: 1,
  created: '2024-01-01T10:00:00.000Z' as User['created'],
  modified: '2024-01-02T10:00:00.000Z' as User['modified'],
  fields: {
    email: 'maria@example.com',
    gender: '1',
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
    birthday: '1990-05-10T00:00:00.000Z' as User['fields']['birthday'],
    obs: '',
  },
}

vi.mock('../api-helpers/user-fields/hooks', () => ({
  useUserFields: () => ({
    data: userFieldsFixture,
    isLoading: false,
  }),
}))

vi.mock('../api-helpers/user-type/hooks', () => ({
  useUserType: () => ({
    data: { id: 1, name: 'Padrão' },
    isLoading: false,
  }),
}))

const renderTab = (user: User = sampleUser) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  })

  return render(
    <QueryClientProvider client={queryClient}>
      <IntlProvider locale='pt-BR' messages={{}}>
        <CirculationUserFormTab user={user} />
      </IntlProvider>
    </QueryClientProvider>,
  )
}

describe('CirculationUserFormTab', () => {
  it('renders core and dynamic user fields', () => {
    renderTab()
    expect(screen.getByText('Gênero')).toBeInTheDocument()
    expect(screen.getByText('Masculino')).toBeInTheDocument()
    expect(screen.getByText('Data de Nascimento')).toBeInTheDocument()
    expect(screen.getByText('10/05/1990')).toBeInTheDocument()
    expect(screen.getByText('Cadastrado em')).toBeInTheDocument()
    expect(screen.getByText('Atualizado em')).toBeInTheDocument()
  })
})
