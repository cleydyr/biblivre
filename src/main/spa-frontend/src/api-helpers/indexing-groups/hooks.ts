import { useQuery } from '@tanstack/react-query'

import { Configuration, IndexingGroupApi } from '../../generated-sources'
import { BIBLIVRE_ENDPOINT } from '../constants'
import { defaultRestApiFetchOptions } from '../rest-api'

export const indexingGroupsQueryKey = (recordType: string) =>
  ['indexing-groups', recordType] as const

export function useIndexingGroups(recordType: string) {
  const apiConfiguration = new Configuration({
    basePath: `${BIBLIVRE_ENDPOINT}/api/v2`,
  })

  const api = new IndexingGroupApi(apiConfiguration)

  return useQuery({
    queryKey: indexingGroupsQueryKey(recordType),
    queryFn: () =>
      api.getIndexingGroups({ recordType }, defaultRestApiFetchOptions),
  })
}

export function useBibliographicIndexingGroups() {
  return useIndexingGroups('biblio')
}
