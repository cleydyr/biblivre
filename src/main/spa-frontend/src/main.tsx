import '@vitejs/plugin-react-swc/preamble'
import './moment/locale/pt-br'

import { EuiContext } from '@elastic/eui'
import flagsmith from '@flagsmith/flagsmith'
import { FlagsmithProvider } from '@flagsmith/flagsmith/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { IntlProvider } from 'react-intl'
import { BrowserRouter } from 'react-router-dom'

import messages from '../lang-compiled.json'

import {
  getFlagsmithApiUrl,
  getFlagsmithEnvironmentId,
} from './config/flagsmith-env.ts'
import AppWithTheme from './AppWithTheme.tsx'
import { i18n } from './i18n.ts'

// Create a client
const queryClient = new QueryClient()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <FlagsmithProvider
        flagsmith={flagsmith}
        options={{
          environmentID: getFlagsmithEnvironmentId(),
          api: getFlagsmithApiUrl(),
        }}
      >
        <IntlProvider locale='pt-BR' messages={messages}>
          <EuiContext i18n={i18n}>
            <QueryClientProvider client={queryClient}>
              <AppWithTheme />
              <ReactQueryDevtools initialIsOpen={false} />
            </QueryClientProvider>
          </EuiContext>
        </IntlProvider>
      </FlagsmithProvider>
    </BrowserRouter>
  </StrictMode>,
)
