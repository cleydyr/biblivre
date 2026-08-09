/*
Helper to create arrays conditionally.

[
    a,
    b,
    ...when(c.is_valid).element(() => c),
    ...when(lending).element((lending) => ({
        description: formatDate(lending.created),
    })),
]
*/

export type Falsy = false | 0 | 0n | '' | null | undefined

export type Truthy<T> = Exclude<T, Falsy>

type WhenResult<T> = {
  element: <U>(factory: (narrowed: Truthy<T>) => U) => [U] | []
  elements: <U>(factoryOrItems: ((narrowed: Truthy<T>) => U[]) | U[]) => U[]
}

export function when<T>(value: T): WhenResult<T> {
  return {
    element: <U>(factory: (narrowed: Truthy<T>) => U): [U] | [] => {
      if (!value) {
        return []
      }

      return [factory(value as Truthy<T>)]
    },
    elements: <U>(
      factoryOrItems: ((narrowed: Truthy<T>) => U[]) | U[],
    ): U[] => {
      if (!value) {
        return []
      }

      if (typeof factoryOrItems === 'function') {
        return factoryOrItems(value as Truthy<T>)
      }

      return factoryOrItems
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

export function unshiftOrReplace<T>(
  users: T[],
  user: T,
  predicate: (u1: T, u2: T) => boolean,
) {
  if (users.some((u) => predicate(u, user))) {
    return users.map((u) => (predicate(u, user) ? user : u))
  }

  return [user, ...users]
}

export function unshiftOrReplaceWithId<T extends { id: number }>(
  users: T[],
  user: T,
) {
  return unshiftOrReplace(users, user, (u1, u2) => u1.id === u2.id)
}
