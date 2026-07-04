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

declare module '../../registry' {
  interface ParametrizedLegacyEndpointPayloadRegistry {
    CirculationParametrizedLegacySearchPayload: CirculationParametrizedLegacySearchPayload
    CirculationParametrizedLegacyPaginatePayload: CirculationParametrizedLegacyPaginatePayload
    CirculationParametrizedLegacyLoadTabDataPayload: CirculationParametrizedLegacyLoadTabDataPayload
  }
}
