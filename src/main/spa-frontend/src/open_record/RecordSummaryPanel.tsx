import { EuiDescriptionList } from '@elastic/eui'

import { usePanelDescriptionListItems } from './lib'

import type { FC } from 'react'

import type { OpenResult } from '../api-helpers/search/response-types'

type Props = {
  record: OpenResult
}

const BibliographicRecordSummaryPanel: FC<Props> = ({ record }) => {
  return (
    <EuiDescriptionList
      compressed
      listItems={usePanelDescriptionListItems(record)}
      type='column'
    />
  )
}

export default BibliographicRecordSummaryPanel
