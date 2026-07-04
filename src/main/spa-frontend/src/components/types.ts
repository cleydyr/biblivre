import type { EuiDescriptionListProps } from '@elastic/eui'

export type EuiDescriptionListItem = Exclude<
  EuiDescriptionListProps['listItems'],
  undefined
>[number]
