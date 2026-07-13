import type { ParametrizedLegacyEndpointPayload } from '../types'

import type { ACTIONS } from './constants'

type MenuParametrizedLegacyEndpointPayload = ParametrizedLegacyEndpointPayload<
  'menu',
  (typeof ACTIONS)[keyof typeof ACTIONS]
>

declare module '../../registry' {
  interface ParametrizedLegacyEndpointPayloadRegistry {
    MenuParametrizedLegacyEndpointPayload: MenuParametrizedLegacyEndpointPayload
  }
}
