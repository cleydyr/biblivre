import { useQuery } from '@tanstack/react-query'

import cataloguingFormFields from './index.json'

import type { UseQueryResult } from '@tanstack/react-query'

import type { CataloguingFormField } from './types'

export function useCataloguingFormFields(): UseQueryResult<
  CataloguingFormField[]
> {
  return useQuery({
    queryKey: ['cataloguing-form-fields'],
    queryFn: () => cataloguingFormFields,
  })
}
