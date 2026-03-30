import type { MODULES } from './constants'
import type { ACTIONS as MENU_ACTIONS } from './menu/constants'
import type { ACTIONS as SEARCH_ACTIONS } from './search/constants'

export type LegacyModule = (typeof MODULES)[keyof typeof MODULES]

export type LegacyEndpointPayload =
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof SEARCH_ACTIONS.SEARCH,
      'search_parameters'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof SEARCH_ACTIONS.PAGINATE,
      'search_id' | 'page'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof SEARCH_ACTIONS.PAGINATE,
      'search_id' | 'page' | 'sort'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof SEARCH_ACTIONS.OPEN,
      'id'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof SEARCH_ACTIONS.EXPORT,
      'id_list'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof SEARCH_ACTIONS.DOWNLOAD_EXPORT,
      'id'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.MENU,
      typeof MENU_ACTIONS.ADMINISTRATION_CUSTOM_REPORTS
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.LOGIN,
      'login',
      'username' | 'password'
    >
  | ParametrizedLegacyEndpointPayload<typeof MODULES.LOGIN, 'session'>
  | ParametrizedLegacyEndpointPayload<typeof MODULES.LOGIN, 'logout'>
  | ParametrizedLegacyEndpointPayload<typeof MODULES.MULTI_SCHEMA, 'list'>

type ParametrizedLegacyEndpointPayload<
  M extends string,
  A extends string,
  P extends string = never,
> = { [key in P]: string } & {
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
