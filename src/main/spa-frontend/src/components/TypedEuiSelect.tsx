import { EuiSelect } from '@elastic/eui'

import type { EuiSelectOption } from '@elastic/eui'
import type { ChangeEvent, ComponentProps, EventHandler } from 'react'

export type TypedEuiSelectOption<T extends string> = Omit<
  EuiSelectOption,
  'value'
> & {
  value: T
}

export type TypedSelectProps<T extends string> = Omit<
  ComponentProps<typeof EuiSelect>,
  'value' | 'options' | 'onChange'
> & {
  value: T
  options: TypedEuiSelectOption<T>[]
  onChange: EventHandler<
    ChangeEvent<
      Omit<HTMLSelectElement, 'value'> & {
        value: T
      }
    >
  >
}

const TypedEuiSelect = <T extends string>(props: TypedSelectProps<T>) => {
  return <EuiSelect {...props} />
}

export default TypedEuiSelect
