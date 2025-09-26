import { MODULES } from '../constants'
import { fetchJSONFromLegacyEndpoint } from '..'

import type { NonSuccessfulResponse, SuccessfulResponse } from '../types'

import type { ACTIONS } from './constants'

const checkMenuPermission = async (
  action: (typeof ACTIONS)[keyof typeof ACTIONS],
): Promise<SuccessfulResponse | NonSuccessfulResponse> => {
  const result = await fetchJSONFromLegacyEndpoint({
    module: MODULES.MENU,
    action,
  })

  return result
}

export default checkMenuPermission
