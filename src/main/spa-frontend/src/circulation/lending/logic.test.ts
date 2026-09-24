import { describe, expect, it } from 'vitest'

import { closedBag, openHoldingLendingBag } from '../return/testFixtures'

import {
  getLendingEligibility,
  getLendingErrorMessage,
  getRenewalEligibility,
  hasReservationPriorityWarning,
} from './logic'

import type { LendingBag } from '../../api-helpers/circulation/response-types'
import type { LendingPatron } from '../../api-helpers/lending/response-types'

const patronFixture = (
  overrides: Partial<LendingPatron> = {},
): LendingPatron => {
  const bag = openHoldingLendingBag()

  return {
    id: bag.user!.id,
    user: bag.user!,
    lendingInfo: [bag as LendingBag],
    reservedRecords: [],
    ...overrides,
  }
}

describe('lending eligibility', () => {
  it('allows an available holding for an active patron', () => {
    const available = closedBag()
    available.lending = undefined

    expect(getLendingEligibility(available, patronFixture())).toBe('eligible')
  })

  it('blocks unavailable and already lent holdings', () => {
    const unavailable = closedBag()
    unavailable.lending = undefined
    unavailable.holding.availability = 'unavailable'

    expect(getLendingEligibility(unavailable, patronFixture())).toBe(
      'holding_unavailable',
    )
    expect(
      getLendingEligibility(openHoldingLendingBag(), patronFixture()),
    ).toBe('already_lent')
  })

  it('blocks lending and renewal for blocked patrons', () => {
    const patron = patronFixture({
      user: {
        ...patronFixture().user,
        status: 'blocked',
      },
    })
    const available = closedBag()
    available.lending = undefined

    expect(getLendingEligibility(available, patron)).toBe('patron_blocked')
    expect(
      getRenewalEligibility(openHoldingLendingBag().lending!, patron),
    ).toBe('patron_blocked')
  })
})

describe('renewal eligibility', () => {
  it('allows an open, on-time lending for the selected patron', () => {
    const bag = openHoldingLendingBag()

    expect(getRenewalEligibility(bag.lending!, patronFixture())).toBe(
      'eligible',
    )
  })

  it('blocks overdue, returned, and another patron loans', () => {
    const patron = patronFixture()
    const bag = openHoldingLendingBag()

    expect(
      getRenewalEligibility({ ...bag.lending!, daysLate: 1 }, patron),
    ).toBe('overdue')
    expect(getRenewalEligibility(closedBag().lending!, patron)).toBe(
      'already_returned',
    )
    expect(
      getRenewalEligibility({ ...bag.lending!, userId: 999 }, patron),
    ).toBe('different_patron')
  })
})

describe('reservation priority warning', () => {
  it('warns when all available copies are reserved for other patrons', () => {
    const bag = openHoldingLendingBag()
    bag.biblio = {
      ...bag.biblio!,
      holdings_available: 1,
      holdings_reserved: 1,
    }

    expect(hasReservationPriorityWarning(bag, patronFixture())).toBe(true)
    expect(
      hasReservationPriorityWarning(
        bag,
        patronFixture({ reservedRecords: [bag.biblio.id] }),
      ),
    ).toBe(false)
  })
})

describe('lending errors', () => {
  it('prefers server errors, then thrown errors, then the fallback', () => {
    expect(
      getLendingErrorMessage(
        new Error('network'),
        'Regra do servidor',
        'Falha',
      ),
    ).toBe('Regra do servidor')
    expect(
      getLendingErrorMessage(new Error('network'), undefined, 'Falha'),
    ).toBe('network')
    expect(getLendingErrorMessage(null, undefined, 'Falha')).toBe('Falha')
  })
})
