import type { Moment } from 'moment'

// TODO: move to a shared type file
export type UUID = ReturnType<(typeof crypto)['randomUUID']>

export type DateRange = {
  from: Moment | null
  to: Moment | null
}
