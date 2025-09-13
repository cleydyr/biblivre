import './icons.ts'

import { EuiContext, EuiProvider } from '@elastic/eui'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { IntlProvider } from 'react-intl'
import { BrowserRouter } from 'react-router-dom'

import App from './App.tsx'
import { i18n } from './i18n.ts'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'

import './moment/locale/pt-br'

// Create a client
const queryClient = new QueryClient()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <IntlProvider locale='pt-BR'>
        <EuiProvider colorMode='light'>
          <EuiContext i18n={i18n}>
            <QueryClientProvider client={queryClient}>
              <App />
              <ReactQueryDevtools initialIsOpen={false} />
            </QueryClientProvider>
          </EuiContext>
        </EuiProvider>
      </IntlProvider>
    </BrowserRouter>
  </StrictMode>,
)
