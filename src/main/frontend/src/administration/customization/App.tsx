import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { EuiProvider } from "@elastic/eui";
import React, { useEffect, useState } from "react";

import "@elastic/eui/dist/eui_theme_light.css";
import FormCustomization from "./FormCustomization";
import { IntlProvider } from "react-intl";

const queryClient = new QueryClient();

const App: React.FC = () => {
  const [messages, setMessages] = useState({});

  useEffect(() => {
    async function loadLocaleData() {
      const compiledMessages = await import(
        "../../../compiled-lang/pt-BR.json"
      );

      setMessages(compiledMessages);
    }

    loadLocaleData();
  }, []);
  return (
    <QueryClientProvider client={queryClient}>
      <EuiProvider colorMode="light">
        <IntlProvider locale="pt-BR" messages={messages}>
          <FormCustomization />
        </IntlProvider>
      </EuiProvider>
    </QueryClientProvider>
  );
};

export default App;