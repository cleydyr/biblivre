/**
 * 1. The Central Registry
 * This interface is meant to be blank here. Other files will
 * use "declaration merging" to add properties to it dynamically.
 */
// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export interface ParametrizedLegacyEndpointPayloadRegistry {}

export type ParametrizedLegacyEndpointPayloadKeys =
  keyof ParametrizedLegacyEndpointPayloadRegistry

export type ParametrizedLegacyEndpointPayloadValues =
  ParametrizedLegacyEndpointPayloadRegistry[ParametrizedLegacyEndpointPayloadKeys]
