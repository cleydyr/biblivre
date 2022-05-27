import React, { useState } from 'react';
import { EuiButton, EuiComboBox, EuiFlexGroup, EuiPageTemplate, EuiProvider, EuiSearchBar } from '@elastic/eui';
import '@elastic/eui/dist/eui_theme_light.css';
import { css, jsx } from '@emotion/react';

export default ({ button = <></>}) => {
  const [showBottomBar, setshowBottomBar] = useState(false);

  const style = css``;

  return (
    <EuiPageTemplate
      pageSideBar={<EuiComboBox></EuiComboBox>}
      bottomBar={showBottomBar ? 'Bottom bar' : undefined}
      pageHeader={{
        pageTitle: 'Biblivre 6',
        rightSideItems: [button],
        tabs: [
          { label: 'Tab 1', isSelected: true },
          {
            label: 'Tab 2',
            onClick: () => setshowBottomBar((showing) => !showing),
          },
        ],
      }}
    >
      <EuiFlexGroup>
        <EuiSearchBar></EuiSearchBar><EuiComboBox></EuiComboBox><EuiButton>Buscar</EuiButton>
      </EuiFlexGroup>
    </EuiPageTemplate>
  );
};