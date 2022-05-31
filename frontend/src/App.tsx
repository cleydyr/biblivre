import React, { useState } from 'react';
import { EuiAccordion, EuiBreadcrumbs, EuiButton, EuiComboBox, EuiFlexGroup, EuiPageTemplate, EuiPanel, EuiProvider, EuiSearchBar, EuiSideNav } from '@elastic/eui';
import '@elastic/eui/dist/eui_theme_light.css';
import { css, jsx } from '@emotion/react';

export default ({ button = <EuiButton>Sair</EuiButton> }) => {
  const [showBottomBar, setshowBottomBar] = useState(false);

  const style = css``;

  const noop = () => { };

  return (
    <EuiPageTemplate
      pageSideBar={
        <EuiSideNav
          heading="Biblivre 6"
          mobileTitle="Nav Items"
          toggleOpenOnMobile={noop}
          isOpenOnMobile={false}
          items={[
            {
              name: 'Circulação',
              id: '1',
              items: [
                {
                  name: 'Children only',
                  id: '1.1',
                  items: [
                    {
                      name: 'General',
                      id: '1.1.1',
                    },
                    {
                      name: 'Timelion',
                      id: '1.1.2',
                    },
                  ],
                },
                {
                  name: 'Cadastro de usuários',
                  id: '1',
                  itens: [
                    {
                      name: 'foo0',
                      id: '1',
                      forceOpen: true
                    }
                  ]
                },
                {
                  name: 'Empréstimos e devoluções',
                  id: '2'
                },
                {
                  name: 'Reservas',
                  id: '3'
                },
                {
                  name: 'Controle de acesso',
                  id: '1'
                },
                {
                  name: 'Impressão de carteirinhas',
                  id: '1'
                },
              ],
            },
            {
              name: 'Circulação',
              id: '1',
              items: [
                {
                  name: 'Cadastro de usuários',
                  id: '1',
                  itens: [
                    {
                      name: 'foo0',
                      id: '1',
                      forceOpen: true
                    }
                  ]
                },
                {
                  name: 'Empréstimos e devoluções',
                  id: '2'
                },
                {
                  name: 'Reservas',
                  id: '3'
                },
                {
                  name: 'Controle de acesso',
                  id: '1'
                },
                {
                  name: 'Impressão de carteirinhas',
                  id: '1'
                },
              ],
            },
          ]}
        />
      }
      bottomBar={showBottomBar ? 'Bottom bar' : undefined}
      pageHeader={{
        breadcrumbs: [
          {
            text: 'Circulação',
            href: '#',
            onClick: (e) => e.preventDefault(),
          },
          {
            text: 'Cartões de acesso',
            href: '#',
            onClick: (e) => e.preventDefault(),
          },
        ],
        pageTitle: 'Cartões de acesso',
        rightSideItems: [<EuiButton>Adicionar cartão</EuiButton>],
      }}
    >
      <div>
        <EuiAccordion id="foobar" buttonContent="Click me to toggle">
          <EuiPanel color="subdued">
            Any content inside of <strong>EuiAccordion</strong> will appear here.
          </EuiPanel>
        </EuiAccordion>
      </div>
      <EuiFlexGroup>
        <EuiSearchBar></EuiSearchBar><EuiComboBox></EuiComboBox><EuiButton>Buscar</EuiButton>
      </EuiFlexGroup>
    </EuiPageTemplate>
  );
};