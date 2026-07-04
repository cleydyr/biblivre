import { EuiText, useEuiTheme } from '@elastic/eui'

import type { CSSProperties, FC, ReactNode } from 'react'

type Props = {
  name: ReactNode
  subtitle?: string | null
  /** Max width before the subtitle truncates with ellipsis. */
  subtitleMaxWidth?: CSSProperties['maxWidth']
  titleSize?: 'xs' | 's' | 'm'
}

const LibraryHeading: FC<Props> = ({
  name,
  subtitle,
  subtitleMaxWidth = '240px',
  titleSize = 's',
}) => {
  const { euiTheme } = useEuiTheme()
  const trimmedSubtitle = subtitle?.trim()

  return (
    <div css={{ minWidth: 0, maxWidth: subtitleMaxWidth }}>
      <EuiText
        css={{ fontWeight: euiTheme.font.weight.semiBold }}
        size={titleSize}
      >
        {name}
      </EuiText>
      {trimmedSubtitle ? (
        <EuiText
          css={{
            color: euiTheme.colors.textSubdued,
            fontSize: euiTheme.size.s,
            lineHeight: euiTheme.font.lineHeightMultiplier,
            marginTop: euiTheme.size.xxs,
            maxWidth: subtitleMaxWidth,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
          }}
          size='xs'
          title={trimmedSubtitle}
        >
          {trimmedSubtitle}
        </EuiText>
      ) : null}
    </div>
  )
}

export default LibraryHeading
