import { describe, expect, it } from 'vitest'

import {
  holdingFixture,
  iso,
  openHoldingLendingBag,
} from '../return/testFixtures'

import {
  applyBagToReader,
  applyBagToResults,
  getDaysLate,
  getHoldingStatus,
  getLendBlockReason,
  getReceiptLendingIds,
  sumPendingFines,
} from './lendingState'

import type { HoldingLendingBag } from '../../api-helpers/circulation/response-types'
import type { LendingReader } from '../../api-helpers/lending/response-types'

const openBag = openHoldingLendingBag
const readerOf = (bags: HoldingLendingBag[] = [openBag()]): LendingReader => {
  const user = openBag().user!

  return { id: user.id, user, lendingInfo: bags }
}

const availableBag = (id = 20): HoldingLendingBag => ({
  ...openBag(),
  id,
  holding: holdingFixture({ id, accession_number: `T-${id}` }),
  user: undefined,
  lending: undefined,
})

describe('getLendBlockReason', () => {
  it('blocks blocked or inactive readers', () => {
    const reader = readerOf()

    expect(
      getLendBlockReason(
        { ...reader, user: { ...reader.user, status: 'blocked' } },
        5,
      ),
    ).toBe('blocked_reader')
    expect(
      getLendBlockReason(
        { ...reader, user: { ...reader.user, status: 'inactive' } },
        5,
      ),
    ).toBe('blocked_reader')
  })

  it('blocks when open lendings reach the user type limit', () => {
    expect(getLendBlockReason(readerOf(), 1)).toBe('limit_reached')
    expect(getLendBlockReason(readerOf(), 2)).toBeNull()
  })

  it('does not block on the limit while the user type is unknown', () => {
    expect(getLendBlockReason(readerOf(), undefined)).toBeNull()
  })
})

describe('getDaysLate', () => {
  const lending = openBag().lending!

  it('prefers the server figure', () => {
    expect(getDaysLate({ ...lending, daysLate: 3 }, new Date())).toBe(3)
  })

  it('falls back to the expected return date', () => {
    expect(getDaysLate(lending, new Date('2024-02-05T12:00:00'))).toBe(4)
    expect(getDaysLate(lending, new Date('2024-01-20T12:00:00'))).toBe(0)
  })
})

describe('getHoldingStatus', () => {
  it('tells who holds the holding', () => {
    expect(getHoldingStatus(openBag(), 3)).toBe('lent_to_reader')
    expect(getHoldingStatus(openBag(), 99)).toBe('lent_to_other')
    expect(getHoldingStatus(availableBag(), 3)).toBe('available')
  })

  it('treats a returned lending as available', () => {
    const bag = openBag()
    const returned = {
      ...bag,
      lending: { ...bag.lending!, returnDate: iso('2024-01-20T00:00:00') },
    }

    expect(getHoldingStatus(returned, 3)).toBe('available')
  })

  it('reports holdings that cannot circulate', () => {
    const bag = availableBag()

    expect(
      getHoldingStatus(
        { ...bag, holding: { ...bag.holding, availability: 'unavailable' } },
        3,
      ),
    ).toBe('unavailable')
  })
})

describe('applyBagToReader', () => {
  it('adds a new lending of the reader', () => {
    const lent = {
      ...availableBag(),
      lending: { ...openBag().lending!, id: 77, holdingId: 20 },
    }

    expect(applyBagToReader(readerOf(), lent).lendingInfo).toHaveLength(2)
  })

  it('replaces a renewed lending instead of duplicating it', () => {
    const bag = openBag()
    const renewed = { ...bag, lending: { ...bag.lending!, id: 88 } }

    const { lendingInfo } = applyBagToReader(readerOf(), renewed)

    expect(lendingInfo?.map(({ lending }) => lending?.id)).toEqual([88])
  })

  it('removes a returned lending', () => {
    const bag = openBag()
    const returned = {
      ...bag,
      lending: { ...bag.lending!, returnDate: iso('2024-01-20T00:00:00') },
    }

    expect(applyBagToReader(readerOf(), returned).lendingInfo).toEqual([])
  })

  it('ignores lendings of other readers', () => {
    const reader = readerOf()
    const bag = openBag()
    const other = { ...bag, lending: { ...bag.lending!, userId: 99 } }

    expect(applyBagToReader(reader, other)).toBe(reader)
  })
})

describe('applyBagToResults', () => {
  it('updates only the matching holding', () => {
    const lent = { ...availableBag(20), lending: openBag().lending }
    const results = applyBagToResults(
      [availableBag(20), availableBag(21)],
      lent,
    )

    expect(results?.[0].lending).toBeDefined()
    expect(results?.[1].lending).toBeUndefined()
  })

  it('keeps an empty search state untouched', () => {
    expect(applyBagToResults(null, openBag())).toBeNull()
  })
})

describe('session helpers', () => {
  it('sums only unpaid fines', () => {
    expect(
      sumPendingFines([
        { value: 2.5, payment: undefined },
        { value: 4, payment: '2024-01-01T00:00:00' },
        { value: 1, payment: null },
      ]),
    ).toBe(3.5)
  })

  it('lists the reader receipt lendings once each', () => {
    const at = Date.now()

    expect(
      getReceiptLendingIds(
        [
          { kind: 'lend', readerId: 3, lendingId: 1, holdingId: 10, at },
          { kind: 'renew', readerId: 3, lendingId: 2, holdingId: 10, at },
          { kind: 'lend', readerId: 4, lendingId: 3, holdingId: 11, at },
          { kind: 'return', readerId: 3, lendingId: 2, holdingId: 10, at },
        ],
        3,
      ),
    ).toEqual([1, 2])
  })
})
