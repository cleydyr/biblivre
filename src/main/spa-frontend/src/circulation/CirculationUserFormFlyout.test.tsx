import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { IntlProvider } from 'react-intl'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import { ResponseError } from '../generated-sources'
import { ToastProvider } from '../toasts/ToastProvider'

import CirculationUserFormFlyout from './CirculationUserFormFlyout'

import type { User } from '../api-helpers/circulation/response-types'
import type { AddressLookupResult } from '../generated-sources'

const addressLookupMock = vi.hoisted(() => ({
  enabled: false,
  isPending: false,
  mutate: vi.fn(),
}))

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
    address_neighborhood: '',
    address_city: '',
    address_state: '',
    birthday: '' as User['fields']['birthday'],
    obs: '',
  },
}

const addressFields = [
  {
    key: 'email',
    type: 'string',
    required: true,
    maxLength: 0,
    sortOrder: 1,
  },
  {
    key: 'address',
    type: 'string',
    required: false,
    maxLength: 500,
    sortOrder: 9,
  },
  {
    key: 'address_zip',
    type: 'string',
    required: false,
    maxLength: 20,
    sortOrder: 12,
  },
  {
    key: 'address_neighborhood',
    type: 'string',
    required: false,
    maxLength: 100,
    sortOrder: 13,
  },
  {
    key: 'address_city',
    type: 'string',
    required: false,
    maxLength: 100,
    sortOrder: 14,
  },
  {
    key: 'address_state',
    type: 'string',
    required: false,
    maxLength: 100,
    sortOrder: 15,
  },
]

vi.mock('../api-helpers/user-fields/hooks', () => ({
  useUserFields: () => ({
    data: addressFields,
    isLoading: false,
  }),
}))

vi.mock('../api-helpers/user-type/hooks', () => ({
  useUserTypes: () => ({
    data: [{ id: 1, name: 'Padrão' }],
    isLoading: false,
  }),
}))

vi.mock('../api-helpers/address-lookup/hooks', () => ({
  useAddressLookupEnabled: () => ({
    data: addressLookupMock.enabled,
    isLoading: false,
  }),
  useLookupAddressByCepMutation: () => ({
    mutate: addressLookupMock.mutate,
    isPending: addressLookupMock.isPending,
  }),
}))

vi.mock('../api-helpers/circulation/hooks', () => ({
  useSaveCirculationUserMutation: () => ({
    mutate: vi.fn(),
    isPending: false,
  }),
}))

const successfulLookupResult: AddressLookupResult = {
  street: 'Avenida Paulista',
  neighborhood: 'Bela Vista',
  city: 'São Paulo',
  state: 'SP',
  incomplete: false,
}

const incompleteLookupResult: AddressLookupResult = {
  street: '',
  neighborhood: 'Centro',
  city: 'Campinas',
  state: 'SP',
  incomplete: true,
}

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

const typeCepAndSearch = async (
  user: ReturnType<typeof userEvent.setup>,
  cep: string,
) => {
  await user.type(screen.getByLabelText('CEP'), cep)
  await user.click(screen.getByRole('button', { name: 'Buscar' }))
}

const invokeLookupSuccess = (result: AddressLookupResult) => {
  const mutateOptions = addressLookupMock.mutate.mock.calls.at(-1)?.[1] as {
    onSuccess?: (result: AddressLookupResult) => void
  }
  mutateOptions.onSuccess?.(result)
}

const invokeLookupError = (status: number) => {
  const mutateOptions = addressLookupMock.mutate.mock.calls.at(-1)?.[1] as {
    onError?: (error: unknown) => void
  }
  mutateOptions.onError?.(
    new ResponseError(new Response(null, { status }), `HTTP ${status}`),
  )
}

