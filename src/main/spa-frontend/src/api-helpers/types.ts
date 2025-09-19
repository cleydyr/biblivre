import type { MODULES } from './constants'
import type { ACTIONS } from './search/constants'

export type LegacyModule = (typeof MODULES)[keyof typeof MODULES]

export type LegacyEndpointPayload =
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof ACTIONS.SEARCH,
      'search_parameters'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof ACTIONS.PAGINATE,
      'search_id' | 'page'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof ACTIONS.PAGINATE,
      'search_id' | 'page' | 'sort'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof ACTIONS.OPEN,
      'id'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof ACTIONS.EXPORT,
      'id_list'
    >
  | ParametrizedLegacyEndpointPayload<
      typeof MODULES.CATALOGING_BIBLIOGRAPHIC,
      typeof ACTIONS.DOWNLOAD_EXPORT,
      'id'
    >

type ParametrizedLegacyEndpointPayload<
  M extends string,
  A extends string,
  P extends string,
> = { [key in P]: string } & {
  module: M
  action: A
}

export type PaginateSearchParams = {
  searchId: string
  page: number
}
