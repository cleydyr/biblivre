import { getLegacyRootUrl } from '../getLegacyLibraryUrl'

import { getStoredSchema } from './schema/storage'
import { BIBLIVRE_ENDPOINT, DEFAULT_HEADERS } from './constants'

import type { ParametrizedLegacyEndpointPayloadValues } from '../registry'

import type { FileDownload } from './types'

export function buildDefaultHeaders(): Record<string, string> {
  const headers: Record<string, string> = { ...DEFAULT_HEADERS }
  const schema = getStoredSchema()
  if (schema) {
    headers['X-Biblivre-Schema'] = schema
  }
  return headers
}

export function buildRestApiHeaders(): Record<string, string> {
  const headers: Record<string, string> = {}
  const schema = getStoredSchema()
  if (schema) {
    headers['X-Biblivre-Schema'] = schema
  }
  return headers
}

export async function fetchJSONFromLegacyEndpoint({
  module,
  action,
  ...otherParams
}: ParametrizedLegacyEndpointPayloadValues) {
  const response = await fetch(getLegacyRootUrl(BIBLIVRE_ENDPOINT), {
    method: 'POST',
    headers: buildDefaultHeaders(),
    body: new URLSearchParams({
      controller: 'json',
      module,
      action,
      ...otherParams,
    }).toString(),
  })

  // Legacy endpoints answer expected failures with HTTP 200 and success:false;
  // a 5xx is a server fault, so surface it instead of parsing an error page.
  if (response.status >= 500) {
    throw new LegacyEndpointError(module, action, response.status)
  }

  return response.json()
}

export class LegacyEndpointError extends Error {
  readonly status: number

  constructor(module: string, action: string, status: number) {
    super(`Legacy endpoint ${module}.${action} failed with HTTP ${status}`)
    this.name = 'LegacyEndpointError'
    this.status = status
  }
}

export async function downloadFromLegacyEndpoint({
  module,
  action,
  ...otherParams
}: ParametrizedLegacyEndpointPayloadValues): Promise<FileDownload> {
  const queryParams = new URLSearchParams({
    controller: 'download',
    module,
    action,
    ...otherParams,
  }).toString()

  const response = await fetch(
    `${getLegacyRootUrl(BIBLIVRE_ENDPOINT)}?${queryParams}`,
    {
      headers: buildDefaultHeaders(),
    },
  )

  return {
    blob: await response.blob(),
    filename: getDownloadFileName(response),
  }
}

function getDownloadFileName(response: Response) {
  return (
    response.headers
      .get('Content-Disposition')
      ?.match(/filename="(.+)"/)?.[1] ?? 'biblivre_download'
  )
}