describe('CirculationUserFormFlyout', () => {
  beforeEach(() => {
    addressLookupMock.enabled = false
    addressLookupMock.isPending = false
    addressLookupMock.mutate.mockReset()
  })

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

  it('hides address lookup when disabled', () => {
    renderFlyout()

    expect(screen.getByLabelText('CEP')).toBeInTheDocument()
    expect(
      screen.queryByRole('button', { name: 'Buscar' }),
    ).not.toBeInTheDocument()
  })

  it('applies lookup result when address fields are empty', async () => {
    addressLookupMock.enabled = true
    const user = userEvent.setup()

    renderFlyout()
    await typeCepAndSearch(user, '01310-100')

    expect(addressLookupMock.mutate).toHaveBeenCalledWith(
      '01310-100',
      expect.objectContaining({
        onSuccess: expect.any(Function),
        onError: expect.any(Function),
      }),
    )

    invokeLookupSuccess(successfulLookupResult)

    await waitFor(() => {
      expect(screen.getByLabelText('Endereço')).toHaveValue('Avenida Paulista')
      expect(screen.getByLabelText('Bairro')).toHaveValue('Bela Vista')
      expect(screen.getByLabelText('Cidade')).toHaveValue('São Paulo')
      expect(screen.getByLabelText('Estado')).toHaveValue('SP')
    })
  })

  it('asks for confirmation before overwriting filled address fields', async () => {
    addressLookupMock.enabled = true
    const user = userEvent.setup()

    renderFlyout()
    await user.type(screen.getByLabelText('Endereço'), 'Rua Antiga')
    await typeCepAndSearch(user, '01310100')

    invokeLookupSuccess(successfulLookupResult)

    expect(
      await screen.findByText(
        'Já existem dados de endereço preenchidos. Substituir pelos dados do CEP?',
      ),
    ).toBeInTheDocument()
    expect(screen.getByLabelText('Endereço')).toHaveValue('Rua Antiga')

    await user.click(screen.getByRole('button', { name: 'Sim' }))

    await waitFor(() => {
      expect(screen.getByLabelText('Endereço')).toHaveValue('Avenida Paulista')
    })
  })

  it('shows warning toast for incomplete lookup results', async () => {
    addressLookupMock.enabled = true
    const user = userEvent.setup()

    renderFlyout()
    await typeCepAndSearch(user, '13010000')
    invokeLookupSuccess(incompleteLookupResult)

    expect(
      await screen.findByText(
        'Endereço incompleto; preencha a rua manualmente.',
      ),
    ).toBeInTheDocument()
    expect(screen.getByLabelText('Cidade')).toHaveValue('Campinas')
    expect(screen.getByLabelText('Endereço')).toHaveValue('')
  })

  it('shows invalid CEP toast without calling the mutation', async () => {
    addressLookupMock.enabled = true
    const user = userEvent.setup()

    renderFlyout()
    await typeCepAndSearch(user, '123')

    expect(addressLookupMock.mutate).not.toHaveBeenCalled()
    expect(
      await screen.findByText('Informe um CEP válido com 8 dígitos.'),
    ).toBeInTheDocument()
  })

  it('shows not found toast for 404 lookup errors', async () => {
    addressLookupMock.enabled = true
    const user = userEvent.setup()

    renderFlyout()
    await typeCepAndSearch(user, '00000000')
    invokeLookupError(404)

    expect(await screen.findByText('CEP não encontrado.')).toBeInTheDocument()
  })

  it('shows invalid CEP toast for 400 lookup errors', async () => {
    addressLookupMock.enabled = true
    const user = userEvent.setup()

    renderFlyout()
    await typeCepAndSearch(user, '01310100')
    invokeLookupError(400)

    expect(
      await screen.findByText('Informe um CEP válido com 8 dígitos.'),
    ).toBeInTheDocument()
  })

  it('keeps existing address when overwrite is cancelled', async () => {
    addressLookupMock.enabled = true
    const user = userEvent.setup()

    renderFlyout()
    await user.type(screen.getByLabelText('Endereço'), 'Rua Antiga')
    await typeCepAndSearch(user, '01310100')
    invokeLookupSuccess(successfulLookupResult)

    const overwriteMessage =
      'Já existem dados de endereço preenchidos. Substituir pelos dados do CEP?'

    expect(await screen.findByText(overwriteMessage)).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: 'Não' }))

    expect(screen.getByLabelText('Endereço')).toHaveValue('Rua Antiga')
    expect(screen.queryByText(overwriteMessage)).not.toBeInTheDocument()
  })
})
