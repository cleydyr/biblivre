import { describe, expect, it } from 'vitest'

import { parseFeatureOverrides } from './features'

describe('parseFeatureOverrides', () => {
  it('returns an empty set when the variable is missing', () => {
    expect(parseFeatureOverrides(undefined).size).toBe(0)
  })

  it('splits comma-separated names and ignores blanks', () => {
    expect([
      ...parseFeatureOverrides(
        ' circulation_lending_prototype, ,search_intelligent ',
      ),
    ]).toEqual(['circulation_lending_prototype', 'search_intelligent'])
  })
})
