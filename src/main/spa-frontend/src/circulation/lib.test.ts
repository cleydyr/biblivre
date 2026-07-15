import moment from 'moment'
import { describe, expect, it } from 'vitest'

import { toISO8601Date } from './lib'

describe('toISO8601Date', () => {
  it('returns an empty string for null', () => {
    expect(toISO8601Date(null)).toBe('')
  })

  it('formats dates without milliseconds or a timezone suffix', () => {
    const date = moment('2020-01-01T15:30:45')

    expect(toISO8601Date(date)).toMatch(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/)
    expect(toISO8601Date(date)).not.toContain('Z')
    expect(toISO8601Date(date)).not.toContain('.')
  })

  it('normalizes the time to the start of the local day', () => {
    const withTime = moment('2020-06-15T15:30:00')
    const atMidnight = moment('2020-06-15T00:00:00')

    expect(toISO8601Date(withTime)).toBe(toISO8601Date(atMidnight))
  })

  it('does not mutate the original moment instance', () => {
    const date = moment('2020-01-01T15:30:00')

    toISO8601Date(date)

    expect(date.format('HH:mm:ss')).toBe('15:30:00')
  })
})
