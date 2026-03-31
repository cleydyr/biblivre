import { useCallback, useState } from 'react'

const useToggle = (
  initialValue: boolean | (() => boolean),
): [boolean, () => void, (value: boolean) => void] => {
  const [value, setValue] = useState<boolean>(initialValue)

  const toggle = useCallback(() => {
    setValue((prev) => !prev)
  }, [])

  return [value, toggle, setValue]
}

export default useToggle
