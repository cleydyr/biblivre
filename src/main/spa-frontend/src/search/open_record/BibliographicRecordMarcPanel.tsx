import { EuiCodeBlock } from '@elastic/eui'

import type { FC } from 'react'

type Props = {
  marc: string
}

const BibliographicRecordMarcPanel: FC<Props> = ({ marc }) => {
  return <EuiCodeBlock>{marc}</EuiCodeBlock>
}

export default BibliographicRecordMarcPanel
