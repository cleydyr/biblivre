import { useQueryClient } from '@tanstack/react-query'
import { useCallback, useEffect, useRef, useState } from 'react'
import { useSearchParams } from 'react-router-dom'

import { clearStoredSchema, getStoredSchema, setStoredSchema } from './storage'

import type { SchemaListItem } from '../types'
import { AUTH_SESSION_QUERY_KEY } from '../login/hooks'

export const SCHEMA_QUERY_PARAM = 'schema'
export const SHOW_SELECT_SCHEMA_QUERY_PARAM = 'showSelectSchema'

type UseSchemaQueryParamsOptions = {
  schemas: SchemaListItem[] | undefined
  setIsSchemaModalOpen: (open: boolean) => void
}

type UseSchemaQueryParamsBag = {
  activeSchemaId: string | null
  applyActiveSchema: (schemaId: string) => void
}

export function useSchemaQueryParams({
  schemas,
  setIsSchemaModalOpen,
}: UseSchemaQueryParamsOptions): UseSchemaQueryParamsBag {
  const schemaAutoPromptedRef = useRef(false)
  
  const [activeSchemaId, setActiveSchemaId] = useState<string | null>(() =>
    getStoredSchema(),
  )
  
  
  const queryClient = useQueryClient()
  
  const [searchParams, setSearchParams] = useSearchParams()

  const applyActiveSchema = useCallback(
    (schemaId: string) => {
      setStoredSchema(schemaId)
      setActiveSchemaId(schemaId)
      void queryClient.invalidateQueries()
    },
    [queryClient],
  )

  const removeQueryParams = useCallback(
    (...keys: string[]) => {
      const next = new URLSearchParams(searchParams)
      let changed = false

      for (const key of keys) {
        if (next.has(key)) {
          next.delete(key)
          changed = true
        }
      }

      if (changed) {
        setSearchParams(next, { replace: true })
      }
    },
    [searchParams, setSearchParams],
  )

  useEffect(() => {
    if (!schemas?.length) {
      return
    }

    const schemaFromQuery = searchParams.get(SCHEMA_QUERY_PARAM)
    const showSelectSchemaFromQuery = searchParams.has(
      SHOW_SELECT_SCHEMA_QUERY_PARAM,
    )

    if (showSelectSchemaFromQuery) {
      clearStoredSchema()
      setActiveSchemaId(null)
      void queryClient.invalidateQueries()
      schemaAutoPromptedRef.current = true
      removeQueryParams(SHOW_SELECT_SCHEMA_QUERY_PARAM, SCHEMA_QUERY_PARAM)

      if (schemas.length > 1) {
        setIsSchemaModalOpen(true)
      }

      return
    }

    if (schemaFromQuery) {
      const matchedSchema = schemas.find((s) => s.schema === schemaFromQuery)
      if (matchedSchema) {
        schemaAutoPromptedRef.current = true
        applyActiveSchema(matchedSchema.schema)
        removeQueryParams(SCHEMA_QUERY_PARAM)
      }
    }
  }, [
    applyActiveSchema,
    queryClient,
    removeQueryParams,
    schemaAutoPromptedRef,
    schemas,
    searchParams,
    setActiveSchemaId,
    setIsSchemaModalOpen,
  ])

  useEffect(() => {
    if (!schemas || schemas.length <= 1 || schemaAutoPromptedRef.current) {
      return
    }
    if (!getStoredSchema()) {
      schemaAutoPromptedRef.current = true
      setIsSchemaModalOpen(true)
    }
  }, [schemas])

  useEffect(() => {
    if (!schemas?.length) {
      return
    }

    if (schemas.length === 1) {
      const only = schemas[0].schema

      if (getStoredSchema() !== only) {
        setStoredSchema(only)
        void queryClient.invalidateQueries({ queryKey: AUTH_SESSION_QUERY_KEY })
      }

      setActiveSchemaId(only)

      return
    }

    setActiveSchemaId(getStoredSchema())
  }, [queryClient, schemas])

  return { activeSchemaId, applyActiveSchema }
}
