import { useQuery } from '@tanstack/react-query'

import { Configuration, UserTypeApi } from '../../generated-sources'
import { BIBLIVRE_ENDPOINT } from '../constants'
import { defaultRestApiFetchOptions } from '../rest-api'

export const userTypeQueryKey = (id: number) => ['user-type', id] as const

export const userTypesQueryKey = () => ['user-types'] as const

export function useUserTypes() {
  const apiConfiguration = new Configuration({
    basePath: `${BIBLIVRE_ENDPOINT}/api/v2`,
  })

  const api = new UserTypeApi(apiConfiguration)

  return useQuery({
    queryKey: userTypesQueryKey(),
    queryFn: () => api.getUserTypes(defaultRestApiFetchOptions),
  })
}

export function useUserType(id: number | undefined) {
  const apiConfiguration = new Configuration({
    basePath: `${BIBLIVRE_ENDPOINT}/api/v2`,
  })

  const api = new UserTypeApi(apiConfiguration)

  return useQuery({
    queryKey: userTypeQueryKey(id ?? 0),
    queryFn: () => api.getUserType({ id: id! }, defaultRestApiFetchOptions),
    enabled: id != null,
  })
}
