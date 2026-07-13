import type { Moment } from 'moment'

export type UUID = ReturnType<(typeof crypto)['randomUUID']>

export type DateRange = {
  from: Moment | null
  to: Moment | null
}

export type Branded<T, Brand extends string> = T & { __brand: Brand }

export type ISO8601Date = Branded<string, 'iso8601'>
