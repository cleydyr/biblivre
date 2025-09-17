import { useQuery, type UseQueryResult } from '@tanstack/react-query'

import cataloguingFormFields from './index.json'

import type { CataloguingFormField } from './types'

export function useCataloguingFormFields(): UseQueryResult<
  CataloguingFormField[]
> {
  return useQuery({
    queryKey: ['cataloguing-form-fields'],
    queryFn: () => cataloguingFormFields,
  })
}
