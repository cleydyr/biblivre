import { useQuery } from '@tanstack/react-query'

import { fetchSchemasList } from '.'

export const SCHEMAS_LIST_QUERY_KEY = ['schemas', 'list'] as const

export function useSchemasList() {
  return useQuery({
    queryKey: SCHEMAS_LIST_QUERY_KEY,
    queryFn: fetchSchemasList,
    select: (data) => data.data,
  })
}
