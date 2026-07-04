import { EuiPageTemplate, useEuiTheme } from '@elastic/eui'

import type { FC, ReactNode } from 'react'

type PageTemplateProps = {
  pageTitle: ReactNode
  children: ReactNode
}

const PageTemplate: FC<PageTemplateProps> = ({ pageTitle, children }) => {
  const { euiTheme } = useEuiTheme()

  return (
    <EuiPageTemplate
      grow={false}
      paddingSize='xl'
      restrictWidth={euiTheme.breakpoint.l}
    >
      <EuiPageTemplate.Header pageTitle={pageTitle} />

      <EuiPageTemplate.Section>{children}</EuiPageTemplate.Section>
    </EuiPageTemplate>
  )
}

export default PageTemplate
