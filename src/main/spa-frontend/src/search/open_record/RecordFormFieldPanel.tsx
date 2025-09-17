import { EuiFlexGroup, EuiPanel, EuiTitle } from '@elastic/eui'

import { isMarcDatafield } from '../../api-helpers/search/lib'
import { getLegacyMarcDatafieldTranslation } from '../../legacy_translations/lib'

import { FormFieldIndicators } from './FormFieldIndicators'

import type { FC } from 'react'

import type {
  MarcDatafield,
  MarcDatafieldValue,
} from '../../api-helpers/search/response-types'

type Props = {
  datafield: MarcDatafield
  datafieldValue: MarcDatafieldValue
}

const RecordFormFieldPanel: FC<Props> = ({ datafield, datafieldValue }) => {
  return (
    <EuiPanel>
      <EuiFlexGroup direction='column'>
        <EuiTitle size='xxs'>
          <h3>
            {`${getLegacyMarcDatafieldTranslation(datafield)} (${datafield})`}
          </h3>
        </EuiTitle>
        {isMarcDatafield(datafield) && (
          <FormFieldIndicators
            datafield={datafield}
            datafieldValue={datafieldValue}
          />
        )}
      </EuiFlexGroup>
    </EuiPanel>
  )
}

export default RecordFormFieldPanel
