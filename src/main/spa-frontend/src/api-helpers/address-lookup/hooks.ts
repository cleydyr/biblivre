import { useMutation, useQuery } from '@tanstack/react-query'

import { AddressLookupApi, Configuration } from '../../generated-sources'
import { BIBLIVRE_ENDPOINT } from '../constants'
import { defaultRestApiFetchOptions } from '../rest-api'

function createAddressLookupApi() {
  return new AddressLookupApi(
    new Configuration({
      basePath: `${BIBLIVRE_ENDPOINT}/api/v2`,
    }),
  )
}

export const addressLookupEnabledQueryKey = () =>
  ['address-lookup-enabled'] as const

export function useAddressLookupEnabled() {
  const api = createAddressLookupApi()

  return useQuery({
    queryKey: addressLookupEnabledQueryKey(),
    queryFn: async () => {
      const response = await api.getAddressLookupEnabled(
        defaultRestApiFetchOptions,
      )
      return response.enabled
    },
  })
}

export function useLookupAddressByCepMutation() {
  const api = createAddressLookupApi()

  return useMutation({
    mutationFn: (cep: string) =>
      api.lookupAddressByCep({ cep }, defaultRestApiFetchOptions),
  })
}
