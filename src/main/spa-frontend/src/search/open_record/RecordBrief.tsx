import { EuiDescriptionList, EuiFlexGroup } from '@elastic/eui'

import { getLegacyMarcDatafieldTranslation } from '../../legacy_translations/lib'

import type { FC } from 'react'

import type { OpenResult } from '../../api-helpers/search/response-types'

type Props = {
  record: OpenResult
}

const RecordBrief: FC<Props> = ({ record }) => {
  return (
    <EuiFlexGroup direction='column'>
      <div />
      <EuiDescriptionList
        columnGutterSize='m'
        listItems={usePanelDescriptionListItems(record)}
        type='column'
      />
    </EuiFlexGroup>
  )
}

function usePanelDescriptionListItems(record: OpenResult) {
  return record.fields.map((field) => {
    return {
      title: getLegacyMarcDatafieldTranslation(field.datafield),
      description: field.value,
    }
  })
}
export default RecordBrief
