import { fetchJSONFromLegacyEndpoint } from '..'

import type {
  CirculationUserSaveResponse,
  CirculationUsersSearchResponse,
  CirculationUserStatusChangeResponse,
  CirculationUserTabDataResponse,
} from './response-types'
import type {
  CirculationPaginatePayload,
  CirculationSearchPayload,
  CirculationUserSavePayload,
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

export const blockCirculationUser = async (
  userId: number,
): Promise<CirculationUserStatusChangeResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.user',
    action: 'block',
    user_id: String(userId),
  })
}

export const unblockCirculationUser = async (
  userId: number,
): Promise<CirculationUserStatusChangeResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.user',
    action: 'unblock',
    user_id: String(userId),
  })
}

export const deleteCirculationUser = async (
  userId: number,
): Promise<CirculationUserStatusChangeResponse> => {
  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.user',
    action: 'delete',
    id: String(userId),
  })
}

const BYPASS_KEYS = new Set(['id', 'name', 'type', 'status', 'photo_data'])

export const saveCirculationUser = async (
  payload: CirculationUserSavePayload,
): Promise<CirculationUserSaveResponse> => {
  const params: {
    id: string
    name: string
    type: string
    status: string
    photo_data?: string
  } & Record<string, string> = {
    id: String(payload.id),
    name: payload.name,
    type: String(payload.type),
    status: payload.status,
  }

  if (payload.photo_data) {
    params.photo_data = payload.photo_data
  }

  for (const [key, value] of Object.entries(payload)) {
    if (BYPASS_KEYS.has(key)) {
      continue
    }

    params[key] = String(value)
  }

  return fetchJSONFromLegacyEndpoint({
    module: 'circulation.user',
    action: 'save',
    ...params,
  })
}
