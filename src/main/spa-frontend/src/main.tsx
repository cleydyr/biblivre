import './icons.ts'
import './moment/locale/pt-br'

import { EuiContext, EuiErrorBoundary } from '@elastic/eui'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { IntlProvider } from 'react-intl'
import { BrowserRouter } from 'react-router-dom'

import messages from '../lang-compiled.json'

import AppWithTheme from './AppWithTheme.tsx'
import { i18n } from './i18n.ts'

// Create a client
const queryClient = new QueryClient()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <IntlProvider locale='pt-BR' messages={messages}>
        <EuiContext i18n={i18n}>
          <QueryClientProvider client={queryClient}>
            <EuiErrorBoundary>
              <AppWithTheme />
            </EuiErrorBoundary>
            <ReactQueryDevtools initialIsOpen={false} />
          </QueryClientProvider>
        </EuiContext>
      </IntlProvider>
    </BrowserRouter>
  </StrictMode>,
)
