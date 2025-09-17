import { EuiFlexGroup, EuiPanel, EuiTitle } from '@elastic/eui'

import { isMarcDatafield } from '../../api-helpers/search/lib'
import { getLegacyMarcDatafieldTranslation } from '../../legacy_translations/lib'

import Datafield from './Datafield'

import type { FC } from 'react'

import type {
  MarcDatafield,
  MarcDatafieldValue,
} from '../../api-helpers/search/response-types'

type Props = {
  datafield: MarcDatafield
  datafieldValue: MarcDatafieldValue
  collapsed: boolean
}

const RecordFormFieldPanel: FC<Props> = ({ datafield, datafieldValue }) => {
  return (
    <EuiPanel>
      <EuiFlexGroup direction='column'>
        <EuiTitle size='s'>
          <h3>
            {`${getLegacyMarcDatafieldTranslation(datafield)} (${datafield})`}
          </h3>
        </EuiTitle>
        {isMarcDatafield(datafield) && (
          <Datafield datafield={datafield} datafieldValue={datafieldValue} />
        )}
      </EuiFlexGroup>
    </EuiPanel>
  )
}

export default RecordFormFieldPanel
