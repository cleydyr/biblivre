import './icons.ts'

import { EuiProvider } from '@elastic/eui'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'

import App from './App.tsx'

// Create a client
const queryClient = new QueryClient()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <EuiProvider>
      <QueryClientProvider client={queryClient}>
        <App />
      </QueryClientProvider>
    </EuiProvider>
  </StrictMode>
)
