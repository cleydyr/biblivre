import type { Moment } from 'moment'

export type UUID = ReturnType<(typeof crypto)['randomUUID']>

export type DateRange = {
  from: Moment | null
  to: Moment | null
}
