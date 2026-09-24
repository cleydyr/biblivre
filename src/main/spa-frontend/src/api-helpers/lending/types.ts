export type LendingHoldingSearchPayload = {
  query: string
  holding_list_lendings?: boolean
}

export type LendingUserSearchPayload = {
  query: string
  field?: string
  page?: number
}

export type CreateLendingPayload = {
  holdingId: number
  userId: number
}
