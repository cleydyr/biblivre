import { useState } from 'react'

const useLatch = () => {
  const [value, setValue] = useState(false)

  const latch = () => {
    if (value) {
      return
    }

    setValue(true)
  }

  const reset = () => {
    setValue(false)
  }

  return {
    value,
    latch,
    reset,
  }
}

export default useLatch
