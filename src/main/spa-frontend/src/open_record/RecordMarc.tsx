import { EuiCodeBlock } from '@elastic/eui'
import { useIntl } from 'react-intl'

import type { FC } from 'react'

type Props = {
  marc: string
}

const BibliographicRecordMarcPanel: FC<Props> = ({ marc }) => {
  const { formatMessage } = useIntl()

  return (
    <EuiCodeBlock
      isCopyable
      copyAriaLabel={formatMessage({
        defaultMessage: 'Copiar MARC para a área de transferência',
        id: 'bibliographic-record.marc.copy_aria_label',
      })}
    >
      {marc}
    </EuiCodeBlock>
  )
}

export default BibliographicRecordMarcPanel
