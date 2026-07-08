import type { ParametrizedLegacyEndpointPayload } from '../types'

type CirculationParametrizedLegacySearchPayload =
  ParametrizedLegacyEndpointPayload<
    'circulation.user',
    'search',
    'search_parameters'
  >

type CirculationParametrizedLegacyPaginatePayload =
  ParametrizedLegacyEndpointPayload<
    'circulation.user',
    'paginate',
    'search_parameters' | 'page'
  >

type CirculationParametrizedLegacyLoadTabDataPayload =
  ParametrizedLegacyEndpointPayload<
    'circulation.user',
    'load_tab_data',
    'id' | 'tab'
  >

type CirculationParametrizedLegacyBlockUserPayload =
  ParametrizedLegacyEndpointPayload<'circulation.user', 'block', 'user_id'>

type CirculationParametrizedLegacyUnblockUserPayload =
  ParametrizedLegacyEndpointPayload<'circulation.user', 'unblock', 'user_id'>

type CirculationParametrizedLegacyDeleteUserPayload =
  ParametrizedLegacyEndpointPayload<'circulation.user', 'delete', 'id'>

declare module '../../registry' {
  interface ParametrizedLegacyEndpointPayloadRegistry {
    CirculationParametrizedLegacySearchPayload: CirculationParametrizedLegacySearchPayload
    CirculationParametrizedLegacyPaginatePayload: CirculationParametrizedLegacyPaginatePayload
    CirculationParametrizedLegacyLoadTabDataPayload: CirculationParametrizedLegacyLoadTabDataPayload
    CirculationParametrizedLegacyBlockUserPayload: CirculationParametrizedLegacyBlockUserPayload
    CirculationParametrizedLegacyUnblockUserPayload: CirculationParametrizedLegacyUnblockUserPayload
    CirculationParametrizedLegacyDeleteUserPayload: CirculationParametrizedLegacyDeleteUserPayload
  }
}
