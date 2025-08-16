import { useEffect, useState } from 'react';
import {
  EuiPageTemplate,
  EuiProvider,
  EuiSideNav,
  EuiText,
  EuiPanel,
  EuiSpacer,
  EuiTitle,
  EuiButton,
  EuiFlexGroup,
  EuiFlexItem,
  EuiImage,
} from '@elastic/eui';
import type { EuiPageTemplateProps, EuiSideNavItemType } from '@elastic/eui';

const panelled: EuiPageTemplateProps['panelled'] = undefined;

const App = () => {
  useEffect(() => {
    fetch('http://localhost:8090/', {
      headers: {
        accept: 'application/json, text/javascript, */*; q=0.01',
        'content-type': 'application/x-www-form-urlencoded; charset=UTF-8',
      },
      body: 'controller=json&module=cataloging.bibliographic&action=search&search_parameters=%7B%22database%22%3A%22main%22%2C%22material_type%22%3A%22all%22%2C%22search_mode%22%3A%22list_all%22%7D',
      method: 'POST',
      mode: 'cors',
      credentials: 'include',
    });
  });
  const [selectedItemName, setSelectedItemName] = useState('custom-reports');

  const sideNavItems: EuiSideNavItemType<unknown>[] = [
    {
      name: 'Custom Reports',
      id: 'custom-reports',
      onClick: () => setSelectedItemName('custom-reports'),
      isSelected: selectedItemName === 'custom-reports',
    },
    {
      name: 'Export',
      id: 'export',
      onClick: () => setSelectedItemName('export'),
      isSelected: selectedItemName === 'export',
    },
  ];

  const renderContent = () => {
    switch (selectedItemName) {
      case 'custom-reports':
        return (
          <EuiPanel paddingSize='l'>
            <EuiTitle size='l'>
              <h2>Custom Reports</h2>
            </EuiTitle>
            <EuiSpacer size='m' />
            <EuiText>
              <p>
                Create and manage your custom reports here. You can build
                personalized reports based on your specific requirements.
              </p>
            </EuiText>
            <EuiSpacer size='l' />
            <EuiFlexGroup gutterSize='s'>
              <EuiFlexItem grow={false}>
                <EuiButton fill>Create New Report</EuiButton>
              </EuiFlexItem>
              <EuiFlexItem grow={false}>
                <EuiButton>View Existing Reports</EuiButton>
              </EuiFlexItem>
            </EuiFlexGroup>
          </EuiPanel>
        );
      case 'export':
        return (
          <EuiPanel paddingSize='l'>
            <EuiTitle size='l'>
              <h2>Export</h2>
            </EuiTitle>
            <EuiSpacer size='m' />
            <EuiText>
              <p>
                Export your data in various formats. Choose from multiple export
                options to get your data in the format you need.
              </p>
            </EuiText>
            <EuiSpacer size='l' />
            <EuiFlexGroup gutterSize='s'>
              <EuiFlexItem grow={false}>
                <EuiButton fill>Export to CSV</EuiButton>
              </EuiFlexItem>
              <EuiFlexItem grow={false}>
                <EuiButton>Export to PDF</EuiButton>
              </EuiFlexItem>
              <EuiFlexItem grow={false}>
                <EuiButton>Export to Excel</EuiButton>
              </EuiFlexItem>
            </EuiFlexGroup>
          </EuiPanel>
        );
      default:
        return null;
    }
  };

  return (
    <EuiProvider>
      <EuiPageTemplate grow={false} offset={0} panelled={panelled}>
        <EuiPageTemplate.Header pageTitle='Biblivre'>
          <EuiImage src='http://localhost:8090/static/images/logo_biblivre.png' />
        </EuiPageTemplate.Header>
        <EuiPageTemplate.Sidebar>
          <EuiSideNav
            aria-label='Main navigation'
            mobileTitle='Navigate'
            toggleOpenOnMobile={() => {}}
            isOpenOnMobile={true}
            items={sideNavItems}
            style={{ width: 192 }}
          />
        </EuiPageTemplate.Sidebar>
        <EuiPageTemplate.Section>{renderContent()}</EuiPageTemplate.Section>
      </EuiPageTemplate>
    </EuiProvider>
  );
};

export default App;
