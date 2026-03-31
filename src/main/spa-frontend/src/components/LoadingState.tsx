import {
  EuiFlexGroup,
  EuiImage,
  EuiLoadingLogo,
  useEuiTheme,
} from '@elastic/eui'

import biblivreDark from '../../assets/biblivre-dark.svg'
import biblivreLight from '../../assets/biblivre-light.svg'

const LoadingState = () => {
  const { colorMode } = useEuiTheme()

  return (
    <EuiFlexGroup
      alignItems='center'
      direction='column'
      justifyContent='center'
    >
      <EuiLoadingLogo
        logo={() => (
          <EuiImage
            alt='Logo Biblivre'
            size='s'
            src={colorMode === 'DARK' ? biblivreDark : biblivreLight}
          />
        )}
        size='xl'
      />
    </EuiFlexGroup>
  )
}

export default LoadingState
