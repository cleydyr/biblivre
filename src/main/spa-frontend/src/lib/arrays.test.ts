import { describe, expect, it, vi } from 'vitest'

import { when } from './arrays'

describe('when', () => {
  describe('element', () => {
    it('includes the factory result when the value is truthy', () => {
      expect(
        when('tombo').element((accessionNumber) => accessionNumber),
      ).toEqual(['tombo'])
    })

    it('returns an empty array when the value is falsy', () => {
      expect(when('').element((accessionNumber) => accessionNumber)).toEqual([])
      expect(when(undefined).element((value) => value)).toEqual([])
      expect(when(null).element((value) => value)).toEqual([])
      expect(when(0).element((value) => value)).toEqual([])
      expect(when(false).element(() => 'x')).toEqual([])
    })

    it('does not call the factory when the value is falsy', () => {
      const factory = vi.fn((value: string) => value)

      when('').element(factory)

      expect(factory).not.toHaveBeenCalled()
    })

    it('narrows object values for the factory', () => {
      const maybeLending: { created: string } | undefined = {
        created: '2026-01-01',
      }

      expect(when(maybeLending).element((lending) => lending.created)).toEqual([
        '2026-01-01',
      ])
    })
  })

  describe('elements', () => {
    it('spreads an array when the value is truthy', () => {
      expect(when(true).elements(['a', 'b'])).toEqual(['a', 'b'])
    })

    it('returns an empty array when the value is falsy', () => {
      expect(when(false).elements(['a', 'b'])).toEqual([])
    })

    it('supports a factory that receives the narrowed value', () => {
      expect(when({ id: 7 }).elements((record) => [`id-${record.id}`])).toEqual(
        ['id-7'],
      )
    })
  })
})
