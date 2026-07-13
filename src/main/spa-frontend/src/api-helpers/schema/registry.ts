import type { ParametrizedLegacyEndpointPayload } from '../types'

type SchemaParametrizedLegacyEndpointPayload =
  ParametrizedLegacyEndpointPayload<'multi_schema', 'list'>

declare module '../../registry' {
  interface ParametrizedLegacyEndpointPayloadRegistry {
    SchemaParametrizedLegacyEndpointPayload: SchemaParametrizedLegacyEndpointPayload
  }
}
