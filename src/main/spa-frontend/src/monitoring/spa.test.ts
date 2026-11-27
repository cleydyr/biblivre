import { captureException } from '@sentry/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'

import { reportQueryError } from './spa'

vi.mock('@sentry/react', () => ({
  captureException: vi.fn(),
  init: vi.fn(),
  setTag: vi.fn(),
  setUser: vi.fn(),
}))

describe('reportQueryError', () => {
  beforeEach(() => vi.mocked(captureException).mockClear())

  it('reports unexpected errors', () => {
    const error = new Error('boom')

    reportQueryError(error, 'query')

    expect(captureException).toHaveBeenCalledWith(error, {
      tags: { 'react-query': 'query' },
    })
  })

  it('reports server errors', () => {
    reportQueryError({ status: 503 }, 'mutation')

    expect(captureException).toHaveBeenCalledTimes(1)
  })

  it('skips client errors such as validation or permission failures', () => {
    reportQueryError({ response: { status: 403 } }, 'mutation')
    reportQueryError({ status: 422 }, 'query')

    expect(captureException).not.toHaveBeenCalled()
  })
})
