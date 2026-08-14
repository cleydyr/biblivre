import { afterEach, describe, expect, it, vi } from 'vitest'

import { getLegacyRootUrl } from '../getLegacyLibraryUrl'

import { BIBLIVRE_ENDPOINT } from './constants'
import { downloadFromLegacyEndpoint, fetchJSONFromLegacyEndpoint } from '.'

describe('legacy form endpoint', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('posts JSON actions to the context root with a trailing slash', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      json: async () => ({ success: true }),
    })
    vi.stubGlobal('fetch', fetchMock)

    await fetchJSONFromLegacyEndpoint({
      module: 'multi_schema',
      action: 'list',
    })

    expect(fetchMock).toHaveBeenCalledWith(
      getLegacyRootUrl(BIBLIVRE_ENDPOINT),
      expect.objectContaining({ method: 'POST' }),
    )
    expect(String(fetchMock.mock.calls[0][0])).toMatch(/\/$/)
  })

  it('downloads from the context root with a trailing slash', async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      blob: async () => new Blob(),
      headers: { get: () => null },
    })
    vi.stubGlobal('fetch', fetchMock)

    await downloadFromLegacyEndpoint({
      module: 'cataloging.bibliographic',
      action: 'download_export',
      id: 'export-id',
    })

    const requestedUrl = String(fetchMock.mock.calls[0][0])
    expect(requestedUrl.startsWith(getLegacyRootUrl(BIBLIVRE_ENDPOINT))).toBe(
      true,
    )
    expect(requestedUrl).toContain('controller=download')
  })
})
