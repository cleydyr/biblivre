import { useQuery } from '@tanstack/react-query'

import { Configuration, UserFieldApi } from '../../generated-sources'
import { BIBLIVRE_ENDPOINT } from '../constants'
import { defaultRestApiFetchOptions } from '../rest-api'

export const userSearchableFieldsQueryKey = () =>
  ['user-searchable-fields'] as const

export function useUserSearchableFields() {
  const apiConfiguration = new Configuration({
    basePath: `${BIBLIVRE_ENDPOINT}/api/v2`,
  })

  const api = new UserFieldApi(apiConfiguration)

  return useQuery({
    queryKey: userSearchableFieldsQueryKey(),
    queryFn: () => api.getUserSearchableFields(defaultRestApiFetchOptions),
  })
}
