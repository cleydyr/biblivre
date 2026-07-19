import { describe, expect, it } from 'vitest'

import { findUniqueHoldingIdentifierMatch } from './useCirculationReturn'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'
import type { Holding } from '../../api-helpers/search/response-types'
import type { ISO8601Date } from '../../types'

const iso = (value: string) => value as ISO8601Date

const holding = (
  overrides: Partial<Holding> & Pick<Holding, 'id'>,
): Holding => ({
  attachments: [],
  created: '2024-01-01T00:00:00.000Z',
  availability: 'available',
  shelf_location: '',
  material_type: 'book',
  accession_number: `ACC-${overrides.id}`,
  record_id: 1,
  database: 'main',
  marc: '',
  modified: '2024-01-01T00:00:00.000Z',
  json: {},
  location_d: '',
  ...overrides,
})

const bag = (
  overrides: Partial<HoldingLendingBag> & {
    holding: Holding
  },
): HoldingLendingBag => ({
  id: overrides.holding.id,
  createdBy: 1,
  modifiedBy: 1,
  created: iso('2024-01-01T00:00:00.000Z'),
  modified: iso('2024-01-01T00:00:00.000Z'),
  biblio: {
    id: 1,
    holdings_count: 1,
    holdings_lent: 1,
    holdings_reserved: 0,
    holdings_available: 0,
    attachments: [],
    created: '2024-01-01T00:00:00.000Z',
    modified: '2024-01-01T00:00:00.000Z',
    author: 'Machado de Assis',
    subject: '',
    title: 'Dom Casmurro',
    shelf_location: 'A1',
    material_type: 'book',
    database: 'main',
    publication_year: '1899',
    marc: '',
    json: {},
  },
  user: {
    id: 5,
    name: 'Ana',
    type: 1,
    type_name: 'Padrão',
    loginId: 0,
    enrollment: '00005',
    status: 'active',
    createdBy: 1,
    modifiedBy: 1,
    created: iso('2024-01-01T00:00:00.000Z'),
    modified: iso('2024-01-01T00:00:00.000Z'),
    fields: {
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
    },
  },
  lending: {
    id: 100,
    holdingId: overrides.holding.id,
    userId: 5,
    previousLendingId: 0,
    expectedReturnDate: iso('2024-02-01T00:00:00.000Z'),
    createdBy: 1,
    modifiedBy: 1,
    created: iso('2024-01-15T00:00:00.000Z'),
    modified: iso('2024-01-15T00:00:00.000Z'),
  },
  ...overrides,
})

describe('findUniqueHoldingIdentifierMatch', () => {
  it('matches a Holding by exact accession number (case-insensitive)', () => {
    const match = bag({
      holding: holding({ id: 10, accession_number: 'T-100' }),
    })
    const other = bag({
      holding: holding({ id: 11, accession_number: 'T-101' }),
    })

    expect(findUniqueHoldingIdentifierMatch('t-100', [match, other])).toBe(
      match,
    )
  })

  it('matches a Holding by numeric holding id', () => {
    const match = bag({ holding: holding({ id: 42, accession_number: 'X' }) })

    expect(findUniqueHoldingIdentifierMatch('42', [match])).toBe(match)
  })

  it('does not auto-match title-like queries even with a single result', () => {
    const only = bag({
      holding: holding({ id: 7, accession_number: 'T-7' }),
    })

    expect(findUniqueHoldingIdentifierMatch('Dom Casmurro', [only])).toBeNull()
  })

  it('returns null when accession is ambiguous', () => {
    const first = bag({
      holding: holding({ id: 1, accession_number: 'DUP' }),
    })
    const second = bag({
      holding: holding({ id: 2, accession_number: 'DUP' }),
    })

    expect(findUniqueHoldingIdentifierMatch('DUP', [first, second])).toBeNull()
  })

  it('returns null for an empty query or empty result list', () => {
    expect(findUniqueHoldingIdentifierMatch('', [])).toBeNull()
    expect(findUniqueHoldingIdentifierMatch('T-1', [])).toBeNull()
  })
})
