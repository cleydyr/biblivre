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

export type NonSuccessfulResponse = {
  success: false
  message_level: 'warning'
  message: string
}
