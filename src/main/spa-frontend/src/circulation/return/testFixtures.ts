import type {
  HoldingLendingBag,
  LendingFine,
  ReservationInfo,
  User,
} from '../../api-helpers/circulation/response-types'
import type { Holding } from '../../api-helpers/search/response-types'
import type { ISO8601Date } from '../../types'

export const iso = (value: string) => value as ISO8601Date

export const holdingFixture = (
  overrides: Partial<Holding> & Pick<Holding, 'id' | 'accession_number'>,
): Holding => ({
  attachments: [],
  created: '2024-01-01T00:00:00.000Z',
  availability: 'available',
  shelf_location: '',
  material_type: 'book',
  record_id: 1,
  database: 'main',
  marc: '',
  modified: '2024-01-01T00:00:00.000Z',
  json: {},
  location_d: '',
  ...overrides,
})

const emptyUserFields = (): User['fields'] => ({
  email: '',
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
  birthday: iso(''),
  obs: '',
})

export const openHoldingLendingBag = (): HoldingLendingBag => ({
  id: 10,
  createdBy: 1,
  modifiedBy: 1,
  created: iso('2024-01-01T00:00:00.000Z'),
  modified: iso('2024-01-01T00:00:00.000Z'),
  holding: holdingFixture({ id: 10, accession_number: 'T-10' }),
  biblio: {
    id: 1,
    holdings_count: 1,
    holdings_lent: 1,
    holdings_reserved: 0,
    holdings_available: 0,
    attachments: [],
    created: '2024-01-01T00:00:00.000Z',
    modified: '2024-01-01T00:00:00.000Z',
    author: 'Clarice Lispector',
    subject: '',
    title: 'A Hora da Estrela',
    shelf_location: 'B2',
    material_type: 'book',
    database: 'main',
    publication_year: '1977',
    marc: '',
    json: {},
  },
  user: {
    id: 3,
    name: 'João',
    type: 1,
    type_name: 'Padrão',
    loginId: 0,
    enrollment: '00003',
    status: 'active',
    createdBy: 1,
    modifiedBy: 1,
    created: iso('2024-01-01T00:00:00.000Z'),
    modified: iso('2024-01-01T00:00:00.000Z'),
    fields: emptyUserFields(),
  },
  lending: {
    id: 55,
    holdingId: 10,
    userId: 3,
    previousLendingId: 0,
    expectedReturnDate: iso('2024-02-01T00:00:00.000Z'),
    createdBy: 1,
    modifiedBy: 1,
    created: iso('2024-01-10T00:00:00.000Z'),
    modified: iso('2024-01-10T00:00:00.000Z'),
  },
})

export const closedBag = (): HoldingLendingBag => {
  const open = openHoldingLendingBag()
  return {
    ...open,
    lending: {
      ...open.lending!,
      returnDate: iso('2024-01-20T00:00:00.000Z'),
    },
  }
}

export const unpaidFine = (): LendingFine => ({
  id: 9,
  lendingId: 55,
  userId: 3,
  value: 2.5,
  payment: undefined as unknown as ISO8601Date,
  title: 'A Hora da Estrela',
  author: 'Clarice Lispector',
  createdBy: 1,
  modifiedBy: 1,
  created: iso('2024-01-20T00:00:00.000Z'),
  modified: iso('2024-01-20T00:00:00.000Z'),
})

export const nextReservationFixture = (): ReservationInfo => ({
  createdBy: 1,
  modifiedBy: 1,
  created: iso('2024-01-01T00:00:00.000Z'),
  modified: iso('2024-01-01T00:00:00.000Z'),
  reservation: {
    id: 1,
    recordId: 1,
    userId: 8,
    expires: iso('2024-03-01T00:00:00.000Z'),
    createdBy: 1,
    modifiedBy: 1,
    created: iso('2024-01-01T00:00:00.000Z'),
    modified: iso('2024-01-01T00:00:00.000Z'),
  },
  user: {
    id: 8,
    name: 'Maria Reserva',
    type: 1,
    type_name: 'Padrão',
    loginId: 0,
    enrollment: '00008',
    status: 'active',
    createdBy: 1,
    modifiedBy: 1,
    created: iso('2024-01-01T00:00:00.000Z'),
    modified: iso('2024-01-01T00:00:00.000Z'),
    fields: emptyUserFields(),
  },
})
