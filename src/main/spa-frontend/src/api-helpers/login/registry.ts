import type { ParametrizedLegacyEndpointPayload } from '../types'

type LoginParametrizedLegacyEndpointPayload =
  | ParametrizedLegacyEndpointPayload<'login', 'login', 'username' | 'password'>
  | ParametrizedLegacyEndpointPayload<'login', 'session'>
  | ParametrizedLegacyEndpointPayload<'login', 'logout'>

declare module '../../registry' {
  interface ParametrizedLegacyEndpointPayloadRegistry {
    LoginParametrizedLegacyEndpointPayload: LoginParametrizedLegacyEndpointPayload
  }
}
