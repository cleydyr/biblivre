/*
Helper to create arrays conditionally.

Example:

[
    a,
    b,
    ...element(c).if(c.is_valid),
    ...element(x).if(x.is_valid),
]
*/

export function element<T>(arg: T): {
  if: (condition: boolean) => [T] | []
} {
  return {
    if: (condition: boolean): [T] | [] => {
      if (condition) {
        return [arg]
      }
      return []
    },
  }
}

export function partition<T>(
  array: T[],
  predicate: (item: T) => boolean,
): [T[], T[]] {
  return array.reduce<[T[], T[]]>(
    (acc, item) => {
      const [trueItems, falseItems] = acc

      if (predicate(item)) {
        trueItems.push(item)
      } else {
        falseItems.push(item)
      }
      return acc
    },
    [[], []],
  )
}
