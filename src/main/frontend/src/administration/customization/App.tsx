import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { EuiProvider } from "@elastic/eui";
import React from "react";

import "@elastic/eui/dist/eui_theme_light.css";
import FormCustomization from "./FormCustomization";
import { IntlProvider } from "react-intl";

const queryClient = new QueryClient();

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <EuiProvider colorMode="light">
        <IntlProvider locale="pt-BR">
          <FormCustomization />
        </IntlProvider>
      </EuiProvider>
    </QueryClientProvider>
  );
};

export default App;
