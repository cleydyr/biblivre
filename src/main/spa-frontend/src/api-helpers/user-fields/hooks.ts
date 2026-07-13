import { useQuery } from '@tanstack/react-query'

import { Configuration, UserFieldApi } from '../../generated-sources'
import { BIBLIVRE_ENDPOINT } from '../constants'
import { defaultRestApiFetchOptions } from '../rest-api'

export const userFieldsQueryKey = () => ['user-fields'] as const

export function useUserFields() {
  const apiConfiguration = new Configuration({
    basePath: `${BIBLIVRE_ENDPOINT}/api/v2`,
  })

  const api = new UserFieldApi(apiConfiguration)

  return useQuery({
    queryKey: userFieldsQueryKey(),
    queryFn: async () => {
      const fields = await api.getUserFields(defaultRestApiFetchOptions)

      return [...fields].sort((left, right) => left.sortOrder - right.sortOrder)
    },
  })
}
