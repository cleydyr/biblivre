/**
 * Pure helpers for the Lending screen. The backend (`LendingBO`) stays the
 * authority on every rule; these only drive what the UI enables up front and
 * how server responses are folded into local state.
 */
import type {
  HoldingLendingBag,
  Lending,
  User,
} from '../../api-helpers/circulation/response-types'
import type { LendingReader } from '../../api-helpers/lending/response-types'

export type LendBlockReason = 'blocked_reader' | 'limit_reached'

export type HoldingStatus =
  'available' | 'lent_to_reader' | 'lent_to_other' | 'unavailable'

export type SessionOperation = {
  kind: 'lend' | 'renew' | 'return'
  readerId: number
  lendingId: number
  holdingId: number
  at: number
}

const DAY_IN_MS = 24 * 60 * 60 * 1000

export const isOpenLending = (
  lending: Lending | undefined,
): lending is Lending => lending !== undefined && !lending.returnDate

/**
 * Prefer the server's figure (set by `user_search` for late lendings); fall
 * back to the expected return date for bags that come without it.
 */
export const getDaysLate = (lending: Lending, now: Date) => {
  if (lending.daysLate !== undefined) {
    return lending.daysLate
  }

  const late = Math.floor(
    (now.getTime() - new Date(lending.expectedReturnDate).getTime()) /
      DAY_IN_MS,
  )

  return Math.max(0, late)
}

export const isUserBlocked = (user: User | undefined) =>
  user?.status === 'blocked' || user?.status === 'inactive'

export const getOpenLendings = (reader: LendingReader) =>
  (reader.lendingInfo ?? []).filter(({ lending }) => isOpenLending(lending))

/** Mirrors `LendingBO.checkLending`; an unknown limit never blocks. */
export const getLendBlockReason = (
  reader: LendingReader,
  lendingLimit: number | undefined,
): LendBlockReason | null => {
  if (isUserBlocked(reader.user)) {
    return 'blocked_reader'
  }

  if (
    lendingLimit !== undefined &&
    getOpenLendings(reader).length >= lendingLimit
  ) {
    return 'limit_reached'
  }

  return null
}

export const getHoldingStatus = (
  bag: HoldingLendingBag,
  readerId: number,
): HoldingStatus => {
  if (isOpenLending(bag.lending)) {
    return bag.lending.userId === readerId ? 'lent_to_reader' : 'lent_to_other'
  }

  return bag.holding.availability === 'available' ? 'available' : 'unavailable'
}

/**
 * Folds a `lend`/`renew_lending`/`return_immediate` response into the selected
 * reader: open lendings are added or replaced, closed ones removed.
 */
export const applyBagToReader = (
  reader: LendingReader,
  bag: HoldingLendingBag,
): LendingReader => {
  if (bag.lending?.userId !== reader.user.id) {
    return reader
  }

  const others = (reader.lendingInfo ?? []).filter(
    ({ holding }) => holding.id !== bag.holding.id,
  )

  return {
    ...reader,
    lendingInfo: isOpenLending(bag.lending) ? [...others, bag] : others,
  }
}

/** Replaces the matching holding in the current search results, if shown. */
export const applyBagToResults = (
  results: HoldingLendingBag[] | null,
  bag: HoldingLendingBag,
) =>
  results?.map((current) =>
    current.holding.id === bag.holding.id ? { ...current, ...bag } : current,
  ) ?? null

export const sumPendingFines = (
  fines: { value: number; payment?: string | null }[],
) =>
  fines
    .filter(({ payment }) => !payment)
    .reduce((sum, { value }) => sum + value, 0)

export const getReceiptLendingIds = (
  operations: SessionOperation[],
  readerId: number,
) => [
  ...new Set(
    operations
      .filter((operation) => operation.readerId === readerId)
      .map(({ lendingId }) => lendingId),
  ),
]
