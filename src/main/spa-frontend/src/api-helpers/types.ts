import type { ISO8601Date } from '../types'

export type ParametrizedLegacyEndpointPayload<
  M extends string,
  A extends string,
  P extends string = never,
  R extends string = never,
> = { [key in P]: string } & { [key in R]?: string } & {
  module: M
  action: A
}

export type PaginateSearchParams = {
  searchId: string
  page: number
}

export type FileDownload = {
  blob: Blob
  filename: string
}

export type SuccessfulResponse = {
  success: true
}

export type SchemaListItem = {
  schema: string
  name: string
  subtitle?: string
}

export type SchemasListResponse = SuccessfulResponse & {
  data: SchemaListItem[]
}

/** Session probe from `login.session`; extends the usual JSON success payload. */
export type LoggedLoginSessionResponse = SuccessfulResponse & {
  logged: true
  username: string
}

export type UnloggedLoginSessionResponse = SuccessfulResponse & {
  logged: false
}

export type LoginSessionResponse =
  | LoggedLoginSessionResponse
  | UnloggedLoginSessionResponse

export type NonSuccessfulResponse = {
  success: false
  message_level: 'warning'
  message: string
}

export type SuccessfulPaginatedResponsePayload<T, U extends string = never> = {
  search: {
    id: number
    record_count: number
    record_limit: number
    records_per_page: number
    page: number
    page_count: number
    time: number
    data: T[]
  } & { [key in U]: string }
} & SuccessfulResponse

type NonSuccessfulPaginatedResponsePayload<U extends string = never> = {
  search: {
    record_count: number
    record_limit: number
    records_per_page: number
    page: number
    page_count: number
    time: number
  } & { [key in U]: string }
} & NonSuccessfulResponse

export type PaginatedResponsePayload<T, U extends string = never> =
  | SuccessfulPaginatedResponsePayload<T, U>
  | NonSuccessfulPaginatedResponsePayload<U>

// Matches AbstractDTO in Java
export type Auditable<T> = T & {
  createdBy: number
  created: ISO8601Date
  modifiedBy: number
  modified: ISO8601Date
}
