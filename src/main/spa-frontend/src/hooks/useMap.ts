import { useState } from 'react'

class ObservableMap<K, V> extends Map<K, V> {
  observer: (map: Map<K, V>) => void

  constructor(initialMap: Map<K, V>, observer: (map: Map<K, V>) => void) {
    super()

    this.observer = observer

    initialMap.forEach((value, key) => {
      super.set(key, value)
    })
  }

  set(key: K, value: V): this {
    const result = super.set(key, value)

    this.observer(this)

    return result
  }

  clear(): void {
    super.clear()

    this.observer(this)
  }

  delete(key: K): boolean {
    const result = super.delete(key)

    this.observer(this)

    return result
  }
}

function useMap<K, V>(
  args: ConstructorParameters<typeof Map<K, V>>[0] = []
): InstanceType<typeof Map<K, V>> {
  const [map, setMap] = useState(new Map<K, V>(args))

  return new ObservableMap<K, V>(map, setMap)
}

export default useMap
