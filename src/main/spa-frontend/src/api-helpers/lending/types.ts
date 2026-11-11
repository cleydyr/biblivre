export type LendingHoldingSearchPayload = {
  query: string
  holding_list_lendings?: boolean
  /** 1-based page; the backend defaults to the first page */
  page?: number
}

export type LendingUserSearchPayload = {
  query: string
  /** 1-based page; the backend defaults to the first page */
  page?: number
}
