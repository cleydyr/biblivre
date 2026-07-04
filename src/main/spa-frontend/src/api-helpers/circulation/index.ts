import { fetchJSONFromLegacyEndpoint } from '..'

import type {
  CirculationUsersSearchResponse,
  CirculationUserTabDataResponse,
} from './response-types'
import type {
  CirculationPaginatePayload,
  CirculationSearchPayload,
  CirculationUserTab,
} from './types'

export const getCirculationUsersSearch = async (
  payload: CirculationSearchPayload,
): Promise<CirculationUsersSearchResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.user',
    action: 'search',
    search_parameters: JSON.stringify(payload),
  })
}

export const getCirculationUsersPaginate = async (
  payload: CirculationPaginatePayload,
): Promise<CirculationUsersSearchResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.user',
    action: 'paginate',
    search_parameters: JSON.stringify(payload),
    page: String(payload.page),
  })
}

export const getCirculationUserTabData = async <T extends CirculationUserTab>(
  userId: number,
  tab: T,
): Promise<CirculationUserTabDataResponse<T>> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.user',
    action: 'load_tab_data',
    id: String(userId),
    tab,
  })
}
