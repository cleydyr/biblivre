import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { act, renderHook, waitFor } from '@testing-library/react'
import type { ReactNode } from 'react'
import { useEffect } from 'react'
import { MemoryRouter, useSearchParams } from 'react-router-dom'
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'

import { AUTH_SESSION_QUERY_KEY } from '../login/hooks'
import { SCHEMA_STORAGE_KEY } from '../constants'
import type { SchemaListItem } from '../types'
import {
  SCHEMA_QUERY_PARAM,
  SHOW_SELECT_SCHEMA_QUERY_PARAM,
  useSchemaQueryParams,
} from './useSchemaQueryParams'

const schemaA: SchemaListItem = { schema: 'schema-a', name: 'Schema A' }
const schemaB: SchemaListItem = { schema: 'schema-b', name: 'Schema B' }

type RenderHookOptions = {
  initialRoute?: string
  schemas?: SchemaListItem[] | undefined
}

let capturedSearchParams = ''

function SearchParamsObserver() {
  const [searchParams] = useSearchParams()

  useEffect(() => {
    capturedSearchParams = searchParams.toString()
  }, [searchParams])

  return null
}

function createTestHarness(initialRoute = '/') {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  })

  const invalidateQueriesSpy = vi.spyOn(queryClient, 'invalidateQueries')

  const wrapper = ({ children }: { children: ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[initialRoute]}>
        <SearchParamsObserver />
        {children}
      </MemoryRouter>
    </QueryClientProvider>
  )

  return { invalidateQueriesSpy, wrapper }
}

function renderUseSchemaQueryParams({
  initialRoute = '/',
  schemas,
}: RenderHookOptions = {}) {
  const setIsSchemaModalOpen = vi.fn()
  const { invalidateQueriesSpy, wrapper } = createTestHarness(initialRoute)

  const rendered = renderHook(
    () =>
      useSchemaQueryParams({
        schemas,
        setIsSchemaModalOpen,
      }),
    { wrapper },
  )

  return { ...rendered, setIsSchemaModalOpen, invalidateQueriesSpy }
}

