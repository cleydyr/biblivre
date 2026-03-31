import { getStoredSchema } from './schema/storage'
import { DEFAULT_HEADERS } from './constants'

import type { FileDownload, LegacyEndpointPayload } from './types'

export function buildDefaultHeaders(): Record<string, string> {
  const headers: Record<string, string> = { ...DEFAULT_HEADERS }
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
}: LegacyEndpointPayload) {
  const response = await fetch(import.meta.env.VITE_BIBLIVRE_ENDPOINT, {
    method: 'POST',
    headers: buildDefaultHeaders(),
    body: new URLSearchParams({
      controller: 'json',
      module,
      action,
      ...otherParams,
    }).toString(),
  })

  return response.json()
}

export async function downloadFromLegacyEndpoint({
  module,
  action,
  ...otherParams
}: LegacyEndpointPayload): Promise<FileDownload> {
  const queryParams = new URLSearchParams({
    controller: 'download',
    module,
    action,
    ...otherParams,
  }).toString()

  const response = await fetch(
    `${import.meta.env.VITE_BIBLIVRE_ENDPOINT}?${queryParams}`,
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
