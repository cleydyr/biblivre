import { EuiFlexGroup } from '@elastic/eui'

import { isMarcDatafield } from '../api-helpers/search/lib'
import { useCataloguingFormFields } from '../cataloguing-form-fields/useCataloguingFormFields'

import RecordFormFieldPanel from './RecordFormFieldPanel'

import type { FC } from 'react'

import type {
  MarcDatafield,
  MarcField,
  OpenResult,
} from '../api-helpers/search/response-types'

type Props = {
  record: OpenResult
}

const BibliographicRecordFormPanel: FC<Props> = ({ record }) => {
  const { data: cataloguingFormFields, isSuccess } = useCataloguingFormFields()

  return (
    <EuiFlexGroup direction='column' gutterSize='s'>
      <div />
      {isSuccess &&
        cataloguingFormFields
          .filter((field) => field.datafield in record.json)
          .filter((field) => isMarcDatafield(field.datafield as MarcField))
          .map((field) => ({
            datafield: field.datafield as MarcDatafield,
            values: record.json[field.datafield as MarcDatafield],
            collapsed: field.collapsed,
          }))
          .map(({ datafield, values, collapsed }) =>
            values?.map((value, index) => (
              <RecordFormFieldPanel
                key={`${datafield}-${index}`}
                collapsed={collapsed}
                datafield={datafield}
                datafieldValue={value}
              />
            )),
          )}
    </EuiFlexGroup>
  )
}

export default BibliographicRecordFormPanel
