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

export function elements<T>(args: T[]): {
  if: (condition: boolean) => T[]
} {
  return {
    if: (condition: boolean): T[] | [] => {
      if (condition) {
        return args
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
