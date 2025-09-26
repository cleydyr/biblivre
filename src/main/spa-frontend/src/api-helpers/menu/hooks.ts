import { useQuery } from '@tanstack/react-query'

import checkMenuPermission from '.'

import type { ACTIONS } from './constants'

const useCheckMenuPermission = (
  action: (typeof ACTIONS)[keyof typeof ACTIONS],
) => {
  return useQuery({
    queryKey: ['menu', action],
    queryFn: async () => {
      const payload = await checkMenuPermission(action)

      return payload.success
    },
  })
}

export default useCheckMenuPermission
