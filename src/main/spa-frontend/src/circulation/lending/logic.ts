import type {
  HoldingLendingBag,
  Lending,
} from '../../api-helpers/circulation/response-types'
import type { LendingPatron } from '../../api-helpers/lending/response-types'

export type LendingEligibility =
  'eligible' | 'patron_blocked' | 'holding_unavailable' | 'already_lent'

export type RenewalEligibility =
  | 'eligible'
  | 'patron_blocked'
  | 'different_patron'
  | 'already_returned'
  | 'overdue'

export function getLendingEligibility(
  holdingBag: HoldingLendingBag,
  patron: LendingPatron,
): LendingEligibility {
  if (patron.user.status === 'blocked' || patron.user.status === 'inactive') {
    return 'patron_blocked'
  }

  if (
    holdingBag.lending !== undefined &&
    holdingBag.lending.returnDate === undefined
  ) {
    return 'already_lent'
  }

  if (holdingBag.holding.availability !== 'available') {
    return 'holding_unavailable'
  }

  return 'eligible'
}

export function getRenewalEligibility(
  lending: Lending,
  patron: LendingPatron,
): RenewalEligibility {
  if (lending.returnDate !== undefined) {
    return 'already_returned'
  }

  if (lending.userId !== patron.user.id) {
    return 'different_patron'
  }

  if (patron.user.status === 'blocked' || patron.user.status === 'inactive') {
    return 'patron_blocked'
  }

  if ((lending.daysLate ?? 0) > 0) {
    return 'overdue'
  }

  return 'eligible'
}

export function hasReservationPriorityWarning(
  holdingBag: HoldingLendingBag,
  patron: LendingPatron,
): boolean {
  const biblio = holdingBag.biblio
  if (!biblio) {
    return false
  }

  return (
    biblio.holdings_reserved > 0 &&
    biblio.holdings_reserved >= biblio.holdings_available &&
    !patron.reservedRecords.includes(biblio.id)
  )
}

export function getLendingErrorMessage(
  error: unknown,
  serverMessage: string | undefined,
  fallback: string,
): string {
  if (serverMessage) {
    return serverMessage
  }

  if (error instanceof Error && error.message.trim()) {
    return error.message
  }

  return fallback
}