describe('useSchemaQueryParams', () => {
  beforeEach(() => {
    localStorage.clear()
    capturedSearchParams = ''
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('initial state', () => {
    it('initializes activeSchemaId from localStorage', () => {
      localStorage.setItem(SCHEMA_STORAGE_KEY, schemaA.schema)

      const { result } = renderUseSchemaQueryParams()

      expect(result.current.activeSchemaId).toBe(schemaA.schema)
    })

    it('starts with null activeSchemaId when nothing is stored', () => {
      const { result } = renderUseSchemaQueryParams()

      expect(result.current.activeSchemaId).toBeNull()
    })
  })

  describe('applyActiveSchema', () => {
    it('persists schema, updates activeSchemaId, and invalidates queries', async () => {
      const { result, invalidateQueriesSpy } = renderUseSchemaQueryParams({
        schemas: [schemaA, schemaB],
      })

      await act(async () => {
        result.current.applyActiveSchema(schemaB.schema)
      })

      expect(result.current.activeSchemaId).toBe(schemaB.schema)
      expect(localStorage.getItem(SCHEMA_STORAGE_KEY)).toBe(schemaB.schema)
      expect(invalidateQueriesSpy).toHaveBeenCalled()
    })
  })

  describe('single schema', () => {
    it('auto-selects the only schema', async () => {
      const { result } = renderUseSchemaQueryParams({
        schemas: [schemaA],
      })

      await waitFor(() => {
        expect(result.current.activeSchemaId).toBe(schemaA.schema)
      })
    })

    it('syncs localStorage and invalidates auth session when stored schema differs', async () => {
      localStorage.setItem(SCHEMA_STORAGE_KEY, 'stale-schema')

      const { invalidateQueriesSpy } = renderUseSchemaQueryParams({
        schemas: [schemaA],
      })

      await waitFor(() => {
        expect(localStorage.getItem(SCHEMA_STORAGE_KEY)).toBe(schemaA.schema)
      })

      expect(invalidateQueriesSpy).toHaveBeenCalledWith({
        queryKey: AUTH_SESSION_QUERY_KEY,
      })
    })

    it('does not invalidate auth session when stored schema already matches', async () => {
      localStorage.setItem(SCHEMA_STORAGE_KEY, schemaA.schema)

      const { invalidateQueriesSpy } = renderUseSchemaQueryParams({
        schemas: [schemaA],
      })

      await waitFor(() => {
        expect(localStorage.getItem(SCHEMA_STORAGE_KEY)).toBe(schemaA.schema)
      })

      expect(invalidateQueriesSpy).not.toHaveBeenCalledWith({
        queryKey: AUTH_SESSION_QUERY_KEY,
      })
    })
  })

  describe('multiple schemas', () => {
    it('keeps activeSchemaId aligned with stored schema', async () => {
      localStorage.setItem(SCHEMA_STORAGE_KEY, schemaB.schema)

      const { result } = renderUseSchemaQueryParams({
        schemas: [schemaA, schemaB],
      })

      await waitFor(() => {
        expect(result.current.activeSchemaId).toBe(schemaB.schema)
      })
    })

    it('opens the schema modal when no schema is stored', async () => {
      const { setIsSchemaModalOpen } = renderUseSchemaQueryParams({
        schemas: [schemaA, schemaB],
      })

      await waitFor(() => {
        expect(setIsSchemaModalOpen).toHaveBeenCalledWith(true)
      })
    })

    it('does not open the schema modal when a schema is already stored', async () => {
      localStorage.setItem(SCHEMA_STORAGE_KEY, schemaA.schema)

      const { setIsSchemaModalOpen } = renderUseSchemaQueryParams({
        schemas: [schemaA, schemaB],
      })

      await waitFor(() => {
        expect(setIsSchemaModalOpen).not.toHaveBeenCalled()
      })
    })
  })

  describe('schema query param', () => {
    it('applies a matching schema from the URL and removes the query param', async () => {
      const { result, invalidateQueriesSpy } = renderUseSchemaQueryParams({
        initialRoute: `/?${SCHEMA_QUERY_PARAM}=${schemaB.schema}`,
        schemas: [schemaA, schemaB],
      })

      await waitFor(() => {
        expect(result.current.activeSchemaId).toBe(schemaB.schema)
      })

      expect(localStorage.getItem(SCHEMA_STORAGE_KEY)).toBe(schemaB.schema)
      expect(invalidateQueriesSpy).toHaveBeenCalled()
      await waitFor(() => {
        expect(capturedSearchParams).not.toContain(`${SCHEMA_QUERY_PARAM}=`)
      })
    })

    it('ignores schema query param when it does not match any schema', async () => {
      const { result, setIsSchemaModalOpen } = renderUseSchemaQueryParams({
        initialRoute: `/?${SCHEMA_QUERY_PARAM}=unknown-schema`,
        schemas: [schemaA, schemaB],
      })

      await waitFor(() => {
        expect(setIsSchemaModalOpen).toHaveBeenCalledWith(true)
      })

      expect(result.current.activeSchemaId).toBeNull()
      expect(localStorage.getItem(SCHEMA_STORAGE_KEY)).toBeNull()
    })

    it('does nothing when schemas are not loaded yet', () => {
      const { result, setIsSchemaModalOpen } = renderUseSchemaQueryParams({
        initialRoute: `/?${SCHEMA_QUERY_PARAM}=${schemaA.schema}`,
        schemas: undefined,
      })

      expect(result.current.activeSchemaId).toBeNull()
      expect(setIsSchemaModalOpen).not.toHaveBeenCalled()
      expect(localStorage.getItem(SCHEMA_STORAGE_KEY)).toBeNull()
    })
  })

  describe('showSelectSchema query param', () => {
    it('clears stored schema, resets active schema, and removes query params', async () => {
      localStorage.setItem(SCHEMA_STORAGE_KEY, schemaA.schema)

      const { result, invalidateQueriesSpy } = renderUseSchemaQueryParams({
        initialRoute: `/?${SHOW_SELECT_SCHEMA_QUERY_PARAM}=1&${SCHEMA_QUERY_PARAM}=${schemaA.schema}`,
        schemas: [schemaA, schemaB],
      })

      await waitFor(() => {
        expect(result.current.activeSchemaId).toBeNull()
      })

      expect(localStorage.getItem(SCHEMA_STORAGE_KEY)).toBeNull()
      expect(invalidateQueriesSpy).toHaveBeenCalled()
      await waitFor(() => {
        expect(capturedSearchParams).toBe('')
      })
    })

    it('opens the schema modal when multiple schemas are available', async () => {
      const { setIsSchemaModalOpen } = renderUseSchemaQueryParams({
        initialRoute: `/?${SHOW_SELECT_SCHEMA_QUERY_PARAM}=1`,
        schemas: [schemaA, schemaB],
      })

      await waitFor(() => {
        expect(setIsSchemaModalOpen).toHaveBeenCalledWith(true)
      })
    })

    it('does not open the schema modal when only one schema exists', async () => {
      const { setIsSchemaModalOpen } = renderUseSchemaQueryParams({
        initialRoute: `/?${SHOW_SELECT_SCHEMA_QUERY_PARAM}=1`,
        schemas: [schemaA],
      })

      await waitFor(() => {
        expect(localStorage.getItem(SCHEMA_STORAGE_KEY)).toBe(schemaA.schema)
      })

      expect(setIsSchemaModalOpen).not.toHaveBeenCalled()
    })

    it('prevents the auto-prompt modal after handling showSelectSchema', async () => {
      const { setIsSchemaModalOpen } = renderUseSchemaQueryParams({
        initialRoute: `/?${SHOW_SELECT_SCHEMA_QUERY_PARAM}=1`,
        schemas: [schemaA, schemaB],
      })

      await waitFor(() => {
        expect(setIsSchemaModalOpen).toHaveBeenCalledTimes(1)
        expect(setIsSchemaModalOpen).toHaveBeenCalledWith(true)
      })
    })
  })
})
