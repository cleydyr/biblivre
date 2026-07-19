import { EuiSpacer, EuiTabbedContent } from '@elastic/eui'
import { Fragment, useState } from 'react'

import type { EuiSpacerProps, EuiTabbedContentProps } from '@elastic/eui'
import type { FC } from 'react'

type Props = EuiTabbedContentProps & {
  spacing?: EuiSpacerProps['size']
  initiallySelectedTabId?: string
}

const SpacedEuiTabbedContent: FC<Props> = ({
  spacing,
  initiallySelectedTabId,
  ...props
}) => {
  const { tabs } = props

  const [selectedTabId, setSelectedTabId] = useState<string | undefined>(
    initiallySelectedTabId ?? tabs[0]?.id,
  )

  if (spacing === undefined) {
    const selectedTab = tabs.find((tab) => tab.id === selectedTabId) ?? tabs[0]

    return <EuiTabbedContent {...props} selectedTab={selectedTab} />
  }

  const newTabs = tabs.map((tab) => ({
    ...tab,
    content: (
      <Fragment>
        <EuiSpacer size={spacing} />
        {tab.content}
      </Fragment>
    ),
  }))

  const selectedTab =
    newTabs.find((tab) => tab.id === initiallySelectedTabId) ?? newTabs[0]

  /*
    <EuiTabbedContent
        initialSelectedTab={tabs[0]}
        selectedTab={selectedTab}
        tabs={tabs}
        onTabClick={(tab) => setSelectedTabId(tab.id as CirculationUserTab)}
      />
    */
  return (
    <EuiTabbedContent
      {...props}
      initialSelectedTab={selectedTab}
      tabs={newTabs}
      onTabClick={(tab) => setSelectedTabId(tab.id)}
    />
  )
}

export default SpacedEuiTabbedContent
